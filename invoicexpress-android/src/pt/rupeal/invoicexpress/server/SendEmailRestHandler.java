package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SendEmailRestHandler extends AsyncTask<String, Void, Void> {

	public SendEmailRestHandler(Context context) {
		this.context = context;
	}
	
	@Override
    protected void onPostExecute(Void result) {
		super.onPostExecute(result);
    	// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	Toast.makeText(context, R.string.email_successfull_message, Toast.LENGTH_LONG).show();
    	
    	String lastFragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
    	((MainActivity) context).removeFragment(lastFragmentTag);
	}
	
	@Override
	protected Void doInBackground(String... params) {
		
		HttpPut httpPut = new HttpPut(buildRequestHttpPost(params));
		
		try {
			
			StringEntity entity = new StringEntity(buildXmlRequest(params), "UTF-8");
			entity.setContentType("application/xml; charset=UTF-8");
			httpPut.setEntity(entity);				
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());			
			HttpResponse response = httpClient.execute(httpPut, new BasicHttpContext());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			
			StringBuffer responseString = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				responseString.append(line);
			}
			
			if(InvoiceXpress.DEBUG) {
				Log.d(DocumentChangeStatusRestHandler.class.getCanonicalName(), responseString.toString());
			}
			
		} catch (UnsupportedEncodingException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_send_email_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (ClientProtocolException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_send_email_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(AuthenticationRestHandler.class.getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_send_email_unexpected, InvoiceXpressErrorType.ERROR);
		}
		
		return null;

	}

    /**
     * Example: https://:screen-name.invoicexpress.net/invoice/:invoice-id/email-invoice.xml
     * @return
     */
    private String buildRequestHttpPost(String... params) {
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl()); 
    	request.append(DocumentTypeEnum.getUrlOperations(params[4]));
    	request.append("/").append(params[3]);
    	request.append("/").append("email-invoice.xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentChangeStatusRestHandler.class.getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }  
    
    private MessageFormat xmlRequest = new MessageFormat (
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    		"<message>" +
    			"<client>" +
    				"<email>{0}</email>" +
    				"<save>1</save>" +
    			"</client>" +
    			// "<cc>ccguy@ clientcompany.com</cc>" +
    			// "<bcc>bccguy@ clientcompany.com</bcc>" +
    			"<subject>{1}</subject>" +
    			"<body>{2}</body>" +	
    		"</message>");
    
    private String buildXmlRequest(String... params) {
    	Object[] args = {params[0], params[1], params[2]};
    	String xmlRequestFormated = xmlRequest.format(args);
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentChangeStatusRestHandler.class.getCanonicalName(), xmlRequestFormated);
    	}
    	
    	return xmlRequestFormated;
    } 
	
}
