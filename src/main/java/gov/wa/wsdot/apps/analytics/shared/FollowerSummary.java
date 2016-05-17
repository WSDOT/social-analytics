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

/**
 *  Model used for data from
 *  /summary/followers/:screenName/:fromYear/:fromMonth/:fromDay/:toYear/:toMonth/:toDay
 */
public class FollowerSummary extends JavaScriptObject {
	protected FollowerSummary() {}
	
	public final native double getId() /*-{ return this._id }-*/;
	public final native int getValue() /*-{ return this.value }-*/;
	public final native JsArray<FollowerSummary> getFollowerSummary() /*-{ return this }-*/;
}
