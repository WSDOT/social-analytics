/*
 * Copyright (c) 2016 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

/* Calls using the model methods have a different synxtax than the Mongo CLI.
 *
 * app.get('/foo', function(req, res) {
 *        // Model.find({query}, [fields], {options}, callback)
 *        Mentions.find({}, ['created_at', 'text'], { sort:{'id': -1}, limit: 3 }, function(err, results) {
 *                res.send(results);
 *        });
 * });
*/

// The API controller for mentions
var mongoose = require('mongoose');

var Statuses = require('../models/status.js');
var Mentions = require('../models/mention.js');
var SourceSummary = require('../models/sourceSummary.js');
var SentimentSummary = require('../models/sentimentSummary.js');

exports.sourceFromToDate = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
        end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
        start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }
    
    if (screenName == "all") {
        var command = {
            query: {'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                var src = this.source;
                var re = new RegExp("(>)(.*)(<)", "");
                var matched = src.match(re);
		emit(matched[2], 1);
            },
            reduce: function(key, values) {
                var sum = 0;
                for (index in values) {
                    sum += values[index];
                }
                return sum;
            },
            out: "source_summary"
        };
    } else {
        var command = {
            query: {'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                var src = this.source;
                var re = new RegExp("(>)(.*)(<)", "");
                var matched = src.match(re);
                emit(matched[2], 1);
            },
            reduce: function(key, values) {
                var sum = 0;
                for (index in values) {
                    sum += values[index];
                }
                return sum;
            },
            out: "source_summary"
        }; 
    }

    Mentions.mapReduce(command, function(err, results) {
	SourceSummary.find({},
			    null,
			    {sort: {'value': -1}, limit: 10},
			    function(err, results) {
				if(err) throw err;
				res.jsonp(results);
			    }
			   );
    });
}

exports.sentimentFromToDate = function(req, res) {

    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
        end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
        start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }

    if (screenName == "all") {
        var command = {
            query: {'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                emit(this.sentiment, 1);
            },
            reduce: function(key, values) {
                var sum = 0;
                for (index in values) {
                    sum += values[index];
                }
                return sum;
            },
            out: "sentiment_summary"
        };
    } else {
        var command = {
            query: {'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                emit(this.sentiment, 1);
            },
            reduce: function(key, values) {
                var sum = 0;
                for (index in values) {
                    sum += values[index];
                }
                return sum;
            },
            out: "sentiment_summary"
        };
    }

    Mentions.mapReduce(command, function(err, results) {
	if(err) throw err;
	SentimentSummary.find({},
			    null,
			    {sort: {'value': -1}},
			    function(err, results) {
				if(err) throw err;
				res.jsonp(results);
			    }
			   );
    });
}

exports.sentimentEdit = function(req, res) {
    var sentiment = req.params.sentiment;

    Mentions.findOne({'id': req.params.id}, function(err, results) {
        if (results) {
            results.sentiment = sentiment;
            results.save(function(err){
                if (!err) {
                    res.jsonp(results);
                } else {
                    res.jsonp(null);
                }
            });
        } else {
            res.jsonp(results);
        }
    });
}

exports.positiveFromToDate = function(req, res) {

    var itemsPerPage = 25;
    var pageNum = req.params.page;
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
        end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
        start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }        

    if (screenName == "all") {
        Mentions.find({'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'positive'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
    } else {
        Mentions.find({'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'positive'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
    }
}

exports.negativeFromToDate = function(req, res) {
    var itemsPerPage = 25;
    var pageNum = req.params.page;
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
        end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
        start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }

    if (screenName == "all") {
        Mentions.find({'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'negative'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
    } else {

        Mentions.find({'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'negative'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage}, 
                      function(err, results) {
                          if (err){ return res.jsonp(err);} 
                          return res.jsonp(results);
                      });
    }
}

exports.neutralFromToDate = function(req, res) {
    var itemsPerPage = 25;
    var pageNum = req.params.page;
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
        end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
        start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }

    if (screenName == "all") {
        Mentions.find({'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'neutral'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
    } else {
        Mentions.find({'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}, 'sentiment': 'neutral'},
                      null,
                      {sort:{'id': -1},
                       skip: (itemsPerPage * (pageNum - 1)),
                       limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
    }
}