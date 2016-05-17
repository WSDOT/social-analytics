// Follower summary model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var FollowerSummarySchema = new Schema({
    _id: Number,
    value: Number
    },
    {collection: 'followers'});

module.exports = mongoose.model('FollowerSummary', FollowerSummarySchema);
