package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

import java.util.Date;

/**
 * Created by simsl on 5/4/16.
 */
public class SearchEvent extends GenericEvent {

    private final String searchText;

    public SearchEvent(String text) {
        this.searchText = text;
    }

    public String getSearchText() {
        return this.searchText;}
}
