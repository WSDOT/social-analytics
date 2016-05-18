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

var express = require("express");
var mongoose = require("mongoose");
var app = express();

// Connect to MongoDB when the app initializes
mongoose.connect('mongodb://localhost/twitter');

// Setup the RESTful API. Handler methods are defined in summary.js controller.
var summary = require('./controllers/summary.js');

app.get('/summary', summary.tweetsAndMentions);
app.get('/summary/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay', summary.tweetsAndMentionsfromToDate);

app.get('/summary/mentions/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/:page', summary.mentionsFromToDate);

app.get('/summary/followers/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay', summary.followersFromToDate);

app.get('/summary/statuses/favorites/:type/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay', summary.likesRanking);
app.get('/summary/statuses/retweets/:type/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay', summary.retweetRanking);

// Handler methods are defined in mentions.js controller.
var mentions = require('./controllers/mentions.js');

// returns JSON of mention sources, if no date is given returns last two weeks/
app.get('/mentions/source/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay', mentions.sourceFromToDate);

// returns JSON of sentiment counts, if no date is given returns counts from last two weeks.
app.get('/mentions/sentiment/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/', mentions.sentimentFromToDate);

// Sets the sentiment of tweet with :id to :sentiment
app.get('/mentions/sentiment/edit/:id/:sentiment', mentions.sentimentEdit);

// returns JSON of tweets with a certain sentiment.
app.get('/mentions/positive/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/:page', mentions.positiveFromToDate);
app.get('/mentions/negative/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/:page', mentions.negativeFromToDate);
app.get('/mentions/neutral/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/:page', mentions.neutralFromToDate);

// Hander method is defined in search.js controller.
var search = require('./controllers/search.js');

app.get('/search/suggest/:text', search.suggest);

// Returns a list of tweets that match :text as JSON. If called without a page number will return CSV of all results. 
app.get('/search/:text/:screenName/:collection/:media/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/:page', search.advSearch);
app.get('/search/:text/:screenName/:collection/:media/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay/', search.exportSearch);

app.listen(3001);
console.log('Social Analytics app server running at http://127.0.0.1:3001/');
