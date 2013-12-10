package pt.rupeal.invoicexpress.model;

import java.io.Serializable;

import pt.rupeal.invoicexpress.fragments.DocumentsListFragment.DocumentFilterFragment;

public class ContactModel implements Serializable {
	
	private static final long serialVersionUID = 471756329182813839L;
	
	public static final String ID = "contactId";
	public static final String CONTACT = "contact";
	
	private String id;
	private String name;
	private String code;
	private String email;
	private String address;
	private String postalCode;
	private String country;
	private String fiscalId;
	private String website;
	private String phone;
	private String fax;
	private String preferredName;
	private String preferredEmail;
	private String preferredPhone;
	private String observations;
	private String sendOptions;
	
	private boolean isFirst;
	
	private DocumentsFilterModel documents;

	public ContactModel() {
		documents = new DocumentsFilterModel();
	}
	
	// TODO remove
	public ContactModel(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public ContactModel(ContactModel contact) {
		this.id = contact.getId();
		this.name = contact.getName();
		this.code = contact.getCode();
		this.email = contact.getEmail();
		this.address = contact.getAddress();
		this.postalCode = contact.getPostalCode();
		this.country = contact.getCountry();
		this.fiscalId = contact.getFiscalId();
		this.website = contact.getWebsite();
		this.phone = contact.getPhone();
		this.fax = contact.getFax();
		this.preferredName = contact.getPreferredName();
		this.preferredEmail = contact.getPreferredEmail();
		this.preferredPhone = contact.getPreferredPhone();
		this.observations = contact.getObservations();
		this.sendOptions = contact.getSendOptions();
		
		this.isFirst = contact.isFirst();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isFirst() {
		return isFirst;
	}
	
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}	

	public String getFiscalId() {
		return fiscalId;
	}

	public void setFiscalId(String fiscalId) {
		this.fiscalId = fiscalId;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getSendOptions() {
		return sendOptions;
	}

	public void setSendOptions(String sendOptions) {
		this.sendOptions = sendOptions;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getPreferredEmail() {
		return preferredEmail;
	}

	public void setPreferredEmail(String preferredEmail) {
		this.preferredEmail = preferredEmail;
	}

	public String getPreferredPhone() {
		return preferredPhone;
	}
	
	public boolean hasName() {
		return getName() != null && !getName().isEmpty();
	}	

	public void setPreferredPhone(String preferredPhone) {
		this.preferredPhone = preferredPhone;
	}
	
	public boolean hasPreferredName() {
		return getPreferredName() != null && !getPreferredName().isEmpty();
	}	
	
	public boolean hasPreferredPhone() {
		return getPreferredPhone() != null && !getPreferredPhone().isEmpty();
	}	
	
	public boolean hasPreferredEmail() {
		return getPreferredEmail() != null && !getPreferredEmail().isEmpty();
	}	
	
	public boolean hasAddress() {
		return getAddress() != null && !getAddress().isEmpty();
	}	
	
	public boolean hasPhone() {
		return getPhone() != null && !getPhone().isEmpty();
	}	
	
	public boolean hasFax() {
		return getFax() != null && !getFax().isEmpty();
	}		
	
	public boolean hasPostalCode() {
		return getPostalCode() != null && !getPostalCode().isEmpty();
	}
	
	public boolean hasFiscalId() {
		return getFiscalId() != null && !getFiscalId().isEmpty();
	}
	
	public boolean hasEmail() {
		return getEmail() != null && !getEmail().isEmpty();
	}	
	
	public boolean hasCountry() {
		return getCountry() != null && !getCountry().isEmpty();
	}

	public boolean hasContactInfo() {
		return hasAddress() || hasCountry() || hasPostalCode() || hasEmail() ||hasFax() || hasPhone() ;
	}

	public boolean hasContactPreferredInfo() {
		return hasPreferredName() || hasPreferredEmail() || hasPreferredPhone();
	}

	public DocumentsFilterModel getDocuments() {
		return documents;
	}

	public void setDocuments(DocumentsFilterModel documents) {
		this.documents = documents;
	}

	public boolean existsDocuments() {
		int totalDownloadedDocuments = 0;
		
		if(documents.getDocuments().size() > 0) {
			// archived
			DocumentsModel documentsModel = documents.getDocuments().get(DocumentFilterFragment.FILTER_CODE_ARCHIVED);
			if(documentsModel != null) {
				totalDownloadedDocuments = documentsModel.getDownloadedDocuments();
			}
			// all
			documentsModel = documents.getDocuments().get(DocumentFilterFragment.FILTER_CODE_ALL);
			if(documentsModel != null) {
				totalDownloadedDocuments += documentsModel.getDownloadedDocuments();
			}
			// over due
			documentsModel = documents.getDocuments().get(DocumentFilterFragment.FILTER_CODE_OVER_DUE);
			if(documentsModel != null) {
				totalDownloadedDocuments += documentsModel.getDownloadedDocuments();
			}			
		}
		
		return totalDownloadedDocuments > 0;
		
	}
	
}
