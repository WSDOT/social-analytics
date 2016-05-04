package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gov.wa.wsdot.apps.analytics.client.activities.events.SearchEvent;
import gov.wa.wsdot.apps.analytics.shared.Words;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.ui.MaterialToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by simsl on 4/28/16.
 */
public class AnalyticsActivity extends AbstractActivity implements AnalyticsView.Presenter {

    private final ClientFactory clientFactory;
    private String name;
    private static final String JSON_URL_SUGGESTION = Consts.HOST_URL + "/search/suggest/";
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

            clientFactory.getEventBus().fireEvent(new DateSubmitEvent(dateRange, endDate, account));
        }

    }

    @Override
    public void onSearch(Date searchDate, String searchText) {
        clientFactory.getEventBus().fireEvent(new SearchEvent(searchDate, searchText));
    }

    @Override
    public void getSuggestions(String searchText) {
        String url = JSON_URL_SUGGESTION;
        String searchString = SafeHtmlUtils.htmlEscape(searchText.trim().replace("'", ""));

        // Append the name of the callback function to the JSON URL.
        url += searchString;
        url = URL.encode(url);
        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        // Set timeout for 30 seconds (30000 milliseconds)
        jsonp.setTimeout(30000);
        jsonp.requestObject(url, new AsyncCallback<Words>() {

            @Override
            public void onFailure(Throwable caught) {
                // Just fail silently here.
            }

            @Override
            public void onSuccess(Words words) {
                if (words.getWords() != null) {

                    List<SearchObject> searchHints = new ArrayList<SearchObject>();

                    for (int i = 0; i < words.getWords().length(); i++){
                        SearchObject search = new SearchObject();
                        search.setKeyword(words.getWords().get(i));
                        searchHints.add(search);
                    }

                    view.updateSuggestions(searchHints);
                }
            }
        });
    }

    @Override
    public EventBus getEventBus() {
        return clientFactory.getEventBus();
    }


}
