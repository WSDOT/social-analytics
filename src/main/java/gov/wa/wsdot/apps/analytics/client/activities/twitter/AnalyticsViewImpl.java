package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary.SummaryChart;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialDatePicker;


public class AnalyticsViewImpl extends Composite implements AnalyticsView{

    private static AnalyticsViewImplUiBinder uiBinder = GWT
            .create(AnalyticsViewImplUiBinder.class);

    interface AnalyticsViewImplUiBinder extends UiBinder<Widget, AnalyticsViewImpl> {
    }

    @UiField
    MaterialDatePicker dpStart;

    @UiField
    MaterialDatePicker dpEnd;

    @UiField
    MaterialButton submitDateButton;

    @UiField(provided = true)
    SummaryChart summaryChart;

    private Presenter presenter;

    public AnalyticsViewImpl(ClientFactory clientFactory) {

        summaryChart = new SummaryChart(clientFactory.getEventBus());
        initWidget(uiBinder.createAndBindUi(this));

    }


    @Override
    public void setPresenter(Presenter p){
        this.presenter = p;
    }


    @UiHandler("submitDateButton")
    protected void onClick(ClickEvent click){
        presenter.onDateSubmit(dpStart.getDate(), dpEnd.getDate(), "@wsdot");
    }
}

