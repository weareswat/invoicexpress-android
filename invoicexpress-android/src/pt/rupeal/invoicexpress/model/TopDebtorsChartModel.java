package pt.rupeal.invoicexpress.model;

import java.util.List;

public class TopDebtorsChartModel {
	
	private String currency;
	private List<TopClient> clients;
	
	// is a sample the values are generated
	private boolean isSample = true;
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public List<TopClient> getClients() {
		return clients;
	}

	public void setClients(List<TopClient> clients) {
		this.clients = clients;
	}
	
	public boolean isSample() {
		return isSample;
	}
	
	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}	

	/**
	 * Represents the client object in PieGraph Top debtor
	 * <id>1</id>
	 * <name>Chuck Norris</name>
	 * <balance>220321.30</balance>
	 * <fiscal_id>100200300</fiscal_id>
	 */
	public static class TopClient {
		
		private String name;
		private double balance;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public double getBalance() {
			return balance;
		}
		
		public void setBalance(double balance) {
			this.balance = balance;
		}
		
	}
	
	public static boolean isNoChart(TopDebtorsChartModel topDebtor) {
		if(topDebtor.getClients() == null) {
			return true;
		}
		
		return topDebtor.getClients().isEmpty();
	}
	
}
