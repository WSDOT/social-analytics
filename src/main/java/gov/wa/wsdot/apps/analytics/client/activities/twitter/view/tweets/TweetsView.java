package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweets;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweet.TweetView;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.constants.Axis;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.Date;

public class TweetsView extends Composite {

    interface MyEventBinder extends EventBinder<TweetsView> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static TweetsViewUiBinder uiBinder = GWT
            .create(TweetsViewUiBinder.class);

    interface TweetsViewUiBinder extends
            UiBinder<Widget, TweetsView> {
    }


    @UiField
    static
    MaterialPreLoader tweetsLoader;

    @UiField
    static
    HTMLPanel tweetsList;


    private static String defaultAccount = "wsdot";

    public TweetsView(EventBus eventBus) {
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        updateTweets(new Date(), defaultAccount);
    }


    // TODO Search Event handler


    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        updateTweets(event.getEndDate(), event.getAccount());
    }


    public static void updateTweets(Date day, String account){

        tweetsList.clear();

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String latestDate = fmt.format(day);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/mentions/" + screenName + latestDate;

        tweetsLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                tweetsLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    updateReplies(mention.getMentions());
                    tweetsLoader.setVisible(false);
                }
            }

        });
    }

    public static void updateReplies(JsArray<Mention> asArrayOfMentionData) {

        int j = asArrayOfMentionData.length();
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormat dateTimeFormat2 = DateTimeFormat.getFormat("MMMM dd, yyyy h:mm:ss a");
        tweetsList.clear();

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

            TweetView tweet;

            text = asArrayOfMentionData.get(i).getText();
            updatedText = text.replaceAll(urlPattern, "<a href=\"$1\" target=\"_blank\">$1</a>");
            updatedText = updatedText.replaceAll(atPattern, "<a href=\"http://twitter.com/#!/$1\" target=\"_blank\">@$1</a>");
            updatedText = updatedText.replaceAll(hashPattern, "<a href=\"http://twitter.com/#!/search?q=%23$1\" target=\"_blank\">#$1</a>");

            String createdAt = "<a href=\"http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr() + "\" target=\"_blank\">" + dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt())) + "</a></div>";

            mediaUrl = null;

            try {
                for (int k = 0; k < asArrayOfMentionData.get(i).getEntities().getMedia().length(); k++) {
                    mediaUrl =  asArrayOfMentionData.get(i).getEntities().getMedia().get(k).getMediaUrl();
                }
            } catch (Exception e) {} // Image preview is nice, but if it fails...oh well.

            if (asArrayOfMentionData.get(i).getSentiment().equals("positive")) {
                tweet = new TweetView(screenName, updatedText, createdAt, mediaUrl, IconType.SENTIMENT_SATISFIED);
            } else if (asArrayOfMentionData.get(i).getSentiment().equals("negative")) {
                tweet = new TweetView(screenName, updatedText, createdAt, mediaUrl, IconType.SENTIMENT_DISSATISFIED);
            } else {
                tweet = new TweetView(screenName, updatedText, createdAt, mediaUrl, IconType.SENTIMENT_NEUTRAL);
            }


            tweetsList.add(tweet);

        }
    }
}
