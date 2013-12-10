package pt.rupeal.invoicexpress.model;

public class AccountDetailsModel {

	private String plan;
	private String organization;
	private String entity;
	private String name;
	private String phone;
	private String fax;
	private String email;
	private String address;
	private String city;
	private String postalCode;
	private String locale;
	private String country;
	
	private String currencyName;
	private String currencySymbol;
	
	private boolean isBlocked;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public boolean hasAddress(){
		return getAddress() != null && !getAddress().isEmpty();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean hasPhone(){
		return getPhone() != null && !getPhone().isEmpty();
	}
	
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public boolean hasFax(){
		return getFax() != null && !getFax().isEmpty();
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean hasEmail(){
		return getEmail() != null && !getEmail().isEmpty();
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	private static final String SPAIN = "es";
	
	public boolean isLocaleSpain() {
		return SPAIN.equals(locale);
	}
	
	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currency_name) {
		this.currencyName = currency_name;
	}
	
	public boolean hasCurrency(){
		return getCurrencyName() != null && !getCurrencyName().isEmpty();
	}
	
	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currency_sym) {
		this.currencySymbol = currency_sym;
	}
	
	public boolean hasCurrencySymbol(){
		return getCurrencySymbol() != null && !getCurrencySymbol().isEmpty();
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	
	private static final String PORTUGAL = "Portugal";

	public boolean isFromPortugal() {
		return country != null && country.equals(PORTUGAL);
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
}
