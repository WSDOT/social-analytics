/*
 * Copyright (c) 2016 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.events.SentimentDisplayEvent;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweet.TweetView;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialPreLoader;

/**
 *  A widget for displaying a list of tweets from an account on a specific day.
 *  Requests tweets from server 10 at a time.
 *  Listens for the DateSubmitEvent
 */
public class TweetsView extends Composite {

    interface MyEventBinder extends EventBinder<TweetsView> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static TweetsViewUiBinder uiBinder = GWT
            .create(TweetsViewUiBinder.class);

    interface TweetsViewUiBinder extends UiBinder<Widget, TweetsView> {}

    final Resources res;

    @UiField
    static
    MaterialPreLoader tweetsLoader;

    @UiField
    static
    HTMLPanel tweetsList;

    @UiField
    static
    MaterialButton moreTweetsBtn;

    @UiField
    static
    MaterialButton backToTweetTopBtn;

    // Following 3 values used for loading more tweets
    private static String currentAccount = "wsdot";
    private static String startDate;
    private static String endDate;

    private static String currentUrl;

    private static int pageNum = 1;

    private static String defaultAccount = "wsdot";

    public TweetsView(EventBus eventBus) {

        res = GWT.create(Resources.class);
        res.css().ensureInjected();
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        pageNum = 1;
        currentAccount = event.getAccount();
        startDate = fmt.format(event.getStartDate());
        endDate = fmt.format(event.getEndDate());
        updateTweets(startDate, endDate, event.getAccount());
    }

    @EventHandler
    void onSentimentDisplay(SentimentDisplayEvent event){
        pageNum = 1;
        tweetsList.clear();
        moreTweetsBtn.setVisible(false);

        String url = Consts.HOST_URL + "/mentions/" + event.getSentiment() + "/" + currentAccount + startDate + endDate + "/";
        currentUrl = url;

        tweetsLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url + pageNum, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                tweetsLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    updateTweetsList(mention.getMentions());
                    tweetsLoader.setVisible(false);
                }
            }
        });
    }


    /**
     * Loads in the next set of 10 tweets using the values of currentAccount, currentDate and pageNum.
     * @param e
     */
    @UiHandler("moreTweetsBtn")
    public void onMore(ClickEvent e){

        int nextPage = pageNum + 1;

        tweetsLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(currentUrl + nextPage, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                tweetsLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    pageNum++;
                    updateTweetsList(mention.getMentions());
                    tweetsLoader.setVisible(false);
                }
            }
        });
    }

    @UiHandler("backToTweetTopBtn")
    protected void onBackToTop(ClickEvent e){
        Window.scrollTo(0,0);
    }

    /**
     * Updates the list of tweets through a jsonp request.
     *
     * @param startDate
     * @param endDate
     * @param account : The account, can be "All" for all accounts
     */
    public static void updateTweets(String startDate, String endDate, String account){

        tweetsList.clear();
        moreTweetsBtn.setVisible(false);

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/mentions/" + screenName + startDate + endDate + "/";
        currentUrl = url;

        tweetsLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url + pageNum, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                tweetsLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    updateTweetsList(mention.getMentions());
                    tweetsLoader.setVisible(false);
                }
            }
        });
    }

    /**
     * Constructs the UI from the data returned from the server.
     *
     * @param asArrayOfMentionData : data from server.
     */
    public static void updateTweetsList(JsArray<Mention> asArrayOfMentionData) {

        int j = asArrayOfMentionData.length();
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormat dateTimeFormat2 = DateTimeFormat.getFormat("MMMM dd, yyyy h:mm:ss a");

        String urlPattern = "(https?:\\/\\/[-a-zA-Z0-9._~:\\/?#@!$&\'()*+,;=%]+)";
        String atPattern = "@+([_a-zA-Z0-9-]+)";
        String hashPattern = "#+([_a-zA-Z0-9-]+)";
        String text;
        String updatedText;
        String screenName;
        String mediaUrl;

        for (int i = 0; i < j; i++) {

            screenName = (asArrayOfMentionData.get(i).getFromUser() != null) ?
                    asArrayOfMentionData.get(i).getFromUser() :
                    asArrayOfMentionData.get(i).getUser().getScreenName();

            final TweetView tweet;

            text = asArrayOfMentionData.get(i).getText();
            updatedText = text.replaceAll(urlPattern, "<a href=\"$1\" target=\"_blank\">$1</a>");
            updatedText = updatedText.replaceAll(atPattern, "<a href=\"http://twitter.com/#!/$1\" target=\"_blank\">@$1</a>");
            updatedText = updatedText.replaceAll(hashPattern, "<a href=\"http://twitter.com/#!/search?q=%23$1\" target=\"_blank\">#$1</a>");

            String createdAt = dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt()));

            String link = "http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr();

            mediaUrl = null;

            try {
                for (int k = 0; k < asArrayOfMentionData.get(i).getEntities().getMedia().length(); k++) {
                    mediaUrl =  asArrayOfMentionData.get(i).getEntities().getMedia().get(k).getMediaUrl();
                }
            } catch (Exception e) {} // Image preview is nice, but if it fails...oh well.

            final String id = asArrayOfMentionData.get(i).getIdStr();

            if (asArrayOfMentionData.get(i).getSentiment().equals("positive")) {
                tweet = new TweetView(id, screenName, updatedText, createdAt, link, mediaUrl, IconType.SENTIMENT_SATISFIED);
            } else if (asArrayOfMentionData.get(i).getSentiment().equals("negative")) {
                tweet = new TweetView(id, screenName, updatedText, createdAt, link, mediaUrl, IconType.SENTIMENT_DISSATISFIED);
            } else {
                tweet = new TweetView(id, screenName, updatedText, createdAt, link, mediaUrl, IconType.SENTIMENT_NEUTRAL);
            }
            tweetsList.add(tweet);
        }

        // Check if we are at the end.
        if (j < 25){
            moreTweetsBtn.setVisible(false);
        } else {
            moreTweetsBtn.setVisible(true);
        }
    }
}
