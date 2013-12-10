package pt.rupeal.invoicexpress.model;

import java.util.Map;

public class BarChartModel {

	private String graphId;
	private String[] months;
	private double[] values;
	
	// is a sample the values are generated
	private boolean isSample = true;

	public BarChartModel() {
		super();
	}
	
	public BarChartModel(String graphId, String[] monsths, double[] values) {
		this.graphId = graphId;
		this.months = monsths;
		this.values = values;
	}

	public String getGraphId() {
		return graphId;
	}

	public void setGraphId(String graphId) {
		this.graphId = graphId;
	}

	public String[] getMonths() {
		return months;
	}

	public void setMonths(String[] months) {
		this.months = months;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}
	
	public boolean isSample() {
		return isSample;
	}
	
	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}
	
	public static boolean hasNoChart(Map<String, BarChartModel> graphs) {
		if(graphs == null) {
			return true;
		}
		
		if(graphs.keySet() == null || graphs.keySet().size() == 0) {
			return true;
		}
		
		for (BarChartModel barChart : graphs.values()) {
			double[] values = barChart.getValues();
			for (int i = 0; i < values.length; i++) {
				if(values[i] != 0) {
					return false;
				} 
			}
		}
		
		return true;
	}
	
}
