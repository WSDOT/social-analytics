package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sentiment;

import com.google.gwt.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendAlignment;
import com.googlecode.gwt.charts.client.options.LegendPosition;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.Mention;
import gov.wa.wsdot.apps.analytics.shared.SentimentSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialPreLoader;

public class SentimentPieChart extends Composite {

    interface MyEventBinder extends EventBinder<SentimentPieChart> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static SentimentPieChartUiBinder uiBinder = GWT
            .create(SentimentPieChartUiBinder.class);

    interface SentimentPieChartUiBinder extends
            UiBinder<Widget, SentimentPieChart> {
    }

    @UiField
    static
    MaterialCardContent cardContent;

    @UiField
    static
    MaterialPreLoader sentimentLoader;

    private static final String JSON_URL = Consts.HOST_URL + "/mentions";
    static JsArray<SentimentSummary> sentimentSummary;
    private static String defaultDateRange = "";
    private static String defaultAccount = "wsdot";
    private static PieChart pieChart;

    public SentimentPieChart(EventBus eventBus) {
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));

        updateChart(defaultDateRange, defaultAccount);
    }


    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        updateChart(event.getDateRange(), event.getAccount());
    }

    public static void updateChart(final String dateRange, final String account) {
        String url = "";
        String screenName = account;

        sentimentLoader.setVisible(true);

        if (screenName.equals("all") && dateRange.equals("")) {
            url = JSON_URL + "/sentiment/" + screenName;
        } else {
            url = JSON_URL + "/sentiment/" + screenName + dateRange;
        }

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<SentimentSummary>() {

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Failure: " + caught.getMessage());
                sentimentLoader.setVisible(false);
            }

            @Override
            public void onSuccess(SentimentSummary result) {
                // Create a callback to be called when the visualization API
                // has been loaded.
                sentimentSummary = result.getSentimentSummary();

                // Create the API Loader
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {
                    @Override
                    public void run() {
                        cardContent.add(getPieChart());
                        drawPieChart(sentimentSummary);
                        sentimentLoader.setVisible(false);
                    }
                });


            }

        });

    }

    private static Widget getPieChart() {
        if (pieChart == null) {
            pieChart = new PieChart();
        }
        return pieChart;
    }


    private static void drawPieChart(JsArray<SentimentSummary> sentimentSummary) {

        DataTable data = DataTable.create();
        data.addColumn(ColumnType.STRING, "Sentiment");
        data.addColumn(ColumnType.NUMBER, "Count");

        data.addRows(sentimentSummary.length());
        int j = sentimentSummary.length();

        for (int i = 0; i < j; i++) {
            data.setValue(i, 0, sentimentSummary.get(i).getId());
            data.setValue(i, 1, sentimentSummary.get(i).getValue());
        }

        // Legend
        Legend legend = Legend.create();
        legend.setPosition(LegendPosition.TOP);
        legend.setAligment(LegendAlignment.END);

        PieChartOptions options = PieChartOptions.create();
        options.setWidth(500);
        options.setHeight(400);
        options.setLegend(legend);
        options.setColors("BDBDBD", "26A69A", "FF6E40");

        pieChart.draw(data, options);

    }

}
