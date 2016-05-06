package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

import java.util.Date;


public class DateSubmitEvent extends GenericEvent {


    private final String dateRange;
    private final String account;
    private final Date startDate;
    private final Date endDate;

    public DateSubmitEvent(String dateRange, Date startDate, Date endDate, String account) {
        this.dateRange = dateRange;
        this.account = account;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getDateRange() {
        return this.dateRange;
    }

    public String getAccount() {
        return this.account;
    }

    public Date getStartDate(){ return this.startDate;}

    public Date getEndDate(){ return this.endDate;}
}
