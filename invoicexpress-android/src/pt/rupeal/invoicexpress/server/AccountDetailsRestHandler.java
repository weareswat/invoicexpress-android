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
import pt.rupeal.invoicexpress.model.AccountDetailsModel;
import pt.rupeal.invoicexpress.model.AccountModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import android.content.Context;
import android.util.Log;

public class AccountDetailsRestHandler extends AsyncTask<Void, Void, AccountDetailsModel> {

	private AccountModel account;
	
	public AccountDetailsRestHandler(Context context, AccountModel account) {
		this.context = context;
		this.account = account;
	}
	
	@Override
	protected void onPostExecute(AccountDetailsModel result) {
		super.onPostExecute(result);
    	
		// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	// set active account
		InvoiceXpress.getInstance().setActiveAccount(account);
		// set new active account details
		InvoiceXpress.getInstance().setActiveAccountDetails(result);
		// clean all documents, contacts and dash board data
		InvoiceXpress.getInstance().clear();
		// refresh fragment
		((MainActivity) context).refreshFragment();
	}
	
	@Override
	protected void onCancelled(AccountDetailsModel result) {
		dismissProgressBar();
    	
		// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	// set active account
		InvoiceXpress.getInstance().setActiveAccount(account);
		// set new active account details
		InvoiceXpress.getInstance().setActiveAccountDetails(result);
		// clean all documents, contacts and dash board data
		InvoiceXpress.getInstance().clear();
		// select More Tab Bar
		((MainActivity) context).getActionBar().setSelectedNavigationItem(3);
	}
	
	@Override
	protected AccountDetailsModel doInBackground(Void... params) {
		
		HttpGet httpGet = new HttpGet(buildRequestHttpGet());
		
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
			
			AccountDetailsModel accountDetails = getAccountDetails(responseString.toString());
			if(accountDetails.isBlocked()) {
				throw new InvoiceXpressException(context, R.string.error_account_blocked);
			}
			
			return accountDetails;
			
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_account_switch_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_account_switch_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
		}
		
		return null;

	}
	
	private String buildRequestHttpGet() {

    	StringBuffer request = new StringBuffer(account.getUrl() + "/account_data");
    	request.append(".xml");
    	request.append("?api_key=" + account.getApiKey());
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }

	private AccountDetailsModel getAccountDetails(String reponse) throws InvoiceXpressException {
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(reponse);
		
		AccountDetailsModel accountDetail = new AccountDetailsModel();
		
		NodeList nodeList = documentDomElement.getElementsByTagName("account");

		Element elem = (Element) nodeList.item(0);
		accountDetail.setPlan(parser.getValue(elem, "plan"));
		accountDetail.setEntity(parser.getValue(elem, "organization_name"));
		accountDetail.setName(parser.getValue(elem, "name"));
		accountDetail.setPhone(parser.getValue(elem, "phone"));
		accountDetail.setFax(parser.getValue(elem, "fax"));
		accountDetail.setEmail(parser.getValue(elem, "email"));
		accountDetail.setAddress(parser.getValue(elem, "address"));
		accountDetail.setCity(parser.getValue(elem, "city"));
		accountDetail.setPostalCode(parser.getValue(elem, "postal_code"));
		accountDetail.setLocale(parser.getValue(elem, "locale"));
		accountDetail.setCountry(parser.getValue(elem, "country"));

		accountDetail.setBlocked(Boolean.parseBoolean(parser.getValue(elem, "blocked")));
		
		Node currency = parser.getNode(elem, "currency");
		accountDetail.setCurrencyName(parser.getValue((Element) currency, "name"));
		accountDetail.setCurrencySymbol(parser.getValue((Element) currency, "symbol"));
		
		return accountDetail;
	}
}
