package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.events.SetDateEvent;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.ranking.RankingView;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.search.SearchView;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sentiment.SentimentPieChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sources.SourcesPieChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary.SummaryChart;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.tweets.TweetsView;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.*;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;


public class AnalyticsViewImpl extends Composite implements AnalyticsView{

    interface MyEventBinder extends EventBinder<AnalyticsViewImpl> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static AnalyticsViewImplUiBinder uiBinder = GWT
            .create(AnalyticsViewImplUiBinder.class);

    interface AnalyticsViewImplUiBinder extends UiBinder<Widget, AnalyticsViewImpl> {
    }

    @UiField
    MaterialNavBar navBar;

    @UiField
    MaterialImage logo;

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

    @UiField(provided = true)
    SearchView searchResults;

    @UiField(provided = true)
    RankingView ranking;

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

        eventBinder.bindEventHandlers(this, clientFactory.getEventBus());

        tweets = new TweetsView(clientFactory.getEventBus());
        searchResults = new SearchView(clientFactory.getEventBus());
        summaryChart = new SummaryChart(clientFactory);
        sentimentPieChart = new SentimentPieChart(clientFactory.getEventBus());
        sourcesPieChart = new SourcesPieChart(clientFactory.getEventBus());
        ranking = new RankingView(clientFactory.getEventBus());

        initWidget(uiBinder.createAndBindUi(this));
        logo.setResource(Resources.INSTANCE.tacronymWhiteLogoPNG());
        logo.addStyleName(Resources.INSTANCE.css().logo());

        accountPicker.setItemSelected(4, true);

    }

    @Override
    public void setPresenter(Presenter p){
        this.presenter = p;
    }

    @EventHandler
    void onSetDate(SetDateEvent event){
        // Only react to this event if we have no dates already (Ideally when the application starts)
        if(dpStart.getDate() == null || dpEnd.getDate() == null) {
            dpStart.setDate(event.getStartDate());
            dpEnd.setDate(event.getEndDate());
            presenter.onDateSubmit(dpStart.getDate(), dpEnd.getDate(), accounts[accountPicker.getSelectedIndex()]);
        }
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

