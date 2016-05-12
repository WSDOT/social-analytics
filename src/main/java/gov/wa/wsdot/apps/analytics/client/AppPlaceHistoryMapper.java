package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsPlace;


@WithTokenizers({AnalyticsPlace.AnalyticsPlaceTokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
