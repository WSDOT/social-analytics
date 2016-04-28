package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import gov.wa.wsdot.apps.analytics.client.activities.main.AnalyticsPlace;


@WithTokenizers({AnalyticsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
