package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

import java.util.Date;

/**
 * Created by simsl on 5/4/16.
 */
public class SearchEvent extends GenericEvent {

    private final String searchText;
    private final Date searchDate;

    public SearchEvent(Date date, String text) {
        this.searchText = text;
        this.searchDate = date;
    }

    public Date getDate() {
        return this.searchDate;
    }

    public String getSearchText() {
        return this.searchText;}
}
