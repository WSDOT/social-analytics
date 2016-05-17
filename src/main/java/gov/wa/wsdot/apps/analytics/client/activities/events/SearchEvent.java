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
package gov.wa.wsdot.apps.analytics.client.activities.events;

import com.google.web.bindery.event.shared.binder.GenericEvent;

public class SearchEvent extends GenericEvent {

    private String searchText;
    private String account = "All";
    private int searchType = 0;
    private String startDate = null;
    private String endDate = null;
    private int mediaOnly = 0;

    public SearchEvent(String text) {
        this.searchText = text;
    }

    public SearchEvent(String text, int type, String account, int mediaOnly, String startDate, String endDate) {
        this.searchText = text;
        this.searchType = type;
        this.account = account;
        this.mediaOnly = mediaOnly;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getSearchType(){
        return this.searchType;
    }

    public String getAccount(){
        return this.account;
    }

    public int getMediaOnly() {
        return this.mediaOnly;
    }

    public String getEndDate(){
        return this.endDate;
    }

    public String getStartDate(){
        return this.startDate;
    }

    public String getSearchText() {
        return this.searchText;}
}
