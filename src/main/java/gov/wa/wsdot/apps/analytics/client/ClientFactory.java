package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.wa.wsdot.apps.analytics.client.activities.main.AnalyticsView;

public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    AnalyticsView getAnalyticsView();
}
