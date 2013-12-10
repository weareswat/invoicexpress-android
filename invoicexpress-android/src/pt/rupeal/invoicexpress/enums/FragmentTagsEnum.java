package pt.rupeal.invoicexpress.enums;

public enum FragmentTagsEnum {

	ABOUT("about"),
	ACCOUNTS("accounts"),
	ACCOUNTS_DETAILS("accounts_details"),
	CONTACTS("contacts"),
	CONTACTS_DETAILS("contact_details"),
	DASHBOARD("dashboard"),
	DIALOG_PROGRESS("dialog_progress"),
	DOCUMENTS("documents"),
	DOCUMENTS_LIST("documents_list"),
	DOCUMENTS_DETAILS("document_details"),
	EMAIL("email"),
	ITEM("item"),
	MORE("more");
	
	private String value;
	
	private FragmentTagsEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
