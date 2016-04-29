package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.summary;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.binder.EventBinder;
import com.google.web.bindery.event.shared.binder.EventHandler;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.resources.Resources;
import gov.wa.wsdot.apps.analytics.shared.FollowerSummary;
import gov.wa.wsdot.apps.analytics.shared.TweetSummary;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.MaterialToast;

import java.util.ArrayList;


public class SummaryChart extends Label{

    interface MyEventBinder extends EventBinder<SummaryChart> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);


    private static String defaultDateRange = "";


    public SummaryChart(EventBus eventBus){
        this(eventBus, defaultDateRange);
    }

    public SummaryChart(final EventBus eventBus, String dateRange){
        eventBinder.bindEventHandlers(this, eventBus);
        setText(dateRange);
    }

    @EventHandler
    void onDateSubmit(DateSubmitEvent event){
        setText(event.getDateRange());
    }
}
