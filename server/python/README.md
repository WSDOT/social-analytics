Social Analytics Python Tools
=============================

Python scripts for saving tweets from the [Twitter API](https://dev.twitter.com/overview/documentation) to a Mongo database.

Setup
=====
1. Install [MongoDB.](https://docs.mongodb.com/)
2. Create a database named `twitter` with collections 'mentions' and 'statuses'
3. Obtain [authentication tokens](https://dev.twitter.com/oauth/overview) for twitter
4. Add twitter tokens, accounts, and sentiment file paths to the scripts. 

Dependencies
============
* [PyMongo](https://github.com/mongodb/mongo-python-driver)
* [Python Twitter Tools](https://github.com/sixohsix/twitter)
* [Natural Language Toolkit](http://www.nltk.org/)

License
=======

[GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0.txt)
