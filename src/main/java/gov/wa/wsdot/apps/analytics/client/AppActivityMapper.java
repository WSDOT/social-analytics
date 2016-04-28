package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import gov.wa.wsdot.apps.analytics.client.activities.main.AnalyticsActivity;
import gov.wa.wsdot.apps.analytics.client.activities.main.AnalyticsPlace;

public class AppActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof AnalyticsPlace)
            return new AnalyticsActivity((AnalyticsPlace) place, clientFactory);
        return null;
    }
}
