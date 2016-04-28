package gov.wa.wsdot.apps.analytics.client.activities.main;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;

/**
 * Created by simsl on 4/28/16.
 */
public class AnalyticsActivity extends AbstractActivity implements AnalyticsView.Presenter {

    private final ClientFactory clientFactory;
    private String name;

    public AnalyticsActivity(AnalyticsPlace place, ClientFactory clientFactory) {
        this.name = place.getAnalyticsName();
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        AnalyticsView view = clientFactory.getAnalyticsView();
        view.setPresenter(this);
        panel.setWidget(view.asWidget());
    }


    @Override
    public void onDateSubmit() {

    }


}
