package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.binder.GenericEvent;

/**
 * Created by simsl on 4/29/16.
 */
public class DateSubmitEvent extends GenericEvent {


    private final String dateRange;
    private final String account;

    public DateSubmitEvent(String dateRange, String account) {
        this.dateRange = dateRange;
        this.account = account;
    }

    public String getDateRange() {
        return this.dateRange;
    }

    public String getAccount() {
        return this.account;
    }
}
