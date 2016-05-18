Social Analytics
================

A social analytics dashboard for twitter.

Features
========

* Tweet and Mention counts
* Follower statistics
* Tweet search and export
* Sentiment analytics
* Show popular/unpopular tweets
* Tweet Source data

Setup
=====

1. Install [maven.](https://maven.apache.org/)
2. Run `mvn install` in the project directory.
3. Follow this guide on [deplying a GWT app to a web server](http://www.gwtproject.org/doc/latest/DevGuideDeploying.html#DevGuideDeployingWebServer)
4. Set up server files found in `server/`

*NOTE:* Make sure to change `HOST_URL` in `Conts.java`, as well as adding your twitter accounts in the source (`AnalyticsViewImpl.java`, `AnalyticsViewImpl.ui.xml` and `AdvSearchView.ui.xml`).

License
=======

[GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0.txt)
