package pt.rupeal.invoicexpress.fragments;

import java.util.List;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.adapters.DocumentListRowAdapter;
import pt.rupeal.invoicexpress.adapters.FragmentPagerAdapter;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.DocumentsModel;
import pt.rupeal.invoicexpress.server.DocumentsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.widgets.SearchView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class DocumentsListFragment extends Fragment {

	private ViewPager viewPager;
	private DocumentsFragmentPagerAdapter documentsPagerAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View viewLayout = inflater.inflate(R.layout.documents_main, container, false);
		viewPager = (ViewPager) viewLayout.findViewById(R.id.pager);
		
		((PagerTitleStrip) viewPager.findViewById(R.id.pager_title_strip)).setTextColor(getResources().getColor(R.color.horizontal_navigation_bar_text));
		((PagerTitleStrip) viewPager.findViewById(R.id.pager_title_strip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		((PagerTitleStrip) viewPager.findViewById(R.id.pager_title_strip)).setGravity(Gravity.CENTER_VERTICAL);
		((PagerTitleStrip) viewPager.findViewById(R.id.pager_title_strip)).setPadding(-70, 0, -55, 0);
		
		String docType = "";
		String contactId = "";
		if(getArguments() != null) {
			docType = getArguments().getString(DocumentModel.DOC_TYPE, "");
			contactId = getArguments().getString(ContactModel.ID, "");
		}
		
		documentsPagerAdapter = new DocumentsFragmentPagerAdapter(getFragmentManager(), docType, contactId);
		// set adapter
		new SetAdapterTask().execute();
		// return view
		return viewLayout;
	}
	
	private class SetAdapterTask extends AsyncTask<Void,Void,Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
	    @Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	    
	    @Override
	    protected void onPostExecute(Void result) {
			viewPager.setAdapter(documentsPagerAdapter);
			viewPager.setCurrentItem(1);
	    }
	    
	}
	
	private final int[] documents_filters = new int[] {R.string.doc_filter_archived, R.string.doc_filter_all, R.string.doc_filter_expired};
	
	public class DocumentsFragmentPagerAdapter extends FragmentPagerAdapter {
	    
		private String docType;
		private String contactId;
		
		public DocumentsFragmentPagerAdapter(FragmentManager fm, String docType, String contactId) {
			super(fm);
			this.docType = docType;
			this.contactId = contactId;
	    }
		
	    @Override
	    public Fragment getItem(int position) {
	    	// create new fragment
	        Fragment fragment = new DocumentFilterFragment();
	        // set filter argument
	        Bundle args = new Bundle();
	        args.putInt(DocumentFilterFragment.FILTER_CODE, position);
	        args.putString(DocumentModel.DOC_TYPE, docType);
	        args.putString(ContactModel.ID, contactId);
	        // set argumetns
	        fragment.setArguments(args);
	        // return fragment
	        return fragment;
	    }
	    
	    @Override
	    public int getCount() {
	        return documents_filters.length;
	    }
	    
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	return getResources().getString(documents_filters[position]);
	    }
	    
	}

	public static class DocumentFilterFragment extends Fragment {
	    
		public static final String FILTER_CODE = "filter";

	    public static final int NO_FILTER = -1;
	    public static final int FILTER_CODE_ARCHIVED = 0;
	    public static final int FILTER_CODE_ALL = 1;
	    public static final int FILTER_CODE_OVER_DUE = 2;		
		
	    private int filterCode;
	    
		private DocumentListRowAdapter adapter;
//		private LinearLayout documentsMoreLayout;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        // enable onCreateOptionsMenu
	        setHasOptionsMenu(true);
		}
		
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    	// get documents model
	    	DocumentsModel documentsModel = getDocuments();
	    	// check if there are no documents
	    	if(documentsModel == null || documentsModel.getDocumentsSorted().isEmpty()) {
		        return inflater.inflate(R.layout.documents_list_empty, container, false);
	        }
	    	// get sorted documents list
	    	List<DocumentModel> documentsByFilterCode = documentsModel.getDocumentsSorted();
	        // inflate view to show documents list
	        View view = inflater.inflate(R.layout.documents_list, container, false);
	        ListView documentsListView = (ListView) view.findViewById(R.id.documents_list);

	        // add the upload more documents widget
	        LinearLayout documentsMoreLayout = (LinearLayout) inflater.inflate(R.layout.documents_more, documentsListView, false);
        	documentsMoreLayout.setLayoutParams(new AbsListView.LayoutParams(
        			AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        	
        	int downloadedDocuments = documentsModel.getDownloadedDocuments();
        	((TextView) documentsMoreLayout.findViewById(R.id.documents_more_docs_downloaded)).setText(
        			downloadedDocuments + " " + getResources().getString(R.string.doc_more_downloaded));
        	
        	int toUploadDocuments = documentsModel.getTotalDocuments() - downloadedDocuments;
        	((TextView) documentsMoreLayout.findViewById(R.id.documents_more_docs_to_upload)).setText(
        			toUploadDocuments + " " + getResources().getString(R.string.doc_more_to_upload));
        	
        	documentsListView.addFooterView(documentsMoreLayout);
	        
	        // get adapter
	         adapter = new DocumentListRowAdapter(getActivity(), documentsByFilterCode, documentsMoreLayout);
	         documentsListView.setAdapter(adapter);
	         
	        if(toUploadDocuments > 0) {
	        	documentsMoreLayout.setOnClickListener(new MoreButtonListener(documentsMoreLayout));
	        }
	        
	        return view;
	    }
	    
	    private DocumentsModel getDocuments() {
	    	filterCode = getArguments().getInt(FILTER_CODE);
	    	String docType = getArguments().getString(DocumentModel.DOC_TYPE, "");
	    	String contactId = getArguments().getString(ContactModel.ID, "");
	    	if(contactId.isEmpty()) {
	    		return InvoiceXpress.getInstance().getDocuments(docType).getDocuments().get(filterCode);
	    	} else {
	    		return InvoiceXpress.getInstance().getContacts().get(contactId).getDocuments().getDocuments().get(filterCode);
	    	}
	    }
	    
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    	// clear menu
	    	menu.clear();
	    	
	    	DocumentsModel documentsModel = getDocuments();
	    	if(documentsModel != null) {
	    		List<DocumentModel> documentsByFilterCode = documentsModel.getDocumentsSorted();
	    		if(documentsByFilterCode != null && !documentsByFilterCode.isEmpty()) {
	    			// update menu with refresh and search icons
	    			inflater.inflate(R.menu.action_bar_documents_list, menu);
	    			SearchView searchView = (SearchView) menu.findItem(R.id.search_tab).getActionView();
	    			// set documents search listener
	    			searchView.setOnQueryTextListener(new DocumentsSearch(searchView));
	    		}
	    	}
	    }
	    
	    @Override
	    public void onOptionsMenuClosed(Menu menu) {
	    	// clear menu if there are no documents
	    	DocumentsModel documentsModel = getDocuments();
	    	if(documentsModel != null) {
	    		List<DocumentModel> documentsByFilterCode = documentsModel.getDocumentsSorted();
	    		if(documentsByFilterCode == null || documentsByFilterCode.isEmpty()) { 
	    			menu.clear();
	    		}
	    	}
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	switch (item.getItemId()) {
				case R.id.refresh_tab:
					// get docType
					String docType = getArguments().getString(DocumentModel.DOC_TYPE, "");
					// get clientId
					String clientId = getArguments().getString(ContactModel.ID, "");
					// set parameters
					String[] params = new String[]{docType, clientId, 
							String.valueOf(filterCode), ""};
					// execute web service
					DocumentsRestHandler resthandler = new DocumentsRestHandler(getActivity(), adapter);
					InvoiceXpress.getInstance().setAsyncTaskActive(resthandler);
					resthandler.execute(params);
					break;
				default:
					break;
			}

	    	return super.onOptionsItemSelected(item);
	    }
	    
	    private class MoreButtonListener implements View.OnClickListener {
	    	
	    	private LinearLayout listViewFooter;
	    	
	    	public MoreButtonListener(LinearLayout listViewFooter) {
	    		this.listViewFooter = listViewFooter;
	    	}
	    	
	    	@Override
	    	public void onClick(View v) {
				// check if the progress bar is visible to disable clicks
				if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
					return;
				}
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
				// get docType
				String docType = getArguments().getString(DocumentModel.DOC_TYPE, "");
				// get clientId
				String clientId = getArguments().getString(ContactModel.ID, "");				
				// get page wanted
				int currentPage = InvoiceXpress.getInstance().getDocuments(docType).getDocuments().get(filterCode).getCurrentPage();
				// set parameters
				String[] params = new String[]{
						docType, 
						clientId, 
						String.valueOf(filterCode), 
						String.valueOf(currentPage + 1)};
				// execute web service
				DocumentsRestHandler restHandler = new DocumentsRestHandler(getActivity(), adapter, listViewFooter);
				InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
				restHandler.execute(params);
	    	}

	    }
	    
		private class DocumentsSearch implements OnQueryTextListener {       

			private SearchView searchView;
			
			public DocumentsSearch(SearchView searchView) {
				this.searchView = searchView;
			}
			
		    @Override
		    public boolean onQueryTextChange(String newText) {
		    	
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
		};		    
	}
}
