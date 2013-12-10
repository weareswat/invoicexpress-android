package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
import pt.rupeal.invoicexpress.adapters.DocumentListRowAdapter;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment.DocumentFilterFragment;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.DocumentsFilterModel;
import pt.rupeal.invoicexpress.model.DocumentsModel;
import pt.rupeal.invoicexpress.model.ItemModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DocumentsRestHandler extends AsyncTask<String, Void, DocumentsFilterModel> {

	private static final String REQUEST_NORMAL = "normal";
	private static final String REQUEST_MORE = "more";
	private static final String REQUEST_REFRESH = "refresh";
	
	private DocumentListRowAdapter adapter;
	
	private LinearLayout listViewFooter;
	
	private String docType;
	private String contactId;
	
	// the attribute requestType can be a refresh, a moreDocuments or a first normal request
	private String requestType;
	private int requestedFilterCode;
	
	public DocumentsRestHandler(Context context) {
		this.context = context;
		
		this.requestType = REQUEST_NORMAL;
	}

	/**
	 * Constructor for refresh requests.
	 */
	public DocumentsRestHandler(Context context, DocumentListRowAdapter adapter) {
		this.context = context;
		this.adapter = adapter;
		
		this.requestType = REQUEST_REFRESH;
	}
	
	/**
	 * Constructor for more request
	 */
	public DocumentsRestHandler(Context context, DocumentListRowAdapter adapter, LinearLayout listViewFooter) {
		this.context = context;
		this.adapter = adapter;
		this.listViewFooter = listViewFooter;
		
		this.requestType = REQUEST_MORE;
	}
	
	@Override
    protected void onPostExecute(DocumentsFilterModel result) {
		super.onPostExecute(result);

		// check if there is an error
    	if(existsError()) {
    		processError();
    		return;
    	}
    	
    	// set data depends of the request
    	if(contactId.isEmpty()) { 
    		InvoiceXpress.getInstance().setDocuments(docType, result);
    	} else {
    		InvoiceXpress.getInstance().getContacts().get(contactId).setDocuments(result);
    	}
    	
    	if(REQUEST_MORE.equals(requestType) || REQUEST_REFRESH.equals(requestType)) {
    		// refreshing just update active view..
    		processMoreOrRefreshRequest();
    	} else {
    		processNormalRequest();
    	}
    	
	}
	
	private void processMoreOrRefreshRequest() {
		DocumentsModel documentsModel = getActualDocuments().getDocuments().get(requestedFilterCode);
		adapter.setDocuments(documentsModel.getDocumentsSorted());
		adapter.notifyDataSetChanged();
		// for more request
		if(REQUEST_MORE.equals(requestType)) {
			
			int downloadedDocumentsCount = documentsModel.getDownloadedDocuments();
			((TextView) listViewFooter.findViewById(R.id.documents_more_docs_downloaded)).setText(
					downloadedDocumentsCount + " " + context.getResources().getString(R.string.doc_more_downloaded));
			
			int toUploadDocumentsCount = documentsModel.getTotalDocuments() - downloadedDocumentsCount;
			((TextView) listViewFooter.findViewById(R.id.documents_more_docs_to_upload)).setText(
					toUploadDocumentsCount + " " + context.getResources().getString(R.string.doc_more_to_upload));
			
			if(toUploadDocumentsCount == 0) {
				listViewFooter.setOnClickListener(null);
			}
		}
	}
	
	private void processNormalRequest() {
		// add a new fragment
    	if(contactId.isEmpty()) {
    		if(InvoiceXpress.DEBUG) {
    			Log.d(this.getClass().getCanonicalName(), "Show documents");
    		}
    		Bundle args = new Bundle();
    		args.putString(DocumentModel.DOC_TYPE, docType);
    		args.putString(ContactModel.ID, "");
    		// call fragment
    		((MainActivity) context).addFragment(DocumentsListFragment.class, 
    				FragmentTagsEnum.DOCUMENTS_LIST,
    				args);    		
    	} else {
    		if(InvoiceXpress.DEBUG) {
    			Log.d(this.getClass().getCanonicalName(), "Show documents for client: " + contactId);
    		}
    		// request from Client
    		// set extra contact id. this will be read in DocumentsListFragment
    		Bundle args = new Bundle();
    		args.putString(DocumentModel.DOC_TYPE, docType);
    		args.putString(ContactModel.ID, contactId);
    		// call fragment
    		((MainActivity) context).addFragment(DocumentsListFragment.class, 
    				FragmentTagsEnum.DOCUMENTS_LIST,
    				args);     		
    	}

	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * params[0] = docType; params[1] = client; params[2] = requestedFilterCode; params[3] = page
	 */
	@Override
	protected DocumentsFilterModel doInBackground(String... params) {
		// set docType
		docType = params[0];
		if(InvoiceXpress.DEBUG) {
			Log.d(this.getClass().getCanonicalName(), "Documetn Type: " + docType);
		}
		// set contact id
		contactId = params[1];
		if(InvoiceXpress.DEBUG) {
			Log.d(this.getClass().getCanonicalName(), "Contact id: " + contactId);
		}		
		// get page
		String page = params[3];
		if(InvoiceXpress.DEBUG) {
			Log.d(this.getClass().getCanonicalName(), "Page: " + page);
		}
		// get documents that will be updated
		DocumentsFilterModel documents = getActualDocuments();
		try {
			// get requestedFilterCode
			requestedFilterCode = Integer.valueOf(params[2]);
			switch (requestedFilterCode) {
				case DocumentFilterFragment.NO_FILTER:
					// requestType == NORMAL
					doInBackgroundFiltered(documents, DocumentFilterFragment.FILTER_CODE_ARCHIVED, page);
					doInBackgroundFiltered(documents, DocumentFilterFragment.FILTER_CODE_ALL, page);
					doInBackgroundFiltered(documents, DocumentFilterFragment.FILTER_CODE_OVER_DUE, page);
					break;
				case DocumentFilterFragment.FILTER_CODE_ARCHIVED:
					doBackgroundRefreshOrMore(documents, DocumentFilterFragment.FILTER_CODE_ARCHIVED, page);
					break;
				case DocumentFilterFragment.FILTER_CODE_ALL:
					doBackgroundRefreshOrMore(documents, DocumentFilterFragment.FILTER_CODE_ALL, page);
					break;
				case DocumentFilterFragment.FILTER_CODE_OVER_DUE:
					doBackgroundRefreshOrMore(documents, DocumentFilterFragment.FILTER_CODE_OVER_DUE, page);
					break;				
				default:
					break;
			}
			
		} catch(InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), e.getType());
		}
		
		return documents;
	}
	
	private void doInBackgroundFiltered(DocumentsFilterModel documents, int filterCode, String page) throws InvoiceXpressException {
		
		try {
			
			HttpGet httpGet = new HttpGet(contactId.isEmpty() 
					? buildRequestHttpGet(filterCode, page) 
							: buildContactRequestHttpGet(filterCode, page));
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
			HttpResponse response = httpClient.execute(httpGet, new BasicHttpContext());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

			StringBuffer responseString = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
			    responseString.append(line);
			}
			
			if(InvoiceXpress.DEBUG) {
				Log.d(DocumentsRestHandler.class.getCanonicalName(), responseString.toString());
			}
			
			// if there is documents then the application has to clear and replace oldest documents
			if((requestType.equals(REQUEST_MORE)
					|| requestType.equals(REQUEST_REFRESH))
						&& documents.getDocuments().containsKey(filterCode)) {
				// get actual documentsModel
				DocumentsModel documentsModel = documents.getDocuments().get(filterCode);
				// get downloaded documentsModel
				DocumentsModel documentsModelDownloaded = parseDocumentsResponse(responseString.toString());
				// set new info pages data from downloaded documents model to actual documents model
				documentsModel.setCurrentPage(documentsModelDownloaded.getCurrentPage());
				documentsModel.setTotalDocuments(documentsModelDownloaded.getTotalDocuments());
				documentsModel.setTotalPages(documentsModelDownloaded.getTotalPages());
				// just add download documents if we have a more request
				if(requestType.equals(REQUEST_MORE)) {
					documentsModel.addDownloadedDocuments(documentsModelDownloaded.getDownloadedDocuments());
				}
				// put download documents
				documentsModel.getDocuments().putAll(documentsModelDownloaded.getDocuments());
			} else {
				// if there is not documents for filter code then the application just put the new documents list
				// i'm sure that it is the first request, request type is NORMAL
				// put download documents by filter				
				documents.getDocuments().put(filterCode, parseDocumentsResponse(responseString.toString()));
			}
			
		} catch (ClientProtocolException e) {
			throw new InvoiceXpressException(context, R.string.error_documents_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			throw new InvoiceXpressException(context, R.string.error_documents_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (InvoiceXpressException e) {
			throw new InvoiceXpressException(context, R.string.error_documents_unexpected, InvoiceXpressErrorType.ERROR);
		}
	}
	
	private void doBackgroundRefreshOrMore(DocumentsFilterModel documents, int filterCode, String pageToMore) throws InvoiceXpressException {

		if(REQUEST_REFRESH.equals(requestType)) {
			documents.getDocuments().get(filterCode).getDocuments().clear();
			int currentPage = documents.getDocuments().get(filterCode).getCurrentPage();
			int pageNumber = 1;
			while(pageNumber <= currentPage) {
				doInBackgroundFiltered(documents, filterCode, String.valueOf(pageNumber));
				pageNumber++;
			}
		} else if(REQUEST_MORE.equals(requestType)) {
			doInBackgroundFiltered(documents, filterCode, pageToMore);
		}
	}
	
	/**
	 * Get documents. Getter from documents types fragment or get from specified contact.
	 * @return documents
	 */
	private DocumentsFilterModel getActualDocuments() {
    	if(contactId.isEmpty()) {
    		// generic documents
    		// return InvoiceXpress.getInstance().getDocuments();
    		return InvoiceXpress.getInstance().getDocuments(docType);
    	} else {
    		// contact documents
    		// contact list already selected
    		if(InvoiceXpress.getInstance().getContacts().containsKey(contactId)) {
    			return InvoiceXpress.getInstance().getContacts().get(contactId).getDocuments();
    		} else {
    			// there is no contact list yet
    			return new DocumentsFilterModel();
    		}
    	}
	}
	
    /**
     * Parse the xml response.
     * @param filterCode
     * @param xml
     * @return
     * @throws InvoiceXpressException 
     */
    private DocumentsModel parseDocumentsResponse(String xml) throws InvoiceXpressException {
	
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);

		DocumentsModel documentsModel = new DocumentsModel();
		
		readInfoPages(documentsModel, documentDomElement, parser);

		NodeList nodeList = documentDomElement.getElementsByTagName("invoice");
		// empty list results
		if(nodeList.getLength() == 0) {
			return new DocumentsModel();
		}
		
		Map<String, DocumentModel> documents = new HashMap<String, DocumentModel>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			DocumentModel document = new DocumentModel();
			
			Element elem = (Element) nodeList.item(i);
			document.setId(parser.getValue(elem, "id"));
			document.setSequenceNumber(parser.getValue(elem, "sequence_number"));
			document.setType(parser.getValue(elem, "type"));
			document.setStatus(parser.getValue(elem, "status"));
			document.setDate(parser.getValue(elem, "date"));
			document.setDueDate(parser.getValue(elem, "due_date"));
			
			document.setSum(Double.parseDouble(parser.getValue(elem, "sum")));
			document.setDiscount(Double.parseDouble(parser.getValue(elem, "discount")));
			document.setBeforeTaxes(Double.parseDouble(parser.getValue(elem, "before_taxes")));
			document.setTaxes(Double.parseDouble(parser.getValue(elem, "taxes")));			
			document.setTotal(Double.parseDouble(parser.getValue(elem, "total")));
			
			document.setObservations(parser.getValue(elem, "observations"));
			
			Node mb = parser.getNode(elem, "mb_reference");
			if(mb != null) {
				document.setPayRef(parser.getValue((Element) mb, "reference"));
				document.setPayEntity(parser.getValue((Element) mb, "entity"));
				document.setPayValue(parser.getValue((Element) mb, "value"));
			}
			
			Node client = parser.getNode(elem, "client");
			// for cash invoices or simplified invoices the client can be empty
			if(client != null) {
				document.setClientId(parser.getValue((Element) client, "id"));
				document.setClientName(parser.getValue((Element) client, "name"));
				document.setClientEmail(parser.getValue((Element) client, "email"));			
			} else {
				document.setClientId("");
				document.setClientName(context.getResources().getString(R.string.doc_details_final_costumer));
				document.setClientEmail("");
			}
			
			NodeList itemsNodeList = elem.getElementsByTagName("item");
			HashMap<String, ItemModel> itemsHashMap = new HashMap<String, ItemModel>(itemsNodeList.getLength());
			for (int j = 0; j < itemsNodeList.getLength(); j++) {
				Node itemNode = itemsNodeList.item(j);
				
				ItemModel itemDetails = new ItemModel();
				
				itemDetails.setName(parser.getValue((Element) itemNode, "name"));
				itemDetails.setDescription(parser.getValue((Element) itemNode, "description"));
				itemDetails.setUnitPrice(Double.parseDouble(parser.getValue((Element) itemNode, "unit_price")));
				itemDetails.setQuantity(Double.parseDouble(parser.getValue((Element) itemNode, "quantity")));
				itemDetails.setTaxAmount(Double.parseDouble(parser.getValue((Element) itemNode, "tax_amount")));
				itemDetails.setDiscount(Double.parseDouble(parser.getValue((Element) itemNode, "discount")));
				itemDetails.setSubTotal(Double.parseDouble(parser.getValue((Element) itemNode, "subtotal")));
				itemDetails.setTotal(Double.parseDouble(parser.getValue((Element) itemNode, "total")));
				
				itemsHashMap.put(itemDetails.getName(), itemDetails);
			}
			
			document.setItems(itemsHashMap);
			
			document.setArchived(Boolean.parseBoolean(parser.getValue(elem, "archived")));
			
			documents.put(document.getId(), document);
		}
		
		documentsModel.setDocuments(documents);
		documentsModel.setDownloadedDocuments(documents.size());
		
		return documentsModel;

    }
    
    /**
     * Read info pages from response. The response can have two different xml specification that depends on the request.
     * 
     * @param filterCode, filter code - 0 = archived, 1 = all and 2 = over due
     * @param documentDomElement, document dom element
     * @param parser, parser
     */
    private void readInfoPages(DocumentsModel documentsModel, Document documentDomElement, InvoiceXpressParser parser) {
    	
    	// read info pages from contact details fragment
    	if(documentDomElement.getElementsByTagName("results").item(0) != null) {
    		Node resultNode = documentDomElement.getElementsByTagName("results").item(0);
    		int currentPage = Integer.parseInt(parser.getValue((Element) resultNode, "current_page"));
    		int totalPages = Integer.parseInt(parser.getValue((Element) resultNode, "total_pages"));
    		int totalDocuments = Integer.parseInt(parser.getValue((Element) resultNode, "total_entries"));
    		
    		// DocumentsModel documentsContactModel = InvoiceXpress.getInstance().getContacts().get(contactId).getDocuments().getDocuments().get(filterCode);
    		documentsModel.setCurrentPage(currentPage);
    		documentsModel.setTotalPages(totalPages);
    		documentsModel.setTotalDocuments(totalDocuments);
    		
    	} else {
    		// read info pages from documents type fragment
	    	Node mainNode = documentDomElement.getElementsByTagName("invoices").item(0);
    		int currentPage = Integer.parseInt(parser.getValue((Element) mainNode, "current_page"));
    		int totalPages = Integer.parseInt(parser.getValue((Element) mainNode, "total_pages"));
    		int totalDocuments = Integer.parseInt(parser.getValue((Element) mainNode, "total_entries"));
    		
    		// DocumentsModel documentsModel = InvoiceXpress.getInstance().getDocuments().getDocuments().get(filterCode);
    		documentsModel.setCurrentPage(currentPage);
    		documentsModel.setTotalPages(totalPages);
    		documentsModel.setTotalDocuments(totalDocuments);
		}
    }
    
    private String buildRequestHttpGet(int filterCode, String page) {
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentsRestHandler.class.getCanonicalName(), "buildRequestHttpGet");
    	}

    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl());
    	request.append("/invoices.xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(!docType.equals(DocumentTypeEnum.ALL.getValue())) {
    		request.append("&filter[by_type][]=" + docType);
    	}
    	
    	if(filterCode == DocumentFilterFragment.FILTER_CODE_ARCHIVED) {
    		request.append("&filter[archived]=only_archived");
    	} else if(filterCode == DocumentFilterFragment.FILTER_CODE_OVER_DUE) {
    		request.append("&filter[overdue]=1");
    	}
    	
    	if(page != null && !page.isEmpty()) {
    		request.append("&page=" + page);
    	}
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentsRestHandler.class.getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }
    
    /**
     * Url example: https://screen-name.invoicexpress.net/clients/:client_id/invoices.xml
     * @return url
     */
    private String buildContactRequestHttpGet(int filterCode, String page) {
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentsRestHandler.class.getCanonicalName(), "buildContactRequestHttpGet");
    	}
    	
    	StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl());
    	request.append("/clients").append("/").append(contactId).append("/invoices.xml");
    	request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
    	
    	if(filterCode == DocumentFilterFragment.FILTER_CODE_ARCHIVED) {
    		request.append("&filter[archived]=only_archived");
    	} else if(filterCode == DocumentFilterFragment.FILTER_CODE_OVER_DUE) {
    		request.append("&filter[overdue]=1");
    	}
    	
    	if(page != null && !page.isEmpty()) {
    		request.append("&page=" + page);
    	}
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(DocumentsRestHandler.class.getCanonicalName(), request.toString());
    	}
    	
    	return request.toString();
    }
    
}
