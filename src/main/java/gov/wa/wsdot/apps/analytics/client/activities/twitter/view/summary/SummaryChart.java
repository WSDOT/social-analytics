package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.FollowerSummary;
import gov.wa.wsdot.apps.analytics.shared.TweetSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialToast;
import javafx.scene.chart.AreaChart;

import java.util.ArrayList;


public class SummaryChart extends Composite{

    interface MyEventBinder extends EventBinder<SummaryChart> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static SummaryChartUiBinder uiBinder = GWT
            .create(SummaryChartUiBinder.class);

    interface SummaryChartUiBinder extends
            UiBinder<Widget, SummaryChart> {
    }



    @UiField
    static
    HTMLPanel chartDetailsFollowers;



    private static final String JSON_URL = Consts.HOST_URL + "/summary";
    static JsArray<TweetSummary> tweetSummary;
    static JsArray<FollowerSummary> followerSummary;
    static ArrayList<String> dateArrayList = new ArrayList<String>();
    private static int numberOfStatuses;
    private static int numberOfMentions;
    private HTMLPanel sectionHeaderHTMLPanel;
    private static Image mentionsLoading = new Image(Resources.INSTANCE.ajaxLoaderGIF());

    private static String defaultDateRange = "";


    public SummaryChart(EventBus eventBus){
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        updateChart(defaultDateRange);
        //updateChartFollowers(defaultDateRange);
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        MaterialToast.fireToast("Loading chart");
        updateChart(event.getDateRange());
        //updateChartFollowers(dateRange);
    }

    public static void updateChart(String dateRange) {
        String url = "";
        String screenName = "wsdot";//AccountsView.accounts.getValue(AccountsView.accounts.getSelectedIndex());

        if (screenName.equals("wsdot") && dateRange.equals("")) {
            url = JSON_URL;
        } else {
            url = JSON_URL + "/" + screenName + dateRange;
        }

        mentionsLoading.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<TweetSummary>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                mentionsLoading.setVisible(false);
            }

            @Override
            public void onSuccess(TweetSummary result) {
                // Create a callback to be called when the visualization API
                // has been loaded.
                tweetSummary = result.getTweetSummary();





            }
        });
    }

}
