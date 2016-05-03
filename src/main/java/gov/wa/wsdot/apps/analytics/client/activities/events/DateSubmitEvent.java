package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.binder.GenericEvent;

import java.util.Date;

/**
 * Created by simsl on 4/29/16.
 */
public class DateSubmitEvent extends GenericEvent {


    private final String dateRange;
    private final String account;
    private final Date endDate;

    public DateSubmitEvent(String dateRange, Date endDate, String account) {
        this.dateRange = dateRange;
        this.account = account;
        this.endDate = endDate;
    }

    public String getDateRange() {
        return this.dateRange;
    }

    public String getAccount() {
        return this.account;
    }

    public Date getEndDate(){ return this.endDate;}
}
