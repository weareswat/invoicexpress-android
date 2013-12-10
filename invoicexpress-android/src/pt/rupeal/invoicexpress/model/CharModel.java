package pt.rupeal.invoicexpress.model;

import java.util.List;
import java.util.Map;

public class CharModel {

	private Map<String, BarChartModel> invoicingChart;
	private Map<String, BarChartModel> treasuryChart;
	private List<QuarterChartModel> quarterChart;
	private TopDebtorsChartModel topDebtorsChart;
	
	public Map<String, BarChartModel> getInvoicingChart() {
		return invoicingChart;
	}
	
	public void setInvoicingChart(Map<String, BarChartModel> invoicingChart) {
		this.invoicingChart = invoicingChart;
	}
	
	public Map<String, BarChartModel> getTreasuryChart() {
		return treasuryChart;
	}
	
	public void setTreasuryChart(Map<String, BarChartModel> treasuryChart) {
		this.treasuryChart = treasuryChart;
	}
	
	public List<QuarterChartModel> getQuarterChart() {
		return quarterChart;
	}
	
	public void setQuarterChart(List<QuarterChartModel> quarterChart) {
		this.quarterChart = quarterChart;
	}
	
	public TopDebtorsChartModel getTopDebtorsChart() {
		return topDebtorsChart;
	}
	
	public void setTopDebtorsChart(TopDebtorsChartModel topDebtorsChart) {
		this.topDebtorsChart = topDebtorsChart;
	}
	
}
