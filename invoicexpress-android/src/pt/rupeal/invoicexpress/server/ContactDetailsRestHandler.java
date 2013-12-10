package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.ContactDetailsFragment;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class ContactDetailsRestHandler extends AsyncTask<String, Void, ContactModel> {

	private boolean isRefreshing;
	
	public ContactDetailsRestHandler(Context context) {
		this.context = context;
	}
	
	@Override
    protected void onPostExecute(ContactModel result) {
		super.onPostExecute(result);

		// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	if(isRefreshing) {
    		// update contacts map
    		InvoiceXpress.getInstance().getContacts().put(result.getId(), result);
    		// put id Client
			Bundle args = new Bundle();
			args.putSerializable(ContactModel.CONTACT, result);
    		// set contact details fragment
    		((MainActivity) context).refreshFragment(args);
    	} else {
    		// put contact
    		InvoiceXpress.getInstance().getContacts().put(result.getId(), result);    		
    		// set contact to arguments
			Bundle args = new Bundle();
			args.putSerializable(ContactModel.CONTACT, result);
			// add contact details fragment
    		((MainActivity) context).addFragment(ContactDetailsFragment.class, 
					FragmentTagsEnum.CONTACTS_DETAILS,
					args);
    	}
	}
	
	@Override
	protected ContactModel doInBackground(String... params) {

		String clientId = params[0];
		isRefreshing = Boolean.parseBoolean(params[1]);
		
		HttpGet httpGet = new HttpGet(buildRequestHttpGet(clientId));
		
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
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
			
			return getContactDetails(responseString.toString());
			
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_contact_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_contact_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
		}
		
		return null;
		
	}
	
    private String buildRequestHttpGet(String clientId) {
    	
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl() + "/clients/");
    	request.append(clientId);
    	request.append(".xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }    
	
    private ContactModel getContactDetails(String xml) throws InvoiceXpressException {
		
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);
		
		ContactModel contact = new ContactModel();
		
		NodeList nodeList = documentDomElement.getElementsByTagName("client");

		Element elem = (Element) nodeList.item(0);
		contact.setId(parser.getValue(elem, "id"));
		contact.setName(parser.getValue(elem, "name"));
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
		
		return contact;
			
	}
    
}
