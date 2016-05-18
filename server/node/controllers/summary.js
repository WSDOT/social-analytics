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

/* Calls using the model methods have a different syntax than the Mongo CLI.
 *
 * app.get('/foo', function(req, res) {
 *        // Model.find({query}, [fields], {options}, callback)
 *        Mentions.find({}, ['created_at', 'text'], { sort:{'id': -1}, limit: 3 }, function(err, results) {
 *                res.send(results);
 *        });
 * });
 */

// The API controller for summary
var mongoose = require('mongoose');

var Statuses = require('../models/status.js');
var Mentions = require('../models/mention.js');
var TweetSummary = require('../models/tweetSummary.js');
var MentionSummary = require('../models/mentionSummary.js');
var FollowerSummary = require('../models/followerSummary.js');
var StatusSummary = require('../models/statusSummary.js');

exports.tweetsAndMentions = function(req, res) {
    var screenName = 'wsdot'; // Default screenName
    var now = new Date();
    var today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    var two_weeks_ago = new Date(today - 60 * 60 * 24 * 14 * 1000);

    var r = function(key, values) {
        var result = {mentions: 0, statuses: 0};
        values.forEach(function(value) {
            result.mentions += (value.mentions !== null) ? value.mentions : 0;
            result.statuses += (value.statuses !== null) ? value.statuses : 0;
        });
        return result;
    };

    var mentions_command = {
        query: {'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': two_weeks_ago}},
        map: function() {
            day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
            emit(day.getTime(), {mentions: 1, statuses: 0});               
        },
        reduce: r,
        out: {reduce: "tweet_summary"}
    };

    var statuses_command = {
        query: {'user.screen_name': screenName, 'created_at': {'$gte': two_weeks_ago}},
        map: function() {
            day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
            emit(day.getTime(), {mentions: 0, statuses: 1});               
        },
        reduce: r,
        out: {reduce: "tweet_summary"}
    };

    // drop the map reduced table and generate a new one.
    mongoose.connection.db.collection('tweet_summary', function(err, collection) {
        collection.drop();
	if(err) throw err;
        Mentions.mapReduce(mentions_command, function(err, results) {
	    if(err) throw err;
	    Statuses.mapReduce(statuses_command, function(err, results) {
		if(err) throw err;
		TweetSummary.find({},
				  null,
				  {sort: {'_id': 1}},
				  function(err, results) {
				      if(err) throw err;
				      res.jsonp(results)
				  }
				 );
	    });
	});
    });
}

exports.tweetsAndMentionsfromToDate = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 0, 0, 0);
    var end = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 23, 59, 59);

    if (start > end) {
	end = new Date(req.params.fromYear, req.params.fromMonth - 1, req.params.fromDay, 23, 59, 59);
	start = new Date(req.params.toYear, req.params.toMonth - 1, req.params.toDay, 0, 0, 0);
    }

    var r = function(key, values) {
	var result = {mentions: 0, statuses: 0};
	values.forEach(function(value) {
	    result.mentions += (value.mentions !== null) ? value.mentions : 0;
	    result.statuses += (value.statuses !== null) ? value.statuses : 0;
        });
	return result;
    };

    if (screenName == "all") {
        var mentions_command = {
            query: {'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), {mentions: 1, statuses: 0});
            },
            reduce: r,
            out: {reduce: "tweet_summary"}
        };
    } else {
        var mentions_command = {
            query: {'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), {mentions: 1, statuses: 0});
            },
            reduce: r,
            out: {reduce: "tweet_summary"}
        };
    }

    if (screenName == "all") {
        var statuses_command = {
            query: {'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), {mentions: 0, statuses: 1});
            },
            reduce: r,
            out: {reduce: "tweet_summary"}
        };
    } else {
        var statuses_command = {
            query: {'user.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), {mentions: 0, statuses: 1});
            },
            reduce: r,
            out: {reduce: "tweet_summary"}
        };
    }

    // drop the map reduced table and generate a new one.
    mongoose.connection.db.collection('tweet_summary', function(err, collection) {
        collection.drop();
        if(err) throw err;
        Mentions.mapReduce(mentions_command, function(err, results) {
            if(err) throw err;
            Statuses.mapReduce(statuses_command, function(err, results) {
                if(err) throw err;
                TweetSummary.find({},
                                  null,
                                  {sort: {'_id': 1}},
                                  function(err, results) {
                                      if(err) throw err;
                                      res.jsonp(results)
                                  }
                                 );
            });
        });
    });
}

