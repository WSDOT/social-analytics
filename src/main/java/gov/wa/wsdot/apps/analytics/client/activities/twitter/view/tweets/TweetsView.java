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
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweet.TweetView;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialPreLoader;
import gwt.material.design.client.ui.MaterialToast;
import java.util.Date;

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
    MaterialButton backToTopBtn;

    // Following 3 values used for loading more tweets
    private static String currentAccount = "wsdot";
    private static String currentDate;
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
        currentDate = fmt.format(event.getEndDate());
        updateTweets(event.getEndDate(), event.getAccount());
    }

    /**
     * Loads in the next set of 10 tweets using the values of currentAccount, currentDate and pageNum.
     * @param e
     */
    @UiHandler("moreTweetsBtn")
    public void onMore(ClickEvent e){

        pageNum++;
        String url = Consts.HOST_URL + "/summary/mentions/" + currentAccount + currentDate + "/" + pageNum;

        tweetsLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

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

    @UiHandler("backToTopBtn")
    protected void onBackToTop(ClickEvent e){
        Window.scrollTo(0,0);
    }

    /**
     * Updates the list of tweets through a jsonp request.
     *
     * @param day : the day which we want tweets from
     * @param account : The account, can be "All" for all accounts
     */
    public static void updateTweets(Date day, String account){

        tweetsList.clear();
        moreTweetsBtn.setVisible(false);

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String latestDate = fmt.format(day);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/mentions/" + screenName + latestDate + "/" + pageNum;

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

            TweetView tweet;

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

            String id = asArrayOfMentionData.get(i).getIdStr();

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
        // NOTE: if the num of tweets is a factor of 10 the "more" button will still display at the end.
        if (j < 10){
            moreTweetsBtn.setVisible(false);
        } else {
            moreTweetsBtn.setVisible(true);
        }
    }
}
