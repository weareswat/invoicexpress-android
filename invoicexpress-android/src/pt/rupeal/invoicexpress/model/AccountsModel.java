package pt.rupeal.invoicexpress.model;

import java.util.ArrayList;
import java.util.List;

public class AccountsModel {

	private List<AccountModel> accounts;
	private AccountModel accountActive;
	// for currency and performance purpose
	private AccountDetailsModel accountDetailsActive;
	
	public AccountsModel() {
		accounts = new ArrayList<AccountModel>();
		accountActive = new AccountModel();
		accountDetailsActive = new AccountDetailsModel();
	}
	
	public List<AccountModel> getAccounts() {
		return accounts;
	}
	
	public void setAccounts(List<AccountModel> accounts) {
		this.accounts = accounts;
	}
	
	public AccountModel getAccountActive() {
		return accountActive;
	}
	
	public void setAccountActive(AccountModel accountActive) {
		this.accountActive = accountActive;
	}
	
	public AccountDetailsModel getAccountDetailsActive() {
		return accountDetailsActive;
	}
	
	public void setAccountDetailsActive(AccountDetailsModel accountDetailsActive) {
		this.accountDetailsActive = accountDetailsActive;
	}
	
}
