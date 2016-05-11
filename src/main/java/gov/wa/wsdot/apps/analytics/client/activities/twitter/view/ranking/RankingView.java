package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.ranking;

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
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.*;

import java.util.Date;


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
    MaterialPreLoader loader;

    @UiField
    static
    MaterialCollection mostRetweet;

    @UiField
    static
    MaterialCollection mostLiked;
    @UiField
    static
    MaterialCollection leastRetweet;

    @UiField
    static
    MaterialCollection leastLiked;

    @UiField
    static
    MaterialLink retweetTab;

    @UiField
    static
    MaterialLink likeTab;

    final Resources res;

    private static String defaultAccount = "wsdot";

    public RankingView(EventBus eventBus) {
        res = GWT.create(Resources.class);
        res.css().ensureInjected();
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        getRetweets(defaultAccount, new Date(), new Date(), mostRetweet, "best");
        getLikes(defaultAccount, new Date(), new Date(), mostLiked, "best");
        getRetweets(defaultAccount, new Date(), new Date(), leastRetweet, "worst");
        getLikes(defaultAccount, new Date(), new Date(), leastLiked, "worst");
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        getRetweets(event.getAccount(), event.getStartDate(), event.getEndDate(), mostRetweet, "best");
        getLikes(event.getAccount(), event.getStartDate(), event.getEndDate(), mostLiked, "best");
        getRetweets(event.getAccount(), event.getStartDate(), event.getEndDate(), leastRetweet, "worst");
        getLikes(event.getAccount(), event.getStartDate(), event.getEndDate(), leastLiked, "worst");
    }

    public static void getRetweets(String account, Date start, Date end, final MaterialCollection list, final String listType){

        list.clear();

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String startDate = fmt.format(start);
        String endDate = fmt.format(end);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/statuses/retweets/" + listType + "/" + screenName + startDate + endDate;

        loader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                loader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    list.clear();
                    updateRetweetList(mention.getMentions(), list, listType);
                    loader.setVisible(false);
                }
            }
        });
    }

    public static void updateRetweetList(JsArray<Mention> asArrayOfMentionData,  MaterialCollection list, String listType) {

        int j = asArrayOfMentionData.length();
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormat dateTimeFormat2 = DateTimeFormat.getFormat("MMMM dd, yyyy h:mm:ss a");

        String text;
        String screenName;

        // Assemble header
        MaterialCollectionItem header = new MaterialCollectionItem();

        MaterialCollectionSecondary iconContatiner = new MaterialCollectionSecondary();

        MaterialIcon retweetIcon = new MaterialIcon(IconType.REPEAT);
        retweetIcon.setIconColor("blue lighten-1");
        retweetIcon.setFloat(Style.Float.LEFT);
        header.add(retweetIcon);

        MaterialIcon trendingIcon;

        if (listType.equalsIgnoreCase("best")) {
            trendingIcon = new MaterialIcon(IconType.SENTIMENT_VERY_SATISFIED);
            trendingIcon.setIconColor("green");
        }else{
            trendingIcon = new MaterialIcon(IconType.SENTIMENT_VERY_DISSATISFIED);
            trendingIcon.setIconColor("red");
        }

        iconContatiner.add(trendingIcon);
        iconContatiner.setPaddingTop(8);

        header.add(iconContatiner);

        MaterialLabel headerText = new MaterialLabel();
        headerText.setText("Retweets");
        headerText.setFontSize("1.5em");
        header.add(headerText);

        list.add(header);

        for (int i = 0; i < j; i++) {

            MaterialCollectionItem tweet = new MaterialCollectionItem();

            screenName = (asArrayOfMentionData.get(i).getFromUser() != null) ?
                    asArrayOfMentionData.get(i).getFromUser() :
                    asArrayOfMentionData.get(i).getUser().getScreenName();

            text = asArrayOfMentionData.get(i).getText();

            final String link = "http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr();

            MaterialLink tweetUser = new MaterialLink("@" + screenName);
            tweetUser.setFontSize("1.3em");
            tweetUser.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });
            tweet.add(tweetUser);

            MaterialLabel tweetText = new MaterialLabel(text);

            tweet.add(tweetText);

            String createdAt = dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt()));

            MaterialLink updated = new MaterialLink(createdAt);
            updated.setTextColor("grey");
            updated.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });

            tweet.add(updated);

            // Add badge
            if (listType == "best" && i == 0) {
                MaterialIcon best = new MaterialIcon(IconType.GRADE);
                best.setIconColor("amber");
                best.setFloat(Style.Float.RIGHT);
                tweet.add(best);
            }

            tweet.setShadow(0);

            tweet.setPadding(10.0);

            list.add(tweet);

        }
    }

    public static void getLikes(String account, Date start, Date end, final MaterialCollection list, final String listType){

        list.clear();

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");
        String startDate = fmt.format(start);
        String endDate = fmt.format(end);
        String screenName = account;

        String url = Consts.HOST_URL + "/summary/statuses/favorites/" + listType + "/" + screenName + startDate + endDate;

        loader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                loader.setVisible(false);
            }

            @Override
            public void onSuccess(Mention mention) {
                if (mention.getMentions() != null) {
                    list.clear();
                    updateLikesList(mention.getMentions(), list, listType);
                    loader.setVisible(false);
                }
            }
        });
    }

    public static void updateLikesList(JsArray<Mention> asArrayOfMentionData, MaterialCollection list, String listType) {

        int j = asArrayOfMentionData.length();
        DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormat dateTimeFormat2 = DateTimeFormat.getFormat("MMMM dd, yyyy h:mm:ss a");

        String text;
        String screenName;

        // Assemble header
        MaterialCollectionItem header = new MaterialCollectionItem();

        MaterialCollectionSecondary iconContatiner = new MaterialCollectionSecondary();

        MaterialIcon likesIcon = new MaterialIcon(IconType.FAVORITE);
        likesIcon.setIconColor("pink lighten-1");
        likesIcon.setFloat(Style.Float.LEFT);
        header.add(likesIcon);

        MaterialIcon trendingIcon;

        if (listType.equalsIgnoreCase("best")) {
            trendingIcon = new MaterialIcon(IconType.SENTIMENT_VERY_SATISFIED);
            trendingIcon.setIconColor("green");
        }else{
            trendingIcon = new MaterialIcon(IconType.SENTIMENT_VERY_DISSATISFIED);
            trendingIcon.setIconColor("red");
        }

        iconContatiner.add(trendingIcon);

        iconContatiner.setPaddingTop(8);

        header.add(iconContatiner);

        MaterialLabel headerText = new MaterialLabel();
        headerText.setText("Likes");
        headerText.setFontSize("1.5em");
        header.add(headerText);

        list.add(header);

        for (int i = 0; i < j; i++) {

            MaterialCollectionItem tweet = new MaterialCollectionItem();

            screenName = (asArrayOfMentionData.get(i).getFromUser() != null) ?
                    asArrayOfMentionData.get(i).getFromUser() :
                    asArrayOfMentionData.get(i).getUser().getScreenName();

            text = asArrayOfMentionData.get(i).getText();

            final String link = "http://twitter.com/#!/" + screenName + "/status/" + asArrayOfMentionData.get(i).getIdStr();

            MaterialLink tweetUser = new MaterialLink("@" + screenName);
            tweetUser.setFontSize("1.3em");
            tweetUser.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });
            tweet.add(tweetUser);

            MaterialLabel tweetText = new MaterialLabel(text);

            tweet.add(tweetText);

            String createdAt = dateTimeFormat2.format(dateTimeFormat.parse(asArrayOfMentionData.get(i).getCreatedAt()));

            MaterialLink updated = new MaterialLink(createdAt);
            updated.setTextColor("grey");
            updated.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(link, "_blank", "");
                }
            });

            tweet.add(updated);

            // Add badge
            if (listType == "best" && i == 0) {
                MaterialIcon best = new MaterialIcon(IconType.GRADE);
                best.setIconColor("amber");
                best.setFloat(Style.Float.RIGHT);
                tweet.add(best);
            }

            tweet.setPadding(10.0);
            list.add(tweet);
        }
    }
}
