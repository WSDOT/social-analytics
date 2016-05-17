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
from time import sleep
from twitter import *

import json
import pymongo
import urllib2

t = Twitter(auth=OAuth(
        "TOKEN",
        "TOKEN_KEY",
        "CON_SECRET",
        "CON_SECRET_KEY")
        )

connection = pymongo.Connection()
db = connection.twitter
statuses = db.statuses

screen_names = ["YOUR ACCOUNTS HERE"]

# Twitter
for screen_name in screen_names:
    try:
        since_id = statuses.find({'user.screen_name': screen_name}).sort("id", pymongo.DESCENDING).limit(1)[0]["id"]
        tweets = t.statuses.user_timeline(
               screen_name="%s" % screen_name,
               include_entities="true",
               include_rts="true",
               count="200",
               since_id="%s" % since_id
               )

    except IndexError:
        tweets = t.statuses.user_timeline(
               screen_name="%s" % screen_name,
               include_entities="true",
               include_rts="true",
               count="200"
               )

    for tweet in tweets:
        tweet['created_at'] = dateparser.parse(tweet['created_at'])
        statuses.insert(tweet)

    print("Imported, %s status updates from %s account." % (len(tweets), screen_name,))
