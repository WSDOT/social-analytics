// Mention summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var MentionSummarySchema = new Schema({
    _id: Number,
    value: Number
    },
    {collection: 'summary'});

module.exports = mongoose.model('MentionSummary', MentionSummarySchema);
