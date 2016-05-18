package gov.wa.wsdot.apps.analytics.shared;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by simsl on 5/18/16.
 */
public class TweetTimes extends JavaScriptObject {
    protected TweetTimes() {}

    public final native String getStartDate() /*-{ return this.firstMention.created_at}-*/;
    public final native String getEndDate() /*-{ return this.lastMention.created_at }-*/;

}
