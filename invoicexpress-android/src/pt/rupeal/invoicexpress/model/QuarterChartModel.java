package pt.rupeal.invoicexpress.model;

import java.util.List;

import pt.rupeal.invoicexpress.utils.StringUtil;

public class QuarterChartModel {
	
	private String invoicing;
	private String taxes;
	private String ytd;
	
	// is a sample the values are generated
	private boolean isSample = true;
	
	public String getInvoicing() {
		return invoicing;
	}
	
	public void setInvoicing(String invoicing) {
		this.invoicing = invoicing;
	}
	
	public String getTaxes() {
		return taxes;
	}
	
	public void setTaxes(String taxes) {
		this.taxes = taxes;
	}
	
	public String getYtd() {
		return ytd;
	}
	
	public void setYtd(String ytd) {
		this.ytd = ytd;
	}
	
	public boolean isSample() {
		return isSample;
	}
	
	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}	
	
	public static boolean isNoChart(List<QuarterChartModel> quarters) {
		for (QuarterChartModel quarter : quarters) {
			if(!StringUtil.ZERO.equals(quarter.getInvoicing()) 
					|| !StringUtil.ZERO.equals(quarter.getTaxes())
					|| !StringUtil.NOT_APPLICABLE.equals(quarter.getYtd())) {
				
				return false;
			}
		}
		
		return true;
	}	
	
}
