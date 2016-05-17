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

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsPlace;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SocialAnalytics implements EntryPoint {

  private Place defaultPlace = new AnalyticsPlace("twitter");
  private SimplePanel appWidget = new SimplePanel();

  @Override
  public void onModuleLoad() {

      ClientFactory clientFactory = GWT.create(ClientFactory.class);
      EventBus eventBus = clientFactory.getEventBus();
      PlaceController placeController = clientFactory.getPlaceController();

      // Start ActivityManager for the twitter widget with our ActivityMapper
      ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
      ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
      activityManager.setDisplay(appWidget);

      // Start PlaceHistoryHandler with our PlaceHistoryMapper
      AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
      PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
      historyHandler.register(placeController, eventBus, defaultPlace);

      RootPanel.get().add(appWidget);
      // Goes to the place represented on URL else default place
      historyHandler.handleCurrentHistory();

  }
}
