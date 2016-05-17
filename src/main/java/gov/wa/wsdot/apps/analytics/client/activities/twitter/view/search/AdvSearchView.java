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
package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.binder.EventBinder;
import gov.wa.wsdot.apps.analytics.client.activities.events.SearchEvent;
import gov.wa.wsdot.apps.analytics.util.Consts;
import gwt.material.design.client.ui.*;

import java.util.Date;

public class AdvSearchView extends Composite{

    interface MyEventBinder extends EventBinder<AdvSearchView> {}
    private final MyEventBinder eventBinder = GWT.create(MyEventBinder.class);

    private static AdvSearchViewUiBinder uiBinder = GWT
            .create(AdvSearchViewUiBinder.class);

    interface AdvSearchViewUiBinder extends
            UiBinder<Widget, AdvSearchView> {
    }

    @UiField
    static
    MaterialModal advSearch;

    @UiField
    static
    MaterialButton searchBtn;

    @UiField
    static
    MaterialListBox searchAccountPicker;

    @UiField
    static
    MaterialListBox searchTypePicker;

    @UiField
    static
    MaterialDatePicker searchdpStart;

    @UiField
    static
    MaterialDatePicker searchdpEnd;

    @UiField
    static
    MaterialTextBox advSearchTextBox;

    @UiField
    static
    MaterialCheckBox mediaOnly;

    @UiField
    static
    MaterialButton closeAdvSearchBtn;

    @UiField
    static
    MaterialButton clearAdvSearchBtn;

    private static final String JSON_URL_SUGGESTION = Consts.HOST_URL + "/search/suggest/";
    private static int pageNum = 1;
    private static String searchText = "";
    private static EventBus eventBus;


    public AdvSearchView(EventBus eventBus) {
        eventBinder.bindEventHandlers(this, eventBus);
        initWidget(uiBinder.createAndBindUi(this));
        this.eventBus = eventBus;
    }

    @UiHandler("searchBtn")
    void onSearch(ClickEvent e){

        if (advSearchTextBox.getText().equalsIgnoreCase("")){
            advSearchTextBox.setText(" ");
        }

        DateTimeFormat fmt = DateTimeFormat.getFormat("/yyyy/M/d");

        String endDate = null;
        String startDate = null;

        if (searchdpStart.getDate() != null) {
            startDate = fmt.format(searchdpStart.getDate());
        }

        if (searchdpEnd.getDate() != null) {
            endDate = fmt.format(searchdpEnd.getDate());
        }

        String account = searchAccountPicker.getSelectedItemText();
        int searchType = Integer.valueOf(searchTypePicker.getSelectedValue());
        String searchTerm = advSearchTextBox.getText();

        int media = (mediaOnly.getValue() ? 1 : 0);

        SearchEvent searchEvent = new SearchEvent(searchTerm, searchType, account, media, startDate, endDate);

        this.eventBus.fireEvent(searchEvent);
        advSearch.closeModal();

    }

    // Setting orientation closes date picker.
    @UiHandler("searchdpStart")
    protected void onStartDateSelected(ValueChangeEvent<Date> e){
        // Forces date picker to close
        searchdpStart.setOrientation(searchdpStart.getOrientation());
    }

    @UiHandler("searchdpEnd")
    protected void onEndDateSelected(ValueChangeEvent<Date> e){
        // Forces date picker to close
        searchdpEnd.setOrientation(searchdpEnd.getOrientation());
    }

    @UiHandler("clearAdvSearchBtn")
    void onClearAdvSearch(ClickEvent e){
        clearAdvSearch();
    }

    @UiHandler("closeAdvSearchBtn")
    void onCloseAdvSearch(ClickEvent e){
        advSearch.closeModal();
        clearAdvSearch();
    }

    private void clearAdvSearch() {
        advSearchTextBox.clear();
        searchdpEnd.clear();
        searchdpStart.clear();
        searchAccountPicker.setSelectedIndex(0);
        mediaOnly.setValue(false);
    }

    public void open(){
        advSearch.openModal();
    }


}
