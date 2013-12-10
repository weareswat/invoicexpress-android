package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.SortedMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.DocumentsModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class DocumentChangeStatusRestHandler extends AsyncTask<String, Void, String> {

	private DocumentModel document;
	
	private StringBuffer responseXml;
	
	public DocumentChangeStatusRestHandler(Context context, DocumentModel document) {
		this.context = context;
		this.document = document;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		HttpPut httpPut = new HttpPut(buildRequestHttpPost());
		
		try {
			
			StringEntity entity = new StringEntity(buildXmlRequest(params), "UTF-8");
			entity.setContentType("application/xml; charset=utf-8");
			httpPut.setEntity(entity);				
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
			HttpResponse response = httpClient.execute(httpPut, new BasicHttpContext());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			
			responseXml = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				responseXml.append(line);
			}
			
			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), "Status Code: " + response.getStatusLine().getStatusCode());
					Log.d(this.getClass().getCanonicalName(), responseXml.toString());
				}
				return "";
			}

			if(InvoiceXpress.DEBUG) {
				Log.d(this.getClass().getCanonicalName(), responseXml.toString());
			}
			
			return parseNewStatus();
			
		} catch (UnsupportedEncodingException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_change_status_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (ClientProtocolException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_change_status_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(R.string.error_change_status_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
		}
		
		return "";

	}
	
    private String buildRequestHttpPost() {
    	
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl()); 
    	request.append(DocumentTypeEnum.getUrlOperations(document.getType()));
    	request.append("/").append(document.getId());
    	request.append("/").append("change-state.xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentChangeStatusRestHandler.class.getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    	
    }
    
    private MessageFormat xmlRequest = new MessageFormat (
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    		"<invoice>" +
    			"<state>{0}</state>" +
    			"<message>{1}</message>" +
    		"</invoice>");
    
    private String buildXmlRequest(String... params) {
    	Object[] args = {params[0], params[1]};
    	String xmlRequestFormated = xmlRequest.format(args);
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), xmlRequestFormated);
    	}
    	
    	return xmlRequestFormated;
    }
    
    private String parseNewStatus() throws InvoiceXpressException {
    	InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(responseXml.toString());
		NodeList nodeList = documentDomElement.getElementsByTagName(DocumentTypeEnum.getTagXmlMainChildsByDocType(document.getType()));
		
		return parser.getValue((Element) nodeList.item(0), "state");
    }
    
    @Override
    protected void onPostExecute(String result) {
		super.onPostExecute(result);

		// if the result is empty then an error will be showed
		if(result.isEmpty()) {
			processReturnedError();
		}
    	// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	// if the document was deleted then the application will delete it and will show the documents list
    	if(DocumentStatusEnum.isDeleted(result)) {
    		// remove cached document
    		// find where is the document and delete it from application
    		deleteDocument();
    		// hide document details and show documents list
    		String lastFragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
    		((MainActivity) context).removeFragment(lastFragmentTag);
    		
    	} else {
    		// set new status
    		document.setStatus(result);
    		// refresh fragment
	    	Bundle args = new Bundle();
	    	args.putSerializable(DocumentModel.DOCUMENT, document);
	    	((MainActivity) context).refreshFragment(args);
    	}
    }
    
    /**
     * Delete document from cache application.
     * @return if a document was deleted then return true else return false 
     */
    private boolean deleteDocument() {
    	
    	boolean result = false;
    	// try to find document in documents map
    	// SortedMap<Integer, DocumentsModel> documentsSortedMap = InvoiceXpress.getInstance().getDocuments().getDocuments();
    	// delete document from all documents list
    	SortedMap<Integer, DocumentsModel> documentsAllSortedMap = InvoiceXpress.getInstance().getDocuments(DocumentTypeEnum.ALL.getValue()).getDocuments();
    	if(!documentsAllSortedMap.isEmpty()) {
    		Collection<DocumentsModel> filterKeys = documentsAllSortedMap.values();
    		for (DocumentsModel documentsModel : filterKeys) {
    			DocumentModel documentDeleted = documentsModel.getDocuments().remove(document.getId());
    			// log
    			if(InvoiceXpress.DEBUG) { 
    				if(documentDeleted != null) {
	    				Log.d(this.getClass().getCanonicalName(), "Document with sequenceNumber: " + documentDeleted.getSequenceNumber() + " was deleted from documents cache.");
	    				break;
    				}
    			}
			}
    	}
    	// delete document document from respective documents type list
    	SortedMap<Integer, DocumentsModel> documentsTypeSortedMap = InvoiceXpress.getInstance().getDocuments(document.getType()).getDocuments();
    	if(!documentsTypeSortedMap.isEmpty()) {
    		Collection<DocumentsModel> filterKeys = documentsTypeSortedMap.values();
    		for (DocumentsModel documentsModel : filterKeys) {
    			DocumentModel documentDeleted = documentsModel.getDocuments().remove(document.getId());
    			// log
    			if(InvoiceXpress.DEBUG) { 
    				if(documentDeleted != null) {
    					Log.d(this.getClass().getCanonicalName(), "Document with sequenceNumber: " + documentDeleted.getSequenceNumber() + " was deleted from documents cache.");
    					break;
    				}
    			}
			}
    	}
    	// try to find document in contacts documents map
    	Collection<ContactModel> contacts = InvoiceXpress.getInstance().getContacts().values();
    	for (ContactModel contact : contacts) {
    		Collection<DocumentsModel> filterKeys = contact.getDocuments().getDocuments().values();
    		for (DocumentsModel documentsModel : filterKeys) {
    			DocumentModel documentDeleted = documentsModel.getDocuments().remove(document.getId());
    			// log
    			if(InvoiceXpress.DEBUG) { 
    				if(documentDeleted != null) {
    					result = result || true;
    					Log.d(this.getClass().getCanonicalName(), "Document with sequenceNumber: " + documentDeleted.getSequenceNumber() + " was deleted from documents cache.");
    					break;
    				}
    			}
			}
		}
    	
    	return result;
    }
    
    private static final String ERROR_DATE = "Date can not be before last sent invoice of this sequence ";
    private static final String ERROR_CANCEL_MESSAGE_EMPTY = "Cancel message can't be blank";
    private static final String ERROR_ALREADY_SENT_TO_AT = "Cannot cancel, document already sent to AT";
    
    private void processReturnedError() {
    	
    	try {
			InvoiceXpressParser parser = new InvoiceXpressParser(context);
			Document documentDomElement = parser.getDomElement(responseXml.toString());
			NodeList errorsList = documentDomElement.getElementsByTagName("errors");
			Node error = errorsList.item(0);
			String errorDescr = parser.getValue((Element) error, "error");
	
	    	if(errorDescr.contains(ERROR_DATE)) {
	    		String msg = context.getResources().getString(R.string.error_change_status_date) 
	    				+ " " + errorDescr.substring(ERROR_DATE.length(), errorDescr.length());
	    		setError(msg, InvoiceXpressErrorType.ERROR);
	    	} else if(errorDescr.equals(ERROR_CANCEL_MESSAGE_EMPTY)) {
	    		setError(R.string.error_change_status_cancel_empty_message, InvoiceXpressErrorType.ERROR);
	    	}  else if(errorDescr.equals(ERROR_ALREADY_SENT_TO_AT)) {
	    		setError(R.string.error_change_status_already_sent_to_at, InvoiceXpressErrorType.ERROR);
	    	}
    	} catch (InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), InvoiceXpressErrorType.ERROR);
    	}
    	
    }

}
