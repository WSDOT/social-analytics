package gov.wa.wsdot.apps.analytics.client.activities.main;

import com.google.gwt.user.client.ui.IsWidget;

public interface AnalyticsView extends IsWidget {

    void setPresenter(Presenter presenter);

    public interface Presenter {
        public void onDateSubmit();
    }

}
