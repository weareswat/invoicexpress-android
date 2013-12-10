package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import pt.rupeal.invoicexpress.model.AccountModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import android.content.Context;
import android.util.Log;

public class AccountsRestHandler extends AsyncTask<Void, Void, List<AccountModel>> {

	public AccountsRestHandler(Context context) {
		this.context = context;
	}

	@Override
	protected void onPostExecute(List<AccountModel> result) {
		super.onPostExecute(result);

		// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	// this execution is always for refresh
    	// clear account list
    	InvoiceXpress.getInstance().getAccounts().clear();
    	// set account list
    	InvoiceXpress.getInstance().setAccounts(result);
		// refresh
		((MainActivity) context).refreshFragment();    	
	}
	
	@Override
	protected List<AccountModel> doInBackground(Void... params) {

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
			
			return getAccounts(responseString.toString());
			
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_accounts_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_accounts_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_accounts_unexpected, InvoiceXpressErrorType.ERROR);
		}
		
		return null;
		
	}
	
    private String buildRequestHttpGet() {
    	
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl() + "/users/accounts");
    	request.append(".xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }    
	
    private List<AccountModel> getAccounts(String xml) throws InvoiceXpressException {
    	
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);
		
		if(documentDomElement == null) {
			throw new InvoiceXpressException(context, R.string.error_bad_credentials);
		}
		
		NodeList accountsNode = documentDomElement.getElementsByTagName("account");
		List<AccountModel> accounts = new ArrayList<AccountModel>(accountsNode.getLength());
		
		for (int index = 0; index < accountsNode.getLength(); index++) {
			Node accountNode = accountsNode.item(index);
			
			AccountModel accountDTO = new AccountModel();
			accountDTO.setId(parser.getValue((Element) accountNode, "id"));
			accountDTO.setName(parser.getValue((Element) accountNode, "name"));
			accountDTO.setUrl(parser.getValue((Element) accountNode, "url"));
			accountDTO.setApiKey(parser.getValue((Element) accountNode, "api_key"));
			accountDTO.setState(parser.getValue((Element) accountNode, "state"));
			
			accounts.add(accountDTO);
		}
		
		return accounts;
	}

}
