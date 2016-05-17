// Source summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var SourceSummarySchema = new Schema({
    _id: String,
    value: Number
    },
    {collection: 'source_summary'});

module.exports = mongoose.model('SourceSummary', SourceSummarySchema);
