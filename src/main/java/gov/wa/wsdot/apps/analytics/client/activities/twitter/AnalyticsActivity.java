/*
 * Copyright (c) 2016 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.binder.EventBinder;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gwt.material.design.client.ui.MaterialToast;

import java.util.Date;

/**
 *  Main activity
 */
public class AnalyticsActivity extends AbstractActivity implements AnalyticsView.Presenter {

    private final ClientFactory clientFactory;
    private String name;

    private AnalyticsView view;

    public AnalyticsActivity(AnalyticsPlace place, ClientFactory clientFactory) {
        this.name = place.getAnalyticsName();
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view = clientFactory.getAnalyticsView();
        view.setPresenter(this);
        panel.setWidget(view.asWidget());
    }

    @Override
    public void onDateSubmit(Date startDate, Date endDate, String account) {

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");

        if (startDate.getTime() > endDate.getTime()) {

            MaterialToast.fireToast("Whoops! Invalid date range.");
        } else {
            String fromDate = fmt.format(startDate);
            String toDate = fmt.format(endDate);
            String dateRange = fromDate + toDate;

            clientFactory.getEventBus().fireEvent(new DateSubmitEvent(dateRange, startDate, endDate, account));
        }

    }


    @Override
    public EventBus getEventBus() {
        return clientFactory.getEventBus();
    }


}
