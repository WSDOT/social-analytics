package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.addins.client.pathanimator.MaterialPathAnimator;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

/**
 *  A custom view for a tweet. Uses a MaterialCard as a base. Besides displaying content for the tweet has logic for
 *  editing a tweets sentiment value.
 *
 *  Is used by TweetsView
 */
public class TweetView extends Composite {

    private static TweetViewUiBinder uiBinder = GWT
            .create(TweetViewUiBinder.class);

    interface TweetViewUiBinder extends
            UiBinder<Widget, TweetView> {
    }

    @UiField
    MaterialImage image;

    @UiField
    MaterialLabel title;

    @UiField
    MaterialLabel content;

    @UiField
    MaterialLink updated;

    @UiField
    MaterialLink sentiment;

    @UiField
    MaterialCard sentimentDialog;

    @UiField
    MaterialLink positive;

    @UiField
    MaterialLink negative;

    @UiField
    MaterialLink neutral;

    @UiField
    MaterialButton btnClose;

    private static final String JSON_URL = Consts.HOST_URL + "/mentions/sentiment/edit/";
    private final String id;

    public TweetView(String id, String titleText, String contentText, String createdAt, final String url, String imageUrl, IconType sentimentType ){

        this.id = id;

        initWidget(uiBinder.createAndBindUi(this));

        title.setText(titleText);
        content.setText(contentText);

        updated.setText(createdAt);
        updated.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(url, "_blank", "");
            }
        });

        sentiment.setIconType(sentimentType);
        if (sentimentType == IconType.SENTIMENT_DISSATISFIED){
            sentiment.setIconColor("deep-orange");
        }else if (sentimentType == IconType.SENTIMENT_SATISFIED){
            sentiment.setIconColor("teal accent-4");
        }else{
            sentiment.setIconColor("black");
        }

        if (imageUrl != null) {
            image.setUrl(imageUrl);
        }
    }

    @UiHandler("sentiment")
    public void onOpenClick(ClickEvent e){
        MaterialPathAnimator.animate(sentiment.getElement(), sentimentDialog.getElement());
    }

    @UiHandler("positive")
    public void onPoslick(ClickEvent e){
        MaterialPathAnimator.reverseAnimate(sentiment.getElement(), sentimentDialog.getElement());
        sentimentDialog.setVisible(false);
        updateSentiment("positive");
    }

    @UiHandler("negative")
    public void onNegClick(ClickEvent e){
        MaterialPathAnimator.reverseAnimate(sentiment.getElement(), sentimentDialog.getElement());
        sentimentDialog.setVisible(false);
        updateSentiment("negative");
    }

    @UiHandler("neutral")
    public void onNeuClick(ClickEvent e){
        MaterialPathAnimator.reverseAnimate(sentiment.getElement(), sentimentDialog.getElement());
        sentimentDialog.setVisible(false);
        updateSentiment("neutral");
    }

    @UiHandler("btnClose")
    public void onClose(ClickEvent e){
        MaterialPathAnimator.reverseAnimate(sentiment.getElement(), sentimentDialog.getElement());
        sentimentDialog.setVisible(false);
    }

    private void updateSentiment(final String sentimentType){

        String url = JSON_URL + this.id + "/" + sentimentType;

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Mention>() {

        @Override
        public void onFailure(Throwable caught) {
            Window.alert("Failure: " + caught.getMessage());
        }

        @Override
        public void onSuccess(Mention result) {
            if (sentimentType.equalsIgnoreCase("positive")) {
                sentiment.setIconType(IconType.SENTIMENT_SATISFIED);
                sentiment.setIconColor("teal accent-4");
            } else if (sentimentType.equalsIgnoreCase("negative")) {
                sentiment.setIconType(IconType.SENTIMENT_DISSATISFIED);
                sentiment.setIconColor("deep-orange");
            }else {
                sentiment.setIconType(IconType.SENTIMENT_NEUTRAL);
                sentiment.setIconColor("black");
            }
        }});
    }
}
