package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendPosition;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.SourceSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialPreLoader;
import gwt.material.design.client.ui.MaterialToast;

/**
 * Custom widget for displaying tweet sources data.
 *
 * Listens for DateSubmitEvents
 */
public class SourcesPieChart extends Composite{
    interface MyEventBinder extends EventBinder<SourcesPieChart> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static SourcesPieChartUiBinder uiBinder = GWT
            .create(SourcesPieChartUiBinder.class);

    interface SourcesPieChartUiBinder extends
            UiBinder<Widget, SourcesPieChart> {
    }

    @UiField
    static
    MaterialCardContent cardContent;

    @UiField
    static
    MaterialPreLoader sourcesLoader;

    final Resources res;

    private static final String JSON_URL = Consts.HOST_URL + "/mentions/source";
    static JsArray<SourceSummary> sourceSummary;

    private static String defaultDateRange = "";
    private static String defaultAccount = "wsdot";
    private static PieChart pieChart;

    public SourcesPieChart(EventBus eventBus) {
        res = GWT.create(Resources.class);
        res.css().ensureInjected();
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        updateChart(event.getDateRange(), event.getAccount());
    }

    /**
     * Requests data from the server
     * @param dateRange
     * @param account
     */
    public static void updateChart(final String dateRange, final String account) {
        cardContent.clear();

        String url = "";
        String screenName = account;

        sourcesLoader.setVisible(true);

        if (screenName.equals("all") && dateRange.equals("")) {
            url = JSON_URL + "/" + screenName;
        } else {
            url = JSON_URL + "/" + screenName + dateRange;
        }

        sourcesLoader.setVisible(true);

        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<SourceSummary>() {

            @Override
            public void onFailure(Throwable caught) {
                MaterialToast.fireToast("Failure: " + caught.getMessage());
                sourcesLoader.setVisible(false);
            }

            @Override
            public void onSuccess(SourceSummary result) {
                // Create a callback to be called when the visualization API
                // has been loaded.
                sourceSummary = result.getSourceSummary();
                // Create the API Loader
                ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                chartLoader.loadApi(new Runnable() {
                    @Override
                    public void run() {
                        cardContent.add(getPieChart());
                        drawPieChart(sourceSummary);
                        sourcesLoader.setVisible(false);
                    }
                });


            }

        });
    }

    /**
     * Updates the pue chart with new data
     * @param sourcesSummary
     */
    private static void drawPieChart(JsArray<SourceSummary> sourcesSummary) {

        DataTable data = DataTable.create();
        data.addColumn(ColumnType.STRING, "Client");
        data.addColumn(ColumnType.NUMBER, "Count");

        data.addRows(sourcesSummary.length());
        int j = sourcesSummary.length();

        for (int i = 0; i < j; i++) {
            data.setValue(i, 0, sourcesSummary.get(i).getId());
            data.setValue(i, 1, sourcesSummary.get(i).getValue());
        }

        Legend legend = Legend.create();
        legend.setPosition(LegendPosition.NONE);

        PieChartOptions options = PieChartOptions.create();
        options.setWidth(500);
        options.setHeight(400);
        ChartArea area = ChartArea.create();
        area.setTop(50);
        area.setLeft(25);
        options.setChartArea(area);
        options.setLegend(legend);
        options.setColors("00796b", "00897b", "009688", "26a69a", "4db6ac", "80cbc4", "b2dfdb");

        pieChart.draw(data, options);
    }

    private static Widget getPieChart() {
        if (pieChart == null) {
            pieChart = new PieChart();
        }
        return pieChart;
    }
}
