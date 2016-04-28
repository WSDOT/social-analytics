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

public class Status extends JavaScriptObject {
	protected Status() {}
	
	public final native String getText() /*-{ return this.text }-*/;
	public final native String getCreatedAt() /*-{ return this.created_at }-*/;
	public final native double getId() /*-{ return this.id }-*/;
	public final native String getIdStr() /*-{ return this.id_str }-*/;
	public final native String getSource() /*-{ return this.source }-*/;
	public final native User getUser() /*-{ return this.user }-*/;
	public final native JsArray<Status> getStatus() /*-{ return this }-*/;
}
