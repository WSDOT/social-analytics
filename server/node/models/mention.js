// Twitter Mention model

var mongoose = require("mongoose")
    ,Schema = mongoose.Schema
    ,ObjectId = Schema.ObjectId;

var MentionSchema = new Schema({
    _id: ObjectId,
    text: String,
    profile_image_url: String,
    to_user_id_str: String,
    from_user: String,
    from_user_id: Number,
    to_user_id: Number,
    geo: String,
    id: Number,
    iso_language_code: String,
    from_user_id_str: String,
    sentiment: {type: String, enum: ['negative', 'neutral', 'positive'] },
    source: String,
    id_str: String,
    created_at: Date,
    metadata: {result_type: String}
});

module.exports = mongoose.model('mentions', MentionSchema);