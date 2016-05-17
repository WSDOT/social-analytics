// Twitter Status model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var StatusSchema = new Schema({});

module.exports = mongoose.model('statuses', StatusSchema);
