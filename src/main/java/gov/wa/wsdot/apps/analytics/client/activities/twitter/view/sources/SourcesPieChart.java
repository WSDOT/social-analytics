package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.sources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.binder.EventBinder;


public class SourcesPieChart extends Composite{
    interface MyEventBinder extends EventBinder<SourcesPieChart> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static SourcesPieChartUiBinder uiBinder = GWT
            .create(SourcesPieChartUiBinder.class);

    interface SourcesPieChartUiBinder extends
            UiBinder<Widget, SourcesPieChart> {
    }






























}
