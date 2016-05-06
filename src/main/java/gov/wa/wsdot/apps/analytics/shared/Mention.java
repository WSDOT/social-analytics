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

package gov.wa.wsdot.apps.analytics.shared;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class Mention extends JavaScriptObject {
	protected Mention() {}
	
	public final native String getText() /*-{ return this.text }-*/;
	public final native String getProfileImageUrl() /*-{ return this.profile_image_url }-*/;
	public final native String getToUserIdStr() /*-{ return this.to_user_id_str }-*/;
	public final native String getFromUser() /*-{ return this.from_user }-*/;
	public final native int getFromUserId() /*-{ return this.from_user_id }-*/;
	public final native int getToUserId() /*-{ return this.to_user_id }-*/;
	public final native String getGeo() /*-{ return this.geo }-*/;
	public final native double getId() /*-{ return this.id }-*/;
	public final native String getIsoLanguageCode() /*-{ return this.iso_language_code }-*/;
	public final native String getFromUserIdStr() /*-{ return this.from_user_id_str }-*/;
	public final native String getSource() /*-{ return this.source }-*/;
	public final native String getIdStr() /*-{ return this.id_str }-*/;
	public final native String getCreatedAt() /*-{ return this.created_at }-*/;
	public final native String getSentiment() /*-{ return this.sentiment }-*/;
	public final native Entities getEntities() /*-{ return this.entities }-*/;
	public final native JsArray<Mention> getMentions() /*-{ return this }-*/;
	public final native User getUser() /*-{ return this.user }-*/;
	public final native int getRetweet() /*-{ return this.retweet_count }-*/;
	public final native int getFavorited() /*-{ return this.favorite_count }-*/;
}
