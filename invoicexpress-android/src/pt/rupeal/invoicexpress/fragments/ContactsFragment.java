package pt.rupeal.invoicexpress.fragments;

import java.util.List;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.adapters.ContactListRowAdapter;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.server.ContactsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.widgets.SearchView;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;

public class ContactsFragment extends Fragment {
	
	private ContactListRowAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// after the create option method is called
		setHasOptionsMenu(true);
		
		((MainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		List<ContactModel> contacts = InvoiceXpress.getInstance().getContactsSorted();
		if(contacts == null || contacts.isEmpty()) {
			return inflater.inflate(R.layout.contacts_list_empty, container, false);
		}
		
		// inflate view
		View view = inflater.inflate(R.layout.contacts_list_fragment, container, false);
		
		// set adapter for contacts rows
		adapter = new ContactListRowAdapter(inflater.getContext(), this, contacts);
		((ListView) view.findViewById(R.id.contacts_list)).setAdapter(adapter);
		
		// Inflate the layout for this fragment
		return view;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
			case R.id.refresh_tab:
				String[] params = new String[] {"true", ""};
				ContactsRestHandler restHandler = new ContactsRestHandler(getActivity(), adapter);
				InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
				restHandler.execute(params);
				break;
			default:
				break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		
		List<ContactModel> contacts = InvoiceXpress.getInstance().getContactsSorted();
		if(contacts != null && !contacts.isEmpty()) {
			// inflate action bar options
			inflater.inflate(R.menu.action_bar_contacts_list, menu);
			SearchView searchView = (SearchView) menu.findItem(R.id.search_tab).getActionView();
			// set search view listener
			searchView.setOnQueryTextListener(new ContactsSearch(searchView));
		}
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private class ContactsSearch implements OnQueryTextListener {      

		private SearchView searchView;
		
		public ContactsSearch(SearchView searchView) {
			this.searchView = searchView;
		}
		
	    @Override
	    public boolean onQueryTextChange(String newText) {
	    	if(InvoiceXpress.DEBUG) {
	    		Log.d(this.getClass().getCanonicalName(), "contacts search.. " + newText);
	    	}
	    	
	    	if(adapter != null) {
	    		adapter.getFilter().filter(newText);
	    	}
	    	
	        return true;
	    }

	    @Override
	    public boolean onQueryTextSubmit(String query) {
	    	((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchView.getWindowToken(), 0);
	        return true;
	    }

	}	
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// if there is no contacts then clear menu
		List<ContactModel> contacts = InvoiceXpress.getInstance().getContactsSorted();
		if(contacts == null || contacts.isEmpty()) {
			menu.clear();
		}
		
		super.onOptionsMenuClosed(menu);
	}	

}
