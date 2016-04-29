package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

public interface AnalyticsView extends IsWidget {

    void setPresenter(Presenter presenter);

    public interface Presenter {
        public void onDateSubmit(Date start, Date end, String account);

        public EventBus getEventBus();
    }

}
