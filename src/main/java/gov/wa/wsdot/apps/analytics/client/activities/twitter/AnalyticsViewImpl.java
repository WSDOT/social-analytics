package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sentiment.SentimentPieChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sources.SourcesPieChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary.SummaryChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweets.TweetsView;
import gwt.material.design.client.ui.*;

import java.util.Date;


public class AnalyticsViewImpl extends Composite implements AnalyticsView{

    private static AnalyticsViewImplUiBinder uiBinder = GWT
            .create(AnalyticsViewImplUiBinder.class);

    interface AnalyticsViewImplUiBinder extends UiBinder<Widget, AnalyticsViewImpl> {
    }

    @UiField
    MaterialNavBar navBar;

    @UiField
    MaterialLink btnSearch;

    @UiField
    MaterialNavBar navBarSearch;

    @UiField
    MaterialSearch tweetSearch;

    @UiField
    MaterialDatePicker dpStart;

    @UiField
    MaterialDatePicker dpEnd;

    @UiField
    MaterialListBox accountPicker;

    @UiField
    MaterialButton submitDateButton;

    @UiField(provided = true)
    SummaryChart summaryChart;

    @UiField(provided = true)
    SentimentPieChart sentimentPieChart;

    @UiField(provided = true)
    SourcesPieChart sourcesPieChart;

    @UiField(provided = true)
    TweetsView tweets;

    private String[] accounts =
                  {"all",
                   "BerthaDigsSR99",
                   "GoodToGoWSDOT",
                   "SnoqualmiePass",
                   "wsdot",
                   "wsdot_520",
                   "WSDOT_East",
                   "wsdot_north",
                   "wsdot_sw",
                   "wsdot_tacoma",
                   "wsdot_traffic",
                   "wsferries"};

    private Presenter presenter;

    public AnalyticsViewImpl(ClientFactory clientFactory) {

        tweets = new TweetsView(clientFactory.getEventBus());
        summaryChart = new SummaryChart(clientFactory.getEventBus());
        sentimentPieChart = new SentimentPieChart(clientFactory.getEventBus());
        sourcesPieChart = new SourcesPieChart(clientFactory.getEventBus());

        initWidget(uiBinder.createAndBindUi(this));

        accountPicker.setItemSelected(4, true);

        dpStart.setDate(new Date());
        dpEnd.setDate(new Date());

        // Add Close Handler
        tweetSearch.addCloseHandler(new CloseHandler<String>() {
            @Override
            public void onClose(CloseEvent<String> event) {
                navBar.setVisible(true);
                navBarSearch.setVisible(false);
            }
        });
    }



    @Override
    public void setPresenter(Presenter p){
        this.presenter = p;
    }

    @UiHandler("btnSearch")
    void onSearch(ClickEvent e){
        navBar.setVisible(false);
        navBarSearch.setVisible(true);
    }

    @UiHandler("accountPicker")
    protected void onSelect(ValueChangeEvent<String> e){
        presenter.onDateSubmit(dpStart.getDate(), dpEnd.getDate(), accounts[accountPicker.getSelectedIndex()]);
    }

    @UiHandler("submitDateButton")
    protected void onClick(ClickEvent click){
        presenter.onDateSubmit(dpStart.getDate(), dpEnd.getDate(), accounts[accountPicker.getSelectedIndex()]);
    }
}

