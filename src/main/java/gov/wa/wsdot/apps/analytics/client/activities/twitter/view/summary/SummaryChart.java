package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.options.*;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.FollowerSummary;
import gov.wa.wsdot.apps.analytics.shared.TweetSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialPreLoader;
import gwt.material.design.client.ui.MaterialToast;

import java.util.ArrayList;
import java.util.Date;


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

    @UiField
    static
    MaterialCardAction labels;

    @UiField
    static
    MaterialPreLoader loader;

    @UiField
    static
    MaterialCardContent cardContent;

    private static AreaChart chart;

    private static final String JSON_URL = Consts.HOST_URL + "/summary";
    static JsArray<TweetSummary> tweetSummary;
    static JsArray<FollowerSummary> followerSummary;
    static ArrayList<String> dateArrayList = new ArrayList<String>();

    private HTMLPanel sectionHeaderHTMLPanel;
    private static Image mentionsLoading = new Image(Resources.INSTANCE.ajaxLoaderGIF());

    private static String defaultDateRange = "";


    public SummaryChart(EventBus eventBus){
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        updateTweetsChart(defaultDateRange);
        //updateChartFollowers(defaultDateRange);
    }



    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        updateTweetsChart(event.getDateRange());
        //updateChartFollowers(dateRange);
    }

    public static void updateTweetsChart(String dateRange) {
        cardContent.clear();
        labels.clear();
        loader.setVisible(true);

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

                // Create the API Loader
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {

                    @Override
                    public void run() {

                        cardContent.add(getChart());
                        drawTweetsChart(tweetSummary);
                        loader.setVisible(false);
                    }
                });



            }
        });
    }


    private static Widget getChart() {
        if (chart == null) {
            chart = new AreaChart();
        }
        return chart;
    }


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
            numberOfMentions += tweetSummary.get(i).getValue().getMentions();
            numberOfStatuses += tweetSummary.get(i).getValue().getStatuses();
        }

        labels.add(new Label("Mentions: " + numberOfMentions));
        labels.add(new Label("Tweets: " + numberOfStatuses));

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
        options.setIsStacked(true);
        options.setAreaOpacity(1);
        options.setVAxis(vAxis);
        options.setHAxis(hAxis);
        options.setLegend(legend);
        options.setColors("B2DFDB", "4DB6AC");

        // Draw the chart
        chart.draw(data, options);
    }


}
