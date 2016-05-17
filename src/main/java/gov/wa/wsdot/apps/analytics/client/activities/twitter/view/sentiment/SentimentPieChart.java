/*
 * Copyright (c) 2016 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sentiment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendPosition;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.events.SentimentDisplayEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.SentimentSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialPreLoader;

/**
 * Custom widget for displaying tweet sentiment data.
 *
 * Listens for DateSubmitEvents
 */
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

    final Resources res;
    static ClientFactory clientFactory;

    private static final String JSON_URL = Consts.HOST_URL + "/mentions";
    static JsArray<SentimentSummary> sentimentSummary;
    private static PieChart pieChart;

    public SentimentPieChart(ClientFactory clientFactory) {
        res = GWT.create(Resources.class);
        res.css().ensureInjected();
        this.clientFactory = clientFactory;
        eventBinder.bindEventHandlers(this, clientFactory.getEventBus());
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

        sentimentLoader.setVisible(true);

        url = JSON_URL + "/sentiment/" + screenName + dateRange;

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

    /**
     * Updates the pue chart with new data
     * @param sentimentSummary
     */
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
        options.setColors("BDBDBD", "26A69A", "FF6E40");

        pieChart.draw(data, options);
    }

    private static Widget getPieChart() {
        if (pieChart == null) {
            pieChart = new PieChart();
            pieChart.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {

                    // May be multiple selections.
                    JsArray<Selection> selections = pieChart.getSelection();

                    for (int i = 0; i < selections.length(); i++) {
                        Selection selection = selections.get(i);

                        int row = selection.getRow();
                        // Append the name of the callback function to the JSON URL
                        if (row == 0) {
                            clientFactory.getEventBus().fireEvent(new SentimentDisplayEvent("neutral"));
                        } else if (row == 1) {
                            clientFactory.getEventBus().fireEvent(new SentimentDisplayEvent("positive"));
                        } else if (row == 2) {
                            clientFactory.getEventBus().fireEvent(new SentimentDisplayEvent("negative"));
                        }
                    }
                }
            });
        }
        return pieChart;
    }
}
