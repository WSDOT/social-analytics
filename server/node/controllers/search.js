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
// The API controller for type-ahead search

/* Calls to the model methods has a different synxtax than the Mongo CLI.
 *
 * app.get('/foo', function(req, res) {
 *        // Model.find({query}, [fields], {options}, callback)
 *        Mentions.find({}, ['created_at', 'text'], { sort:[['id', -1]], limit: 3 }, function(err, results) {
 *                res.send(results);
 *        });
 * });
*/
var mongoose = require('mongoose');

var Statuses = require('../models/status.js');
var Mentions = require('../models/mention.js');
var json2csv = require('json2csv');
var csv = require('csv');

/*
exports.suggest = function(req, res) {
    var text = req.params.text;
    var re = new RegExp(text, 'i');
    String.prototype.trunc = function(m, n) {
        return this.substr(m, n - 1)
    };
    mongoose.connection.db.collection('mentions', function(err, collection) {
        collection.find({
            'text': re
        }).sort({
            'id': -1
        }).limit(20).toArray(function(err, results) {
            var arr = [];
            for (r in results) {
                var i = results[r].text.search(re);
                arr.push(results[r].text.trunc(i, 45));
            }
            res.jsonp(arr);
        });
    });
}
*/


exports.advSearch = function(req, res) {

    var itemsPerPage = 25;
    var pageNum = req.params.page;
    var collection = req.params.collection
    var text = req.params.text;
    var screenName = req.params.screenName;
    var re = new RegExp(text, 'i');
    var media = parseInt(req.params.media);

    if (req.params.fromDat == 0) {
        var start = new Date(0, 0, 0);
    } else {
        var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, req.params.fromDay);
    }

    if (req.params.toDay == 0) {
        var end = new Date();
    } else {
        var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay) + 1);
    }

    if (collection.toLowerCase() === 'mentions') {

        Mentions.find({
                'text': re,
                'entities.user_mentions.screen_name': {
                    $regex: ((screenName.toLowerCase() === 'all') ? '.*' : '^' + screenName + '$'),
                    $options: 'i'
                },
                'created_at': {
                    '$gte': start,
                    '$lte': end
                },
                $or:[
                   {'entities.media.media_url' : {$exists: media}},
                   {'entities.media.media_url' : {$exists: true}}
                ]
            },
            null, {
                sort: {
                    'id': -1
                },
                text: 1,
                id: 1,
                created_at: 1,
                skip: (itemsPerPage * (pageNum - 1)),
                limit: itemsPerPage
            },
            function(err, results) {
                res.jsonp(results);
            });

    } else {
        Statuses.find({
                'text': re,
                'user.screen_name': {
                    $regex: ((screenName.toLowerCase() === 'all') ? '.' : '^' + screenName + '$'),
                    $options: 'i'
                },
                'created_at': {
                    '$gte': start,
                    '$lte': end
                },
                $or:[
                   {'entities.media.media_url' : {$exists: media}},
                   {'entities.media.media_url' : {$exists: true}}
                ]
            },
            null, {
                sort: {
                    'id': -1
                },
                text: 1,
                id: 1,
                created_at: 1,
                skip: (itemsPerPage * (pageNum - 1)),
                limit: itemsPerPage
            },
            function(err, results) {
                res.jsonp(results);
            });
    }

}



exports.exportSearch = function(req, res) {

    var collection = req.params.collection
    var text = req.params.text;
    var screenName = req.params.screenName;
    var re = new RegExp(text, 'i');

    var fileName = 'search_' + text.replace(/ /g, '') + '_' + req.params.fromYear + '_' + req.params.fromMonth + '_' + req.params.fromDay 	+ '-' + req.params.toYear + '_' + req.params.toMonth + '_' + req.params.toDay + '.csv';
    if (req.params.fromDat == 0) {
        var start = new Date(0, 0, 0);
    } else {
        var start = new Date(req.params.fromYear, parseInt(req.params.fromMonth) - 1, req.params.fromDay);
    }

    if (req.params.toDay == 0) {
        var end = new Date();
    } else {
        var end = new Date(req.params.toYear, parseInt(req.params.toMonth) - 1, parseInt(req.params.toDay) + 1);
    }

    if (collection.toLowerCase() === 'mentions') {

        mongoose.connection.db.collection('mentions', function(err, collection) {
            collection.find({
                $and: [{
                    'text': re
                }, {
                    'created_at': {
                        '$gte': start,
                        '$lte': end
                    }
                }, {
                    'entities.user_mentions.screen_name': {
                        $regex: ((screenName.toLowerCase() === 'all') ? '.*' : '^' + screenName + '$')
                    }
                }]
            }, {
                text: 1,
                id: 1,
                created_at: 1
            }).sort({
                'id': -1
            }).toArray(function(err, results) {
                json2csv({
                    data: results,
                    fields: ['id', 'id_str', 'text', 'created_at']
                }, function(err, csvData) {
                    if (err) console.log(err);
                    res.setHeader('Content-disposition', 'attachment; filename=' + fileName);
                    res.set('Content-Type', 'text/csv');
                    res.charset = 'utf-8';
                    res.status(200).send(csvData);
                });

            });
        });
    } else {
        mongoose.connection.db.collection('statuses', function(err, collection) {
            collection.find({
                $and: [{
                    'text': re
                }, {
                    'created_at': {
                        '$gte': start,
                        '$lte': end
                    }
                }, {
                    'user.screen_name': {
                        $regex: ((screenName.toLowerCase() === 'all') ? '.*' : '^' + screenName + '$')
                    }
                }]
            }, {
                text: 1,
                id: 1,
                created_at: 1
            }).sort({
                'id': -1
            }).toArray(function(err, results) {

                json2csv({
                    data: results,
                    fields: ['id', 'text', 'created_at']
                }, function(err, csvData) {
                    if (err) console.log(err);
                    res.setHeader('Content-disposition', 'attachment; filename=' + fileName);
                    res.set('Content-Type', 'text/csv');
                    res.charset = 'utf-8';
                    res.status(200).send(csvData);
                });

            });
        });
    }
}
