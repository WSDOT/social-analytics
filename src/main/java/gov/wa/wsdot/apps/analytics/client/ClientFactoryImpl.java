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
package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsView;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsViewImpl;


public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final AnalyticsView analyticsView = new AnalyticsViewImpl(this);

    public EventBus getEventBus() {
        return eventBus;
    }

    public PlaceController getPlaceController() {
        return placeController;
    }

    public AnalyticsView getAnalyticsView() {
        return analyticsView;
    }
}
