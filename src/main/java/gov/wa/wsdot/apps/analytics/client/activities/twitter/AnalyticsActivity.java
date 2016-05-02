package gov.wa.wsdot.apps.analytics.client.activities.twitter;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.wa.wsdot.apps.analytics.client.ClientFactory;
import gov.wa.wsdot.apps.analytics.client.activities.events.DateSubmitEvent;
import gwt.material.design.client.ui.MaterialToast;

import java.util.Date;

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
    public void onDateSubmit(Date startDate, Date endDate, String account) {

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");

        if (startDate.getTime() > endDate.getTime()) {

            MaterialToast.fireToast("Whoops! Invalid date range.");
        } else {
            String fromDate = fmt.format(startDate);
            String toDate = fmt.format(endDate);
            String dateRange = fromDate + toDate;

            clientFactory.getEventBus().fireEvent(new DateSubmitEvent(dateRange, account));

            /*
            //dateRangeLoading.setVisible(true);
            SummaryChart.updateChart(dateRange);
            SummaryChart.updateChartFollowers(dateRange);
            SentimentPieChart.updateChart(dateRange);
            SourcesPieChart.updateChart(dateRange);
            TweetsView.showLatestTweets();
            */
        }

    }

    @Override
    public EventBus getEventBus() {
        return clientFactory.getEventBus();
    }


}
