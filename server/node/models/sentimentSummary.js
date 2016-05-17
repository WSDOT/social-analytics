// Sentiment summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var SentimentSummarySchema = new Schema({
    _id: String,
    value: Number
    },
    {collection: 'sentiment_summary'});

module.exports = mongoose.model('SentimentSummary', SentimentSummarySchema);
