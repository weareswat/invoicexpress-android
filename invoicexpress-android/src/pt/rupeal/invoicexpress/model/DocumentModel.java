package pt.rupeal.invoicexpress.model;

import java.io.Serializable;
import java.util.HashMap;

import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;
import pt.rupeal.invoicexpress.utils.DateUtil;

public class DocumentModel implements Serializable {

	private static final long serialVersionUID = -8435344286162456887L;
	
	public static final String DOCUMENT = "document";
	
	public static final String ID = "document_id";
	public static final String SEQUENCE_NUMBER = "sequence_number";
	public static final String DOC_TYPE = "document_type";
	public static final String CLIENT_ID = "doc_client_id";
	
	private String id;
	private String sequenceNumber;
	private String type;
	private String status;
	private String date;
	private String dueDate;
	
	private double sum;
	private double discount;
	private double beforeTaxes;
	private double taxes;	
	private double total;
	
	private String observations;
	
	private String payEntity;
	private String payRef;
	private String payValue;
	
	private String clientId;
	private String clientName;
	private String clientEmail;
	
	private HashMap<String, ItemModel> items;
	
	private boolean isArchived;
	
	public DocumentModel() {
		super();
	}
	
	public DocumentModel(DocumentModel document) {
		this.id = document.getId();
		this.sequenceNumber = document.getSequenceNumber();
		this.type = document.getType();
		this.status = document.getStatus();
		this.date = document.getDate();
		this.dueDate = document.getDueDate();
		
		this.sum = document.getSum();
		this.discount = document.getDiscount();
		this.beforeTaxes = document.getBeforeTaxes();
		this.taxes = document.getTaxes();		
		this.total = document.getTotal();
		
		this.observations = document.getObservations(); 
		
		this.clientId = document.getClientId();
		this.clientName = document.getClientName();
		this.clientEmail = document.getClientEmail();
		
		this.items = document.getItems();
		
		this.isArchived = document.isArchived();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	
//	public String getTotal() {
//		return total;
//	}
//	
//	public void setTotal(String total) {
//		this.total = total;
//	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}	

	public boolean isArchived() {
		return isArchived;
	}

	public void setArchived(boolean isArchived) {
		this.isArchived = isArchived;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public HashMap<String, ItemModel> getItems() {
		return items;
	}

	public void setItems(HashMap<String, ItemModel> items) {
		this.items = items;
	}

//	public String getSum() {
//		return sum;
//	}
//
//	public void setSum(String sum) {
//		this.sum = sum;
//	}
//
//	public String getDiscount() {
//		return discount;
//	}
//
//	public void setDiscount(String discount) {
//		this.discount = discount;
//	}
//
//	public String getBeforeTaxes() {
//		return beforeTaxes;
//	}
//
//	public void setBeforeTaxes(String beforeTaxes) {
//		this.beforeTaxes = beforeTaxes;
//	}
//
//	public String getTaxes() {
//		return taxes;
//	}
//
//	public void setTaxes(String taxes) {
//		this.taxes = taxes;
//	}
	
	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getBeforeTaxes() {
		return beforeTaxes;
	}

	public void setBeforeTaxes(double beforeTaxes) {
		this.beforeTaxes = beforeTaxes;
	}

	public double getTaxes() {
		return taxes;
	}

	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}	

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getPayEntity() {
		return payEntity;
	}

	public void setPayEntity(String payEntity) {
		this.payEntity = payEntity;
	}

	public String getPayRef() {
		return payRef;
	}

	public void setPayRef(String payRef) {
		this.payRef = payRef;
	}

	public String getPayValue() {
		return payValue;
	}

	public void setPayValue(String payValue) {
		this.payValue = payValue;
	}
	
	public boolean isOverDueDate() {
		return !DocumentStatusEnum.isCanceled(this.getStatus()) 
				&& !DocumentStatusEnum.isSettled(this.getStatus())
				&& !DocumentStatusEnum.isDraft(this.getStatus())
				&& this.getDueDate() != null 
				&& DateUtil.isAfter(DateUtil.formatDate(this.getDueDate()));
	}
	
}
