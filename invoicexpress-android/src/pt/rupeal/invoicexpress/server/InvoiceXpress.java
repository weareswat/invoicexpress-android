package pt.rupeal.invoicexpress.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.DashBoardFragment.DashBoardFilterFragment;
import pt.rupeal.invoicexpress.model.AccountModel;
import pt.rupeal.invoicexpress.model.AccountDetailsModel;
import pt.rupeal.invoicexpress.model.AccountsModel;
import pt.rupeal.invoicexpress.model.ChartModel;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentsFilterModel;
import pt.rupeal.invoicexpress.model.FragmentNavigationModel;
import pt.rupeal.invoicexpress.utils.ContactsComparable;
import pt.rupeal.invoicexpress.utils.StatusGraphs;
import pt.rupeal.invoicexpress.utils.StringUtil;

public class InvoiceXpress {

	public static final boolean DEBUG = false;
		
	public static final String INVOICE_EXPRESS_URL = "https://www.invoicexpress.net";
	public static final String LOGIN_URL = INVOICE_EXPRESS_URL + "/login.xml";
	public static final String EMAIL_INVOICEXPRESS_SUPPORT = "support@invoicexpress.com";
	
	public static final String ACCOUNT_ANDROID_INVOICEXPRESS_TYPE = "pt.rupeal.invoicexpress";

	private static final boolean USE_PROXY = false;
	private static final int TIMEOUT_CONNECTION = 5000;
	private static final int TIMEOUT_SOCKET = 5000;
	
	private static InvoiceXpress instance;
	
	// screen width and height
	private int width = -1;
	private int height = -1;
	
	private AccountManager accountManager;
	
	/**
	 * accounts management
	 */
	private AccountsModel accounts;
	
	/**
	 * for back button
	 */
	private List<FragmentNavigationModel> fragmentsTags;
	
	private ChartModel charts;
	private boolean isChartsRequested;
	
	private Map<String, DocumentsFilterModel> documents;
	
	private Map<String, ContactModel> contacts;
	private boolean isContactsRequested;
	
	private StatusGraphs statusGraphs;
	
	private AsyncTask<?, ?, ?> asyncTaskActive;

	private InvoiceXpress() {
		// fragments for back
		fragmentsTags = new ArrayList<FragmentNavigationModel>();
		// dash board
		charts = new ChartModel();		
		// account
		accounts = new AccountsModel();
		// documents
		initDocuments();
		// contacts
		contacts = new HashMap<String, ContactModel>();
	}
	
	public static InvoiceXpress getInstance() {
		if(instance == null){
			instance = new InvoiceXpress();
		}
		
		return instance;
	}
	
	public void clear() {
		// dash board
		charts = new ChartModel();
		isChartsRequested = false;
		// documents
		initDocuments();
		// contacts
		contacts = new HashMap<String, ContactModel>();
		isContactsRequested = false;
	}
	
	public List<FragmentNavigationModel> getFragments() {
		return fragmentsTags;
	}
	
	public FragmentNavigationModel getLastFragment() {
		if(!fragmentsTags.isEmpty()) {
			return fragmentsTags.get(fragmentsTags.size() - 1);
		} else {
			return null;
		}
	}
	
	public boolean hasFragment() {
		return !fragmentsTags.isEmpty();
	}
	
	public boolean hasOneLastFragment() {
		return fragmentsTags.size() == 1;
	}	
	
	/**
	 * Get fragments size is used to get a valid number and help me to create a mechanism of fragments management.
	 * With this method i can assure that it is generated an unique fragment name for each fragment, even the new fragment is already in used. 
	 * @return
	 */
	public int getFragmentsSize() {
		return fragmentsTags.size();
	}
	
	
	public FragmentNavigationModel getFragmentNavModelByFragmentTag(String fragmentTag) {
		for (FragmentNavigationModel fragmentNavModel : fragmentsTags) {
			if(fragmentNavModel.getFragmentTag().equals(fragmentTag)) {
				return fragmentNavModel;
			}
		}
		
		return null;
	}	

	public final int getScreenWidth(Activity activity) {
		if(width == -1) {
			width = activity.getWindowManager().getDefaultDisplay().getWidth(); 
		}
		
		return width;
	}
	
	public final int getScreenHeight(Activity activity) {
		if(height == -1) {
			height = activity.getWindowManager().getDefaultDisplay().getHeight();
		}
		
		return height;
	}	
	
