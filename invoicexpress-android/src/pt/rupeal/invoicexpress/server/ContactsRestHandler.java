package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.adapters.ContactListRowAdapter;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.ContactsFragment;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import pt.rupeal.invoicexpress.utils.StringUtil;

import android.content.Context;
import android.util.Log;

public class ContactsRestHandler extends AsyncTask<String, Void, Map<String, ContactModel>> {

	private ContactListRowAdapter adapter;
	
	private boolean isRefresh;
	
	public ContactsRestHandler(Context context) {
		this.context = context;
	}
	
	/**
	 * Constructor for refresh requests.
	 */
	public ContactsRestHandler(Context context, ContactListRowAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
	}
	
	@Override
    protected void onPostExecute(Map<String, ContactModel> result) {
		super.onPostExecute(result);

		// check if there is an error
    	if(existsError()) {
    		processError();
    		if(isRefresh) {
    			return;
    		}
    		
    		InvoiceXpress.getInstance().setContactsRequested(false);
    	} else {
    		InvoiceXpress.getInstance().setContactsRequested(true);
    	}
    	// set data
    	InvoiceXpress.getInstance().setContacts(result);
    	// check if is a refresh
    	if(isRefresh) {
    		adapter.setContacts(InvoiceXpress.getInstance().getContactsSorted());
    		adapter.notifyDataSetChanged();
    	} else {
    		// call fragment
    		((MainActivity) context).addFragment(ContactsFragment.class, 
    				FragmentTagsEnum.CONTACTS,
    				null);    	
    	}
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * params[0] = isRefresh params[1] = page
	 */
	protected Map<String, ContactModel> doInBackground(String... params) {
		// check if is a refresh	
		isRefresh = Boolean.parseBoolean(params[0]);
		// initialize contacts structure
		Map<String, ContactModel> contacts = new HashMap<String, ContactModel>();
		
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());

			boolean hasMoreContacts = true;
			int page = 1;
			while(hasMoreContacts) {
				
				HttpGet httpGet = new HttpGet(buildRequestHttpGet(String.valueOf(page)));
				HttpResponse response = httpClient.execute(httpGet, new BasicHttpContext());
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				
				StringBuffer responseString = new StringBuffer();
				String line;
				while ((line = reader.readLine()) != null) {
					responseString.append(line);
				}
				
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), responseString.toString());
				}
				
				Map<String, ContactModel> contactsPerPage = getContacts(responseString.toString());
				if(contactsPerPage.isEmpty()) {
					hasMoreContacts = false;
				} else {
					contacts.putAll(contactsPerPage);
				}
				
				page++;
			}
			
			
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_contacts_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_contacts_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
		}
		
		return contacts;
	}
	
    private String buildRequestHttpGet(String page) {
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl() + "/clients.xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(!page.isEmpty()){
    		request.append("&page=" + page);
    	}
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }
    
	private Map<String, ContactModel> getContacts(String xml) throws InvoiceXpressException {
	
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);

		NodeList nodeList = documentDomElement.getElementsByTagName("client");
		
		Map<String, ContactModel> contactsMap = new HashMap<String, ContactModel>();
		
		// empty list results
		if(nodeList.getLength() == 0){
			return contactsMap;
		}

		List<ContactModel> contacts = new ArrayList<ContactModel>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			ContactModel contact = new ContactModel();
			
			Element elem = (Element) nodeList.item(i);
			contact.setId(parser.getValue(elem, "id"));
			// set contact with first character condition
			String contactName = StringUtil.setFirstCharacterToUpperCase(parser.getValue(elem, "name"));
			contact.setName(contactName);
			
			contact.setCode(parser.getValue(elem, "code"));
			contact.setEmail(parser.getValue(elem, "email"));
			contact.setAddress(parser.getValue(elem, "address"));
			contact.setPostalCode(parser.getValue(elem, "postal_code"));
			contact.setCountry(parser.getValue(elem, "country"));
			contact.setFiscalId(parser.getValue(elem, "fiscal_id"));
			contact.setWebsite(parser.getValue(elem, "website"));
			contact.setPhone(parser.getValue(elem, "phone"));
			contact.setFax(parser.getValue(elem, "fax"));
			
			Node preferred = parser.getNode(elem, "preferred_contact");
			contact.setPreferredName(parser.getValue((Element) preferred, "name"));
			contact.setPreferredEmail(parser.getValue((Element) preferred, "email"));
			contact.setPreferredPhone(parser.getValue((Element) preferred, "phone"));
			
			contact.setObservations(parser.getValue(elem, "observations"));
			contact.setSendOptions(parser.getValue(elem, "send_options"));						
			
			contacts.add(contact);
			
			contactsMap.put(contact.getId(), contact);
		}
		
		return contactsMap;
    }

}
