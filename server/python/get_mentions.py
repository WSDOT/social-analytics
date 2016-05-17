#!/usr/bin/env python
#
# Copyright (c) 2016 Washington State Department of Transportation
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>
#

from datetime import datetime
from dateutil import parser as dateparser
from nltk.classify import NaiveBayesClassifier
from nltk.corpus.reader import WordListCorpusReader
from tokenizer import Tokenizer
from twitter import *

import json
import nltk.classify.util
import pymongo
import re
import urllib
import urllib2

from_user_ban = []

def word_feats(words):
    return dict([(word, True) for word in words])

def classify_tweet(text):
    cutoff = 0.71
    ret = classifier.prob_classify(word_feats(Tokenizer().tokenize(text)))
    p_score = ret.prob("positive")
    n_score = ret.prob("negative")

    if max(p_score, n_score) <= cutoff:
        return "neutral"

    if p_score > n_score:
        return "positive"

    elif n_score > p_score:
        return "negative"

    else:
        return "neutral"


reader = WordListCorpusReader('/path/to/sentiment/files', ['positive.txt', 'negative.txt'])

pos_feats = [(dict([(word, True)]), 'positive') for word in reader.words('positive.txt')]
neg_feats = [(dict([(word, True)]), 'negative') for word in reader.words('negative.txt')]
train_feats = pos_feats + neg_feats
classifier = NaiveBayesClassifier.train(train_feats)

t = Twitter(auth=OAuth(
        "TOKEN",
        "TOKEN_KEY",
        "CON_SECRET",
        "CON_SECRET_KEY")
        )

connection = pymongo.Connection()
db = connection.twitter
mentions = db.mentions

screen_names = ["YOUR_ACCOUNT",
                "YOUR_OTHER_ACCOUNT"]

re_RT = re.compile(("(RT\s?@YOUR_ACCOUNT|"
                    "RT\s?@YOUR_OTHER_ACCOUNT)"), re.UNICODE|re.IGNORECASE)

for screen_name in screen_names:
    try:
        since_id = mentions.find({'entities.user_mentions.screen_name': re.compile('^%s$' % screen_name, re.IGNORECASE)}).sort("id", pymongo.DESCENDING).limit(1)[0]["id"]
        tweets = t.search.tweets(
               q="@%s" % screen_name,
               result_type="recent",
               count="100",
               include_entities="true",
               since_id="%s" % since_id
               )

    except IndexError:
        tweets = t.search.tweets(
               q="@%s" % screen_name,
               result_type="recent",
               count="100",
               include_entities="true"
               )

    for tweet in tweets['statuses']:
        if tweet['user']['screen_name'] in from_user_ban:
            continue

        p = re_RT.search(tweet['text'])

        if not p:
            tweet['sentiment'] = classify_tweet(tweet['text'])
        else:
            tweet['sentiment'] = 'neutral'

        tweet['created_at'] = dateparser.parse(tweet['created_at'])
        mentions.insert(tweet)

    print("Imported and classified, %s mentions from %s account." % (len(tweets['statuses']), screen_name))