exports.mentionsFromToDate = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, parseInt(req.params.fromDay), 0, 0, 0);
    var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay), 23, 59, 59);
    var pageNum = req.params.page;
    var itemsPerPage = 25;
    
    // TODO: check count b/c query hangs when limit is greater than the returned results. not sure why.
    // The sentiment results work fine and use similar queries.

    if (screenName == "all") {
      Mentions.count({'created_at': {'$gte': start, '$lte': end}}, function(err, count){

	if (count != 0){
        if ((count - ((pageNum-1) * itemsPerPage)) < itemsPerPage){
          itemsPerPage = count - ((pageNum-1) * itemsPerPage);
          console.log(itemsPerPage);
        }

        Mentions.find({'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'id': -1}, skip: (itemsPerPage * (pageNum - 1)), limit: itemsPerPage},
                      function(err, results) {
                          res.jsonp(results);
                      });
        }else{
          res.jsonp({});
        }
      });
    } else {
      Mentions.count({'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}}, function(err, count){
       if (count != 0){
       if ((count - ((pageNum-1) * itemsPerPage)) < itemsPerPage){
          itemsPerPage = count - ((pageNum-1) * itemsPerPage);
          console.log(itemsPerPage);
       }

        Mentions.find({'entities.user_mentions.screen_name': screenName, 'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'id': -1}, skip: (itemsPerPage * (pageNum - 1)), limit: itemsPerPage},
                      function(err, results) {
                           if (err) {console.log("ERROR " + err); return err;}
                          return res.jsonp(results);
                      }).maxTime(30000);
       }else{
         res.jsonp({});
       }
      });
    }
}

exports.followersFromToDate = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, req.params.fromDay);
    var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay));

    if (start > end) {
        start = end;
        end = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, parseInt(req.params.fromDay) + 1);
    } else {
        end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay) + 1);
    }

    if (screenName == "all") {
        var command = {
            query: {'created_at': {'$gte': start, '$lt': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), this.user.followers_count);
            },
            reduce: function(key, values){ return Math.max.apply(Math, values) },
            out: "followers"
        };
    } else {
        var command = {
            query: {'user.screen_name': screenName, 'created_at': {'$gte': start, '$lt': end}},
            map: function() {
                day = new Date(this.created_at.getFullYear(), this.created_at.getMonth(), this.created_at.getDate());
                emit(day.getTime(), this.user.followers_count);
            },
            reduce: function(key, values){ return Math.max.apply(Math, values) },
            out: "followers"
        };
    }

    Statuses.mapReduce(command, function(err, results) {
        if(err) throw err;
        FollowerSummary.find({},
                             null,
                             {sort: {'_id': 1}},
                             function(err, results) {
                                 if(err) throw err;
                                 res.jsonp(results);
                             }
                            );
    });
}

exports.likesRanking = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, req.params.fromDay);
    var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay) + 1);
    var type = req.params.type;

    if (screenName == "all") {
        Statuses.find({'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'favorite_count': (type == 'best' ? -1 : 1)}, limit: 5},
                      function(err, results) {
                          res.jsonp(results);
                      });
    } else {
        Statuses.find({'user.screen_name': screenName ,'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'favorite_count': (type == 'best' ? -1 : 1)}, limit:5},
                      function(err, results) {
                          res.jsonp(results);
                      });
    }
}

exports.retweetRanking = function(req, res) {
    var screenName = req.params.screenName;
    var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, req.params.fromDay);
    var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay) + 1);
    var type = req.params.type;

    if (screenName == "all") {
        Statuses.find({'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'retweet_count': (type == 'best' ? -1 : 1)}, limit: 5},
                      function(err, results) {
                          res.jsonp(results);
                      });
    } else {
        Statuses.find({'user.screen_name': screenName ,'created_at': {'$gte': start, '$lte': end}},
                      null,
                      {sort:{'retweet_count': (type == 'best' ? -1 : 1)}, limit:5},
                      function(err, results) {
                          res.jsonp(results);
                      });
    }
}

