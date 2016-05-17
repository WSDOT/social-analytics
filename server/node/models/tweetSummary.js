// Tweet summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var TweetSummarySchema = new Schema({
    _id: Number,
    value: Schema.Types.Mixed
    },
    {collection: 'tweet_summary'});

module.exports = mongoose.model('TweetSummary', TweetSummarySchema);
