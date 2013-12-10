package pt.rupeal.invoicexpress.enums;

import pt.rupeal.invoicexpress.R;

public enum DocumentTypeEnum {
	
	ALL ("all", -1, "/invoices.xml", "invoices", "", "invoice", -1),
	CASHINVOICE ("CashInvoice", R.drawable.icon_document_2, "/cash_invoices", "/cash_invoices", "cash_invoices", "cash_invoice", R.string.doc_type_cash),
	RECEIPT ("Receipt", R.drawable.icon_document_5, "/receipts", "/invoice", "", "receipt", R.string.doc_type_receipt),
	CREDITNOTE ("CreditNote", R.drawable.icon_document_3, "/credit_notes", "/credit_notes", "credit_notes", "credit_note", R.string.doc_type_credit),
	DEBITNOTE ("DebitNote", R.drawable.icon_document_1, "/debit_notes", "/debit_notes", "debit_notes", "debit_note", R.string.doc_type_debit),
	INVOICE ("Invoice", R.drawable.icon_document_4, "/invoices", "/invoice", "invoices", "invoice", R.string.doc_type_invoice),
	SIMPLIFIEDINVOICE ("SimplifiedInvoice", R.drawable.icon_document_2, "/simplified_invoices", "/simplified_invoices", "simplified_invoices", "simplified_invoice", R.string.doc_type_simple_invoice);
	
	private String value;
	private int drawableId;
	private String urlGetDocuments;
	private String urlOperations;
	private String tagXmlMain;
	private String tagXmlMainChilds;
	private int labelGui;
	
	private DocumentTypeEnum(String value, int drawableId, String urlGetDocuments, String urlOperations, String tagXmlMain, String tagXmlMainChilds, int labelGui){
		this.value = value;
		this.drawableId = drawableId;
		this.urlGetDocuments = urlGetDocuments;
		this.urlOperations = urlOperations;
		this.tagXmlMain = tagXmlMain;
		this.tagXmlMainChilds = tagXmlMainChilds;
		this.labelGui = labelGui;
	}

	public String getValue() {
		return value;
	}
	
	public int getDrawableId() {
		return drawableId;
	}
	
	public static int getDrawableId(String type){
		DocumentTypeEnum[] types = values();
		for (int i = 0; i < types.length; i++) {
			if(types[i].value.equals(type)){
				return types[i].getDrawableId();
			}
		}
		
		return -1;
	}
	
	private String getUrlGetDocuments(){
		return this.urlGetDocuments;
	}
	
    public static String getUrlGetDocuments(String docType) {

    	if(DocumentTypeEnum.CASHINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.CASHINVOICE.getUrlGetDocuments();
    	} else if(DocumentTypeEnum.RECEIPT.getValue().equals(docType)){
    		return DocumentTypeEnum.RECEIPT.getUrlGetDocuments();
    	} else if(DocumentTypeEnum.CREDITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.CREDITNOTE.getUrlGetDocuments();
    	} else if(DocumentTypeEnum.DEBITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.DEBITNOTE.getUrlGetDocuments();
    	} else if(DocumentTypeEnum.INVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.INVOICE.getUrlGetDocuments();
    	} else if(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.SIMPLIFIEDINVOICE.getUrlGetDocuments();
    	} else {
    		return "";
    	}
    }
    
    public static String getUrlOperations(String docType) {

    	if(DocumentTypeEnum.CASHINVOICE.getValue().equals(docType)) {
    		return DocumentTypeEnum.CASHINVOICE.urlOperations;
    	} else if(DocumentTypeEnum.RECEIPT.getValue().equals(docType)) {
    		return DocumentTypeEnum.RECEIPT.urlOperations;
    	} else if(DocumentTypeEnum.CREDITNOTE.getValue().equals(docType)) {
    		return DocumentTypeEnum.CREDITNOTE.urlOperations;
    	} else if(DocumentTypeEnum.DEBITNOTE.getValue().equals(docType)) {
    		return DocumentTypeEnum.DEBITNOTE.urlOperations;
    	} else if(DocumentTypeEnum.INVOICE.getValue().equals(docType)) {
    		return DocumentTypeEnum.INVOICE.urlOperations;
    	} else if(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue().equals(docType)) {
    		return DocumentTypeEnum.SIMPLIFIEDINVOICE.urlOperations;
    	} else {
    		return "";
    	}
    }    
	
	private String getTagXmlMain(){
		return tagXmlMain;
	}
	
    public static String getTagXmlMainByDocType(String docType) {
    	
    	if(DocumentTypeEnum.CASHINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.CASHINVOICE.getTagXmlMain();
    	} else if(DocumentTypeEnum.RECEIPT.getValue().equals(docType)){
    		return DocumentTypeEnum.RECEIPT.getTagXmlMain();
    	} else if(DocumentTypeEnum.CREDITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.CREDITNOTE.getTagXmlMain();
    	} else if(DocumentTypeEnum.DEBITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.DEBITNOTE.getTagXmlMain();
    	} else if(DocumentTypeEnum.INVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.INVOICE.getTagXmlMain();
    	} else if(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.SIMPLIFIEDINVOICE.getTagXmlMain();
    	} else {
    		return "";
    	}
    	
    }

	private String getTagXmlMainChilds(){
		return tagXmlMainChilds;
	}

    public static String getTagXmlMainChildsByDocType(String docType) {
    	
    	if(DocumentTypeEnum.CASHINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.CASHINVOICE.getTagXmlMainChilds();
    	} else if(DocumentTypeEnum.RECEIPT.getValue().equals(docType)){
    		return DocumentTypeEnum.RECEIPT.getTagXmlMainChilds();
    	} else if(DocumentTypeEnum.CREDITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.CREDITNOTE.getTagXmlMainChilds();
    	} else if(DocumentTypeEnum.DEBITNOTE.getValue().equals(docType)){
    		return DocumentTypeEnum.DEBITNOTE.getTagXmlMainChilds();
    	} else if(DocumentTypeEnum.INVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.INVOICE.getTagXmlMainChilds();
    	} else if(DocumentTypeEnum.SIMPLIFIEDINVOICE.getValue().equals(docType)){
    		return DocumentTypeEnum.SIMPLIFIEDINVOICE.getTagXmlMainChilds();
    	} else {
    		return "";
    	}
    	
    }
    
	public static int getLabelGui(String type){
		DocumentTypeEnum[] types = values();
		for (int i = 0; i < types.length; i++) {
			if(types[i].value.equals(type)){
				return types[i].labelGui;
			}
		}
		
		return -1;
	}    
    
}
