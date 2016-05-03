package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

/**
 * Created by simsl on 5/3/16.
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

    public TweetView(String titleText, String contentText, String createdAt, final String url, String imageUrl, IconType sentimentType ){
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
            sentiment.setIconColor("blue-grey lighten-3");
        }

        if (imageUrl != null) {
            image.setUrl(imageUrl);
        }
    }

    @UiHandler("sentiment")
    public void onClick(ClickEvent e){

    }

}
