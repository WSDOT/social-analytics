Social Analytics
================

A social analytics dashboard for twitter.

![alt tag](https://github.com/WSDOT/social-analytics/blob/cb86a4bff8bfd0a0a7fd9d1038680742613c601e/images/analyticsScreenShot.png)

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
3. Follow this guide on [deploying a GWT app to a web server](http://www.gwtproject.org/doc/latest/DevGuideDeploying.html#DevGuideDeployingWebServer)
4. Set up server files found in [`server/`](https://github.com/WSDOT/social-analytics/tree/master/server)

*NOTE:* Make sure to change `HOST_URL` and `DEFAULT_ACCOUNT` in [`Consts.java`](src/main/java/gov/wa/wsdot/apps/analytics/util/Consts.java), as well as adding your twitter accounts in the source files.
* [`AnalyticsViewImpl.java`](/src/main/java/gov/wa/wsdot/apps/analytics/client/activities/twitter/AnalyticsViewImpl.java)
* [`AnalyticsViewImpl.ui.xml`](src/main/java/gov/wa/wsdot/apps/analytics/client/activities/twitter/AnalyticsViewImpl.ui.xml)
* [`AdvSearchView.ui.xml`](src/main/java/gov/wa/wsdot/apps/analytics/client/activities/twitter/view/search/AdvSearchView.ui.xml)

License
=======

[GNU GPLv3](http://www.gnu.org/licenses/gpl-3.0.txt)
