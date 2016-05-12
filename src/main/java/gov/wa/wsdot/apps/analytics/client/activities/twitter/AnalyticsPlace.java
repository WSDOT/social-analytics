package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;


public class AnalyticsPlace extends Place {
    private String AnalyticsName;

    public AnalyticsPlace(String token) {
        this.AnalyticsName = token;
    }

    public String getAnalyticsName() {
        return AnalyticsName;
    }

    public static class AnalyticsPlaceTokenizer implements PlaceTokenizer<AnalyticsPlace> {

        @Override
        public String getToken(AnalyticsPlace place) {
            return place.getAnalyticsName();
        }

        @Override
        public AnalyticsPlace getPlace(String token) {
            return new AnalyticsPlace(token);
        }
    }
}
