package pt.rupeal.invoicexpress.enums;

import java.util.List;

public enum RoleEnum {

	ADMINISTRATOR ("administrator"),
	CONTRIBUTOR ("contributor"),
	CONTRIBUTOR_GUIDES ("contributor_guides"),
	SITE_ADMIN ("site_admin"),
	SALESPERSON ("salesperson");
	
	private String value;
	
	private RoleEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static RoleEnum getRoleEnum(String value) {
		RoleEnum[] values = values();
		for (int i = 0; i < values.length; i++) {
			if(values[i].value.equals(value)) {
				return values[i]; 
			}
		}
		
		return null;
	}
	
	/**
	 * Is allow to consult invoices based on user roles.
	 * 
	 * @param roles
	 * @return
	 */
	public static boolean isAllowToConsultInvoices(List<RoleEnum> roles) {
		return !roles.contains(CONTRIBUTOR_GUIDES);
	}
	
}
