package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.activities.AuthenticationActivity;
import pt.rupeal.invoicexpress.model.AccountDetailsModel;
import pt.rupeal.invoicexpress.model.AccountModel;
import pt.rupeal.invoicexpress.model.AccountsModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AuthenticationRestHandler extends AsyncTask<String, Void, AccountsModel> {

	private String username;
	private String password;
	private StringBuffer responseXml;
	
	public AuthenticationRestHandler(Context context) {
		this.context = context;
	}
	
	@Override
	protected AccountsModel doInBackground(String... params) {
		
		username = params[0];
		password = params[1];
		
		AccountsModel accounts = new AccountsModel();
		
		try {
			// Login
			// the login pai operation will return all associated accounts 
			HttpPost httpPost = new HttpPost(InvoiceXpress.LOGIN_URL);
			
			StringEntity entity = new StringEntity(buildXmlRequest(), "UTF-8");
			entity.setContentType("application/xml; charset=utf-8");
			httpPost.setEntity(entity);				
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
			HttpResponse response = httpClient.execute(httpPost, new BasicHttpContext());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			
			responseXml = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				responseXml.append(line);
			}
			
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				// log
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), "Status Code: " + response.getStatusLine().getStatusCode());
					Log.d(this.getClass().getCanonicalName(), responseXml.toString());
				}
				// return
				return accounts;
			}
			
			// log
			if(InvoiceXpress.DEBUG) {
				Log.d(this.getClass().getCanonicalName(), responseXml.toString());
			}
			
			List<AccountModel> accountsList = getAccounts(responseXml.toString());
			// set accounts list
			accounts.setAccounts(accountsList);
			// set active account. it will be the first none blocked account
			AccountModel activeAccount = getActiveAccount(accountsList);
			// if there are no active account return error
			if(activeAccount == null) {
				throw new InvoiceXpressException(context, R.string.error_account_blocked);
			}
			
			accounts.setAccountActive(activeAccount);

			// start account details request
			HttpGet httpGet = new HttpGet(
					buildRequestHttpGet(accounts.getAccountActive().getUrl(), 
										accounts.getAccountActive().getApiKey()));
			
			httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
			response = httpClient.execute(httpGet, new BasicHttpContext());
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

			StringBuffer responseString = new StringBuffer();
			line = "";
			while ((line = reader.readLine()) != null) {
			    responseString.append(line);
			}
			
			if(InvoiceXpress.DEBUG) {
				Log.d(this.getClass().getCanonicalName(), responseString.toString());			
			}
			
			// set account details
			accounts.setAccountDetailsActive(getAccountDetails(responseString.toString()));
			
			// create a new account
			Account[] androidAccounts = InvoiceXpress.getInstance().getInvoiceXpressAccount();
			if(androidAccounts == null || androidAccounts.length == 0) {
				Account account = new Account(username, InvoiceXpress.ACCOUNT_ANDROID_INVOICEXPRESS_TYPE);  
				InvoiceXpress.getInstance().getAccountManager().addAccountExplicitly(account, password, null);
			}
	        
			// set accounts
	        InvoiceXpress.getInstance().setAccounts(accounts);
			
		} catch (UnsupportedEncodingException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_cant_login_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (ClientProtocolException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_cant_login_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_cant_login_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage());
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
		}
		
		return accounts;
	}
	
	private List<AccountModel> getAccounts(String xml) throws InvoiceXpressException {
		
		List<AccountModel> accounts = new ArrayList<AccountModel>();

		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);
		
		if(documentDomElement == null) {
			throw new InvoiceXpressException(context, R.string.error_bad_credentials);
		}
		
		try {
		
			NodeList accountsNodeList = documentDomElement.getElementsByTagName("account");
			for (int index = 0; index < accountsNodeList.getLength(); index++) {
				Node accountNode = accountsNodeList.item(index);
				
				AccountModel accountModel = new AccountModel();
				accountModel.setId(parser.getValue((Element) accountNode, "id"));
				accountModel.setName(parser.getValue((Element) accountNode, "name"));
				accountModel.setUrl(parser.getValue((Element) accountNode, "url"));
				accountModel.setApiKey(parser.getValue((Element) accountNode, "api_key"));
				accountModel.setState(parser.getValue((Element) accountNode, "state"));
				accountModel.setBlocked(Boolean.parseBoolean(parser.getValue((Element) accountNode, "blocked")));
				
				accounts.add(accountModel);
			}
			
		} catch(Exception e) {
			throw new InvoiceXpressException(context, R.string.error_cant_login_unexpected);
		}
		
		return accounts;
	}
	
	private AccountModel getActiveAccount(List<AccountModel> accounts) {
		for (AccountModel account : accounts) {
			if(!account.isBlocked()) {
				return account;
			}
		}
		
		return null;
	}
	
    private MessageFormat xmlRequest = new MessageFormat (
    		"<credentials>" +
    			"<login>{0}</login>" +
    			"<password>{1}</password>" +
    		"</credentials>");
    
    private String buildXmlRequest() throws InvoiceXpressException {
    	
    	Object[] args = {username, password};
    	String xmlRequestFormated = xmlRequest.format(args);
    	// log
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), xmlRequestFormated);
    	}
    	
    	return xmlRequestFormated;
    }
    
    private String buildRequestHttpGet(String url, String apiKey) {
    	
    	StringBuffer request = new StringBuffer(url + "/account_data");
    	request.append(".xml");
    	request.append("?api_key=" + apiKey);
    	// log
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }

	private AccountDetailsModel getAccountDetails(String xml_response) throws InvoiceXpressException {
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml_response);
		
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
    
    private static final String BAD_CREDENTIALS = "bad credentials";
    
    @Override
    protected void onPostExecute(AccountsModel result) {
    	super.onPostExecute(result);
		
    	if(existsError()) {
    		processError(R.string.error_cant_login_unexpected);
    		return;
    	}
    	
    	if(BAD_CREDENTIALS.equals(responseXml.toString())) {
			processError(R.string.error_bad_credentials);
			return;
    	}
    	
		if(result.getAccounts().isEmpty()) {
			processError(R.string.error_there_arent_active_accounts);
			return;
		}
		
		// clean all documents, contacts and dash board data
		InvoiceXpress.getInstance().clear();
		
		// if the context is MainActivity then the application just need to initialize the action bar
		if(context instanceof MainActivity) {
			((MainActivity) context).initActionBar();
		} else if(context instanceof AuthenticationActivity) {
			// else the execution of web service is called from Authentication activity
			// the application has to start main activity and finish the authentication activity
			AuthenticationActivity activity = (AuthenticationActivity) context;
			activity.setResult(Activity.RESULT_OK, new Intent());     
			// finish activity
			activity.finish();
		}
		
    }
    
    private void processError(int errorMessageId) {
    	// show error message
    	Toast.makeText(context, errorMessageId, Toast.LENGTH_LONG).show();
    	
    	if(context instanceof MainActivity) {
    		// show authentication activity
    		// get android account
			Account[] accounts = InvoiceXpress.getInstance().getInvoiceXpressAccount();
			if(accounts.length > 0) {
				// remove accounts data
				InvoiceXpress.getInstance().getAccounts().clear();
				InvoiceXpress.getInstance().getAccounts().addAll(new ArrayList<AccountModel>());
				InvoiceXpress.getInstance().setActiveAccount(null);
				InvoiceXpress.getInstance().setActiveAccountDetails(null);
				// remove android invoiceXpress account
				InvoiceXpress.getInstance().getAccountManager().removeAccount(accounts[0], null, null);
				
				// start authentication activity 
				Intent intent =  new Intent(context, AuthenticationActivity.class);
				((MainActivity) context).startActivityForResult(intent, 1);
			}

    	}
    }
    
}
