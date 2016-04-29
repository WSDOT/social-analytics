package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsView;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsViewImpl;


public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final AnalyticsView analyticsView = new AnalyticsViewImpl(this);

    public EventBus getEventBus() {
        return eventBus;
    }

    public PlaceController getPlaceController() {
        return placeController;
    }

    public AnalyticsView getAnalyticsView() {
        return analyticsView;
    }
}
