package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.ranking;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.*;

import java.util.Date;

/**
 * Created by simsl on 5/5/16.
 */
public class RankingView extends Composite{


    interface MyEventBinder extends EventBinder<RankingView> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static TweetsViewUiBinder uiBinder = GWT
            .create(TweetsViewUiBinder.class);

    interface TweetsViewUiBinder extends
            UiBinder<Widget, RankingView> {
    }

    @UiField
    static
    MaterialPreLoader retweetLoader;

    @UiField
    static
    MaterialPreLoader likesLoader;

    @UiField
    static
    HTMLPanel mostRetweet;

    @UiField
    static
    HTMLPanel mostLiked;

    private static String defaultAccount = "wsdot";

    public RankingView(EventBus eventBus) {
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));
        getTopRetweets(defaultAccount, new Date(), new Date());
        getTopLikes(defaultAccount, new Date(), new Date());
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");

        getTopRetweets(event.getAccount(), event.getStartDate(), event.getEndDate());
        getTopLikes(event.getAccount(), event.getStartDate(), event.getEndDate());

    }

    public static void getTopRetweets(String account, Date start, Date end){

        mostRetweet.clear();

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String startDate = fmt.format(start);
        String endDate = fmt.format(end);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/statuses/retweets/" + screenName + startDate + endDate;

        retweetLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                retweetLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    updateRetweetList(mention.getMentions());
                    retweetLoader.setVisible(false);
                }
            }
        });
    }

    public static void updateRetweetList(JsArray<Mention> asArrayOfMentionData) {

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

            MaterialCard tweet = new MaterialCard();

            screenName = (asArrayOfMentionData.get(i).getFromUser() != null) ?
                    asArrayOfMentionData.get(i).getFromUser() :
                    asArrayOfMentionData.get(i).getUser().getScreenName();

            text = asArrayOfMentionData.get(i).getText();

            MaterialLabel tweetText = new MaterialLabel(text);

            tweet.add(tweetText);

            String createdAt = dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt()));

            final String link = "http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr();

            MaterialLink updated = new MaterialLink(createdAt);
            updated.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });

            tweet.add(updated);

            tweet.setPadding(10.0);

            mostRetweet.add(tweet);

        }
    }

    public static void getTopLikes(String account, Date start, Date end){

        mostLiked.clear();

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String startDate = fmt.format(start);
        String endDate = fmt.format(end);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/statuses/favorites/" + screenName + startDate + endDate;

        likesLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                likesLoader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    updateLikesList(mention.getMentions());
                    likesLoader.setVisible(false);
                }
            }
        });
    }

    public static void updateLikesList(JsArray<Mention> asArrayOfMentionData) {

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

            MaterialCard tweetCard = new MaterialCard();

            MaterialCardContent tweet = new MaterialCardContent();

            screenName = (asArrayOfMentionData.get(i).getFromUser() != null) ?
                    asArrayOfMentionData.get(i).getFromUser() :
                    asArrayOfMentionData.get(i).getUser().getScreenName();

            text = asArrayOfMentionData.get(i).getText();

            MaterialLabel tweetText = new MaterialLabel(text);

            tweet.add(tweetText);

            String createdAt = dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt()));

            final String link = "http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr();

            MaterialLink updated = new MaterialLink(createdAt);
            updated.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });

            tweet.add(updated);

            tweet.setPadding(10.0);

            tweetCard.add(tweet);

            mostLiked.add(tweetCard);

        }
    }
}