	public static HttpParams getHttpParameters() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		HttpConnectionParams.setConnectionTimeout(httpParameters, InvoiceXpress.TIMEOUT_CONNECTION);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, InvoiceXpress.TIMEOUT_SOCKET);
		
		if(USE_PROXY) {
			HttpHost proxy = new HttpHost("10.159.32.155", 8080); 
			httpParameters.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		
		return httpParameters;
	}
	
	public void setAccountManager(AccountManager accountmanager) {
		this.accountManager = accountmanager;
	}
	
	public AccountManager getAccountManager() {
		return accountManager; 
	}
	
	public Account[] getInvoiceXpressAccount() {
		return accountManager.getAccountsByType(ACCOUNT_ANDROID_INVOICEXPRESS_TYPE);
	}
	
	public AccountsModel getAccount() {
		return accounts;
	}
	
	public void setAccounts(AccountsModel accounts) {
		this.accounts = accounts;
	}
	
	public List<AccountModel> getAccounts() {
		return accounts.getAccounts();
	}
	
	public void setAccounts(List<AccountModel> accounts) {
		this.accounts.getAccounts().addAll(accounts);
	}
	
	public AccountModel getActiveAccount() {
		return accounts.getAccountActive();
	}
	
	public void setActiveAccount(AccountModel account) {
		accounts.setAccountActive(account);
	}	
	
	public AccountDetailsModel getActiveAccountDetails() {
		return accounts.getAccountDetailsActive();
	}

	public void setActiveAccountDetails(AccountDetailsModel activeAccountDetails) {
		accounts.setAccountDetailsActive(activeAccountDetails);
	}
	
	private void initDocuments() {
		documents = new HashMap<String, DocumentsFilterModel>();
		documents.put(DocumentTypeEnum.ALL.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.INVOICE.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.CASHINVOICE.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.CREDITNOTE.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.DEBITNOTE.getValue(), new DocumentsFilterModel());
		documents.put(DocumentTypeEnum.RECEIPT.getValue(), new DocumentsFilterModel());} 
	
	public DocumentsFilterModel getDocuments(String docType) {
		return documents.get(docType);
	}
	
	public void setDocuments(String docType, DocumentsFilterModel documents) {
		this.documents.put(docType, documents);
	}
	
	public boolean existsDocumentsTemp(String docType) {
		return documents.get(docType) != null 
				&& documents.get(docType).getDocuments() != null
				&& !documents.get(docType).getDocuments().isEmpty();
	}	
	
	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getStatusGraphs(String documentType) {
		if(statusGraphs == null) {
			statusGraphs = new StatusGraphs();
		}
		
		if(DocumentTypeEnum.CASHINVOICE.getValue().equals(documentType)) { 
			return statusGraphs.getCashInvoiceGraph();
		} else if(DocumentTypeEnum.RECEIPT.getValue().equals(documentType)) {
			return statusGraphs.getReceiptGraph();
		} else if(DocumentTypeEnum.CREDITNOTE.getValue().equals(documentType)) { 
			return statusGraphs.getCreditNoteGraph();
		} else if(DocumentTypeEnum.DEBITNOTE.getValue().equals(documentType)) {
			return statusGraphs.getDebitNoteGraph();
		} else if(DocumentTypeEnum.INVOICE.getValue().equals(documentType)) { 
			return statusGraphs.getInvoiceGraph();
		} else if(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue().equals(documentType)) { 
			return statusGraphs.getSimplifiedInvoiceGraph();
		}
		
		return null;
	}

	public ChartModel getCharts() {
		return charts;
	}

	public void setCharts(ChartModel charts) {
		this.charts = charts;
	}
	
	public void setCharts(ChartModel charts, int filterCode) {
		switch (filterCode) {
			case DashBoardFilterFragment.FILTER_CODE_INVOICING:
				this.charts.setInvoicingChartData(charts.getInvoicingChartData());
				break;
			case DashBoardFilterFragment.FILTER_CODE_TREASURY:
				this.charts.setTreasuryChartData(charts.getTreasuryChartData());
				break;
			case DashBoardFilterFragment.FILTER_CODE_QUARTERLY:
				this.charts.setQuartersChartData(charts.getQuartersChartData());
				break;
			case DashBoardFilterFragment.FILTER_CODE_TOPDEBTORS:
				this.charts.setDebtorsChartData(charts.getDebtorsChartData());
				break;
			default:
				break;
		}
	}
	
	public boolean isChartsRequested() {
		return isChartsRequested;
	}
	
	public void setChartsRequested(boolean isChartsRequested) {
		this.isChartsRequested = isChartsRequested;
	}	

	public Map<String, ContactModel> getContacts() {
		return contacts;
	}

	public void setContacts(Map<String, ContactModel> contacts) {
		this.contacts = contacts;
	}
	
	public List<ContactModel> getContactsSorted() {
		List<ContactModel> contactsList = new ArrayList<ContactModel>(contacts.values());
 		Collections.sort(contactsList, new ContactsComparable());
		
		return sortContactsSetFirstAttribute(contactsList);
	}
	
	private List<ContactModel> sortContactsSetFirstAttribute(List<ContactModel> contacts){
		if(contacts.isEmpty()) {
			return contacts;
		}
		
		contacts.get(0).setFirst(true);
		for(int i=1; i < contacts.size(); i++) {
			if(StringUtil.getFirstCharInLowerCase(contacts.get(i).getName()) 
					!= StringUtil.getFirstCharInLowerCase(contacts.get(i-1).getName())) {
				contacts.get(i).setFirst(true);
			} else {
				continue;
			}
		}
		
		return contacts;
		
	}
	
	public boolean isContactsRequested() {
		return isContactsRequested;
	}
	
	public void setContactsRequested(boolean isContactsRequested) {
		this.isContactsRequested = isContactsRequested;
	}

	public static boolean isInvoiceXpressClickable(Context context) {
		Fragment progressbarFragment = ((Activity) context).getFragmentManager().findFragmentByTag(FragmentTagsEnum.DIALOG_PROGRESS.getValue());
		if(progressbarFragment != null) {
			return !progressbarFragment.isVisible();
		}
		
		return true;
	}
	
	public static boolean isPortugueseLocale() {
		return Locale.getDefault().getDisplayLanguage().equals("português");
	}

	public AsyncTask<?, ?, ?> getAsyncTaskActive() {
		return asyncTaskActive;
	}

	public void setAsyncTaskActive(AsyncTask<?, ?, ?> asyncTaskActive) {
		this.asyncTaskActive = asyncTaskActive;
	}

}
