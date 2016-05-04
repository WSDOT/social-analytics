package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import gwt.material.design.client.base.SearchObject;

import java.util.Date;
import java.util.List;

public interface AnalyticsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void updateSuggestions(List<SearchObject> suggestions);

    public interface Presenter {
        public void onDateSubmit(Date start, Date end, String account);

        public void onSearch(Date searchDate, String searchText);

        public void getSuggestions(String serachText);

        public EventBus getEventBus();
    }

}
