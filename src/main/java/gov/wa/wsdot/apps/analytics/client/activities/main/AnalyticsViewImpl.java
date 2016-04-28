package gov.wa.wsdot.apps.analytics.client.activities.main;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialDatePicker;


public class AnalyticsViewImpl extends Composite implements AnalyticsView{
    private static AnalyticsViewImplUiBinder uiBinder = GWT
            .create(AnalyticsViewImplUiBinder.class);

    interface AnalyticsViewImplUiBinder extends UiBinder<Widget, AnalyticsView> {
    }

    @UiField
    MaterialDatePicker dpStart;

    @UiField
    MaterialDatePicker dpEnd;

    @UiField
    MaterialButton submitDateButton;

    private Presenter presenter;

    public AnalyticsViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }


    @Override
    public void setPresenter(Presenter p){
        this.presenter = p;
    }



    @UiHandler("submitButton")
    public void onSubmitDate(){



    }
}

