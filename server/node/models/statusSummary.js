// Status summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var StatusSummarySchema = new Schema({
    _id: Number,
    value: Number
    },
    {collection: 'status_summary'});

module.exports = mongoose.model('StatusSummary', StatusSummarySchema);
