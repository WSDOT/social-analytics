package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary;


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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.options.*;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.events.SetDateEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.FollowerSummary;
import gov.wa.wsdot.apps.analytics.shared.TweetSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Custom widget that creates two charts in tabs.
 * A chart of tweet/mention counts data and a chart of followers counts.
 *
 * Listens for a DateSubmitEvent
 * Fires SetDateEvent
 */
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
    MaterialRow tabs;

    @UiField
    static
    MaterialLink tweetsTab;

    @UiField
    static
    MaterialLink followersTab;

    @UiField
    static
    MaterialCardAction tweetLabel;

    @UiField
    static
    MaterialLabel followersLabel;

    @UiField
    static
    MaterialIcon followersIcon;

    @UiField
    static
    MaterialPreLoader tweetsLoader;

    @UiField
    static
    MaterialPreLoader followersLoader;

    @UiField
    static
    MaterialCardContent tweetContent;

    @UiField
    static
    MaterialCardContent followerContent;

    final Resources res;

    private static AreaChart tweetsChart;
    private static AreaChart followersChart;

    private static String dateRange = "";
    private static String account = "wsdot";

    private static final String JSON_URL = Consts.HOST_URL + "/summary";
    static JsArray<TweetSummary> tweetSummary;
    static JsArray<FollowerSummary> followerSummary;
    static ArrayList<String> dateArrayList = new ArrayList<String>();

    private static String defaultDateRange = "";

    private static ClientFactory clientFactory;

    public SummaryChart(ClientFactory clientFactory){

        this.clientFactory = clientFactory;

        res = GWT.create(Resources.class);
        res.css().ensureInjected();
        eventBinder.bindEventHandlers(this, clientFactory.getEventBus());
        initWidget(uiBinder.createAndBindUi(this));

        updateTweetsChart(defaultDateRange, "wsdot");

    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){

        dateRange = event.getDateRange();
        account = event.getAccount();

        updateTweetsChart(event.getDateRange(), event.getAccount());
        updateChartFollowers(event.getDateRange(), event.getAccount());

    }

    @UiHandler("tweetsTab")
    protected void onTweetsTabClick(ClickEvent e){
        updateTweetsChart(dateRange, account);
    }


    @UiHandler("followersTab")
    protected void onFollowerTabClick(ClickEvent e){
        updateChartFollowers(dateRange, account);;
    }

    /**
     * Requests tweet and mention counts from server for a given date range and account.
     * @param dateRange
     * @param account : Can be "All"
     */
    public static void updateTweetsChart(String dateRange, String account) {
        tweetContent.clear();
        tweetLabel.clear();
        tweetLabel.setVisible(true);
        tweetsLoader.setVisible(true);

        final String url;
        String screenName = account;

        // no date is passed for first call, date will be set with time from server
        if (screenName.equals("wsdot") && dateRange.equals("")) {
            url = JSON_URL;
        } else {
            url = JSON_URL + "/" + screenName + dateRange;
        }

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<TweetSummary>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                tweetsLoader.setVisible(false);
            }

            @Override
            public void onSuccess(TweetSummary result) {
                // Create a callback to be called when the visualization API
                // has been loaded.
                tweetSummary = result.getTweetSummary();
                // Create the API Loader
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equalsIgnoreCase(JSON_URL)) {
                            // Fire SetDateEvent to change date picker to default date from server
                            clientFactory.getEventBus().fireEvent(new SetDateEvent(new Date((long) tweetSummary.get(0).getId()),
                                    new Date((long) tweetSummary.get(tweetSummary.length() - 1).getId())));
                        }
                        tweetContent.clear();
                        tweetLabel.clear();
                        tweetContent.add(getTweetsChart());
                        drawTweetsChart(tweetSummary);
                        tweetsLoader.setVisible(false);
                    }
                });
            }
        });
    }

    /**
     *
     * Requests follower counts from server for a given date range and account.
     *
     * @param dateRange
     * @param account : Can be "all"
     */
    public static void updateChartFollowers(String dateRange, String account) {

        followerContent.clear();
        followersLoader.setVisible(true);
        followersLabel.clear();
        followersLabel.setVisible(true);

        String screenName = account;
        String url = "";

        url = JSON_URL + "/followers/" + screenName + dateRange;

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<FollowerSummary>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                followersLoader.setVisible(false);
            }

            @Override
            public void onSuccess(FollowerSummary result) {
                // Create a callback to be called when the visualization API
                // has been loaded.
                followerSummary = result.getFollowerSummary();

                // Create the API Loader
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {
                    @Override
                    public void run() {
                        followerContent.add(getFollowersChart());
                        drawFollowersChart(followerSummary);
                        followersLoader.setVisible(false);
                        followersLabel.setVisible(true);
                    }
                });
            }
        });
    }

    /**
     * Creates a chart with the data from tweetSummary
     * @param tweetSummary : returned data from server
     */
    private static void drawTweetsChart(JsArray<TweetSummary> tweetSummary) {

        DataTable data = DataTable.create();
        data.addColumn(ColumnType.STRING, "Date");
        data.addColumn(ColumnType.NUMBER, "Mentions");
        data.addColumn(ColumnType.NUMBER, "Tweets");

        data.addRows(tweetSummary.length());

        DateTimeFormat fmt = DateTimeFormat.getFormat("MMM d");
        DateTimeFormat fmt2 = DateTimeFormat.getFormat("/yyyy/M/d");

        int numberOfStatuses = 0;
        int numberOfMentions = 0;

        for (int i = 0; i < tweetSummary.length(); i++) {
            dateArrayList.add(fmt2.format(new Date((long) tweetSummary.get(i).getId())));
            data.setValue(i, 0, fmt.format(new Date((long) tweetSummary.get(i).getId())));
            data.setValue(i, 1, tweetSummary.get(i).getValue().getMentions());
            data.setValue(i, 2, tweetSummary.get(i).getValue().getStatuses());
            numberOfStatuses += tweetSummary.get(i).getValue().getStatuses();
            numberOfMentions += tweetSummary.get(i).getValue().getMentions();

        }

        tweetLabel.add(new Label("Mentions: " + numberOfMentions));
        tweetLabel.add(new Label("Tweets: " + numberOfStatuses));

        // Set options
        //Grid Lines
        Gridlines lines = Gridlines.create();
        lines.setColor("fff");

        // Text Positions X and Y Axis
        HAxis hAxis = HAxis.create();
        hAxis.setSlantedText(true);
        VAxis vAxis = VAxis.create();
        vAxis.setGridlines(lines);
        hAxis.setGridlines(lines);
        // Legend
        Legend legend = Legend.create();
        legend.setPosition(LegendPosition.TOP);
        legend.setAligment(LegendAlignment.END);

        // Set options
        AreaChartOptions options = AreaChartOptions.create();
        options.setAreaOpacity(1);
        options.setVAxis(vAxis);
        options.setHAxis(hAxis);
        options.setLegend(legend);
        options.setColors("B2DFDB", "4DB6AC");

        // Draw the chart
        tweetsChart.draw(data, options);
    }

    /**
     * Creates a chart with the data from followerSummary
     * @param followerSummary : returned data from server
     */
    private static void drawFollowersChart(JsArray<FollowerSummary> followerSummary) {

        DataTable data = DataTable.create();
        data.addColumn(ColumnType.STRING, "Date");
        data.addColumn(ColumnType.NUMBER, "Followers");
        data.addRows(followerSummary.length());
        int j = followerSummary.length();

        DateTimeFormat fmt = DateTimeFormat.getFormat("MMM d");

        for (int i = 0; i < j; i++) {
            data.setValue(i, 0, fmt.format(new Date((long) followerSummary.get(i).getId())));
            data.setValue(i, 1, followerSummary.get(i).getValue());
        }

        int startFollowers = followerSummary.get(0).getValue();
        int endFollowers = followerSummary.get(j-1).getValue();

        float change = ((float)100 * (((float)endFollowers - (float)startFollowers) / (float)startFollowers));

        change = Math.round(change * 100)/(float)100;

        followersIcon.setIconType((change > 0 ? IconType.TRENDING_UP : IconType.TRENDING_DOWN) );
        followersIcon.setIconColor((change > 0 ? "teal" : "deep-orange accent-2"));

        followersLabel.setText(Math.abs(change) + "% " + (change > 0 ? "increase" : "decrease") + " in followers from "
                + fmt.format(new Date((long) followerSummary.get(0).getId()))
                + " to " + fmt.format(new Date((long) followerSummary.get(j-1).getId())));

        // Set options
        //Grid Lines
        Gridlines lines = Gridlines.create();
        lines.setColor("fff");

        // Text Positions X and Y Axis
        HAxis hAxis = HAxis.create();
        hAxis.setSlantedText(true);
        VAxis vAxis = VAxis.create();
        vAxis.setGridlines(lines);
        hAxis.setGridlines(lines);
        // Legend
        Legend legend = Legend.create();
        legend.setPosition(LegendPosition.TOP);
        legend.setAligment(LegendAlignment.END);

        // Set options
        AreaChartOptions options = AreaChartOptions.create();
        options.setAreaOpacity(1);
        options.setVAxis(vAxis);
        options.setHAxis(hAxis);
        options.setLegend(legend);
        options.setColors("B2DFDB");

        // Draw the chart
        followersChart.draw(data, options);
    }

    private static Widget getTweetsChart() {
        if (tweetsChart == null) {
            tweetsChart = new AreaChart();
        }
        return tweetsChart;
    }

    private static Widget getFollowersChart() {
        if (followersChart == null) {
            followersChart = new AreaChart();
        }
        return followersChart;
    }

}
