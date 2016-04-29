package gov.wa.wsdot.apps.analytics.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import gov.wa.wsdot.apps.analytics.client.activities.twitter.AnalyticsPlace;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

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
