package gov.wa.wsdot.apps.analytics.client.activities.events;
import com.google.web.bindery.event.shared.binder.GenericEvent;

public class SentimentDisplayEvent extends GenericEvent {

    String sentiment;

    public SentimentDisplayEvent(String sentiment){
        this.sentiment = sentiment;
    }

    public String getSentiment(){
        return sentiment;
    }
}
