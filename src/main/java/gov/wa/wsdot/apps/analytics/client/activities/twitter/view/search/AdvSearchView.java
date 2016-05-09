package gov.wa.wsdot.apps.analytics.client.activities.twitter.view.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
            MaterialToast.fireToast("Search term required");
        }else {

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

            SearchEvent searchEvent = new SearchEvent(searchTerm, searchType, account, startDate, endDate);

            this.eventBus.fireEvent(searchEvent);
            advSearch.closeModal();
        }
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
    }

    public void open(){
        advSearch.openModal();
    }


}
