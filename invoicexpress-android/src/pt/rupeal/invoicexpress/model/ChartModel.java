package pt.rupeal.invoicexpress.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartModel {

	private Map<String, BarChartModel> invoicingChartData;
	private Map<String, BarChartModel> treasuryChartData;
	private List<QuarterChartModel> quartersChartData;
	private TopDebtorsChartModel debtorsChartData;
	
	public ChartModel() {
		invoicingChartData = new HashMap<String, BarChartModel>();
		treasuryChartData = new HashMap<String, BarChartModel>();
		quartersChartData = new ArrayList<QuarterChartModel>();
		debtorsChartData = new TopDebtorsChartModel();
	}
	
	public Map<String, BarChartModel> getInvoicingChartData() {
		return invoicingChartData;
	}
	
	public void setInvoicingChartData(Map<String, BarChartModel> invoicingChartData) {
		this.invoicingChartData = invoicingChartData;
	}
	
	public Map<String, BarChartModel> getTreasuryChartData() {
		return treasuryChartData;
	}
	
	public void setTreasuryChartData(Map<String, BarChartModel> treasuryChartData) {
		this.treasuryChartData = treasuryChartData;
	}
	
	public List<QuarterChartModel> getQuartersChartData() {
		return quartersChartData;
	}
	
	public void setQuartersChartData(List<QuarterChartModel> quartersChartData) {
		this.quartersChartData = quartersChartData;
	}
	
	public TopDebtorsChartModel getDebtorsChartData() {
		return debtorsChartData;
	}
	
	public void setDebtorsChartData(TopDebtorsChartModel debtorsChartData) {
		this.debtorsChartData = debtorsChartData;
	}
	
	public boolean existsChartData() {
		return invoicingChartData != null && !invoicingChartData.isEmpty() 
				|| treasuryChartData != null && !treasuryChartData.isEmpty()
				|| quartersChartData != null && !quartersChartData.isEmpty()
				|| debtorsChartData != null;
	}
	
}
