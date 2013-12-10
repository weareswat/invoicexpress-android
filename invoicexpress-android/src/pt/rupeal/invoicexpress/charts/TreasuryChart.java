package pt.rupeal.invoicexpress.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.chart.XYChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYSeriesRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.model.BarChartModel;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.server.InvoiceXpressParser;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

public class TreasuryChart {

	private static final String SETTLED_GRAPH_ID = "settled_values";
	private static final String ONTIME_GRAPH_ID = "due_values";
	private static final String OVERDUE_GRAPH_ID = "overdue_values";
	private static final String FORECAST_GRAPH_ID = "forecast_values";
	
	public View getView(Context context, Map<String, BarChartModel> treasuryChartData) {
		
		// if there is no data chart then the application has to generate it
		if(BarChartModel.hasNoChart(treasuryChartData)) {
			setGeneratedDataTreasuryChart(treasuryChartData);
		}		
		
		String[] months = treasuryChartData.get(SETTLED_GRAPH_ID).getMonths();
		double[] valuesSettled = treasuryChartData.get(SETTLED_GRAPH_ID).getValues();
		
		CategorySeries seriesSettled = new CategorySeries(context.getResources().getString(R.string.dashboard_legend_settled));
		for (int i = 0; i < valuesSettled.length; i++) {
			seriesSettled.add(valuesSettled[i]);
		}
		
		double[] valuesOnTime = treasuryChartData.get(ONTIME_GRAPH_ID).getValues();
		CategorySeries serieOnTime = new CategorySeries(context.getResources().getString(R.string.dashboard_legend_onTime));
		for (int i = 0; i < valuesOnTime.length; i++) {
			serieOnTime.add(valuesOnTime[i]);
		}
		
		double[] valuesOverdue = treasuryChartData.get(OVERDUE_GRAPH_ID).getValues();
		CategorySeries seriesOverdue = new CategorySeries(context.getResources().getString(R.string.dashboard_legend_overdue));
		for (int i = 0; i < valuesOverdue.length; i++) {
			seriesOverdue.add(valuesOverdue[i]);
		}		
		
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		dataSet.addSeries(0, seriesSettled.toXYSeries());
		dataSet.addSeries(1, serieOnTime.toXYSeries());
		dataSet.addSeries(2, seriesOverdue.toXYSeries());
		
		XYSeriesRenderer rendererReceived = new XYSeriesRenderer();
		rendererReceived.setColor(context.getResources().getColor(R.color.dashboard_green_color));
		
		XYSeriesRenderer rendererWithIn = new XYSeriesRenderer();
		rendererWithIn.setColor(context.getResources().getColor(R.color.dashboard_white_color));
		
		XYSeriesRenderer rendererLiquided = new XYSeriesRenderer();
		rendererLiquided.setColor(context.getResources().getColor(R.color.dashboard_red_color));		
		
		int width = InvoiceXpress.getInstance().getScreenWidth((Activity) context);
		int height = InvoiceXpress.getInstance().getScreenHeight((Activity) context);
		
		BarChartInvoiceXpressRenderer multipleRenderer = new BarChartInvoiceXpressRenderer(context, width, height, valuesSettled, valuesOnTime, valuesOverdue);
		multipleRenderer.addSeriesRenderer(rendererReceived);
		multipleRenderer.addSeriesRenderer(rendererWithIn);
		multipleRenderer.addSeriesRenderer(rendererLiquided);
		
		multipleRenderer.setXAxisMax(months.length);
		
		for (int i = 0; i < months.length; i++) {
			multipleRenderer.addXTextLabel(i+1, months[i]);
		}
		
	    XYChart chart = new BarChartInvoiceXpress(context, dataSet, multipleRenderer, Type.DEFAULT);
	    return new GraphicalView(context, chart);
	}
	
	public static String buildRequestHttpGet() { 
		StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl());
		request.append("/api/charts/treasury.xml");
		request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
		
		if(InvoiceXpress.DEBUG) {
			Log.d(TreasuryChart.class.getCanonicalName(), request.toString());
		}
		
		return request.toString();
	}
	
	public static Map<String, BarChartModel> getChart(Context context, String xml) throws InvoiceXpressException {
		
		Map<String, BarChartModel> graphs = new HashMap<String, BarChartModel>();
		
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);
		Node chartNode = documentDomElement.getElementsByTagName("chart").item(0);
		
		List<Node> monthsXml = parser.getChildNodes((Element) chartNode, "series", 0);
		String[] months = new String[monthsXml.size()];
		for (int i = 0; i < monthsXml.size(); i++) {
			Node node = monthsXml.get(i);
			months[i] = node.getTextContent();
		}
		
		Node graphshNode = documentDomElement.getElementsByTagName("graphs").item(0);
		NodeList graphsXmlNodeList = documentDomElement.getElementsByTagName("graph");
		for (int i = 0; i < graphsXmlNodeList.getLength(); i++) { 
			
			String graphId = ((Element) graphsXmlNodeList.item(i)).getAttribute("gid");
			if(FORECAST_GRAPH_ID.equals(graphId)) {
				continue;
			}
			
			List<Node> graphXml = parser.getChildNodes((Element) graphshNode, "graph", i);
			
			double[] values = new double[graphXml.size()];
			for (int j = 0; j < graphXml.size(); j++) {
				Node node = graphXml.get(j);
				values[j] = Double.parseDouble(node.getTextContent());
			}
			
			BarChartModel graph = new BarChartModel(graphId, months, values);
			graph.setSample(false);
			graphs.put(graphId, graph);
		}

		return graphs;
	}
	
	private static void setGeneratedDataTreasuryChart(Map<String, BarChartModel> graphs) {
		// months
		String[] months = new String[] {"Jan", "Fev", "Mar", "Abr", "Jun", "Jul", "Ago"};

		BarChartModel barChart = new BarChartModel();
		// set graph id
		barChart.setGraphId(SETTLED_GRAPH_ID);		
		// set months
		barChart.setMonths(months);
		// set values
		double[] values = new double[] {100, 500, 1100, 400, 2700, 300, 150};
		barChart.setValues(values);
		// set sample
		barChart.setSample(true);
		graphs.put(SETTLED_GRAPH_ID, barChart);
		
		barChart = new BarChartModel();
		// set graph id
		barChart.setGraphId(ONTIME_GRAPH_ID);			
		// set months
		barChart.setMonths(months);
		// set values		
		values = new double[] {0, 0, 0, 300, 0, 0, 750};
		barChart.setValues(values);
		// set sample
		barChart.setSample(true);
		graphs.put(ONTIME_GRAPH_ID, barChart);
		
		barChart = new BarChartModel();
		// set graph id
		barChart.setGraphId(OVERDUE_GRAPH_ID);			
		// set months
		barChart.setMonths(months);
		// set values		
		values = new double[] {400, 0, 0, 450, 0, 0, 100};
		barChart.setValues(values);
		// set sample
		barChart.setSample(true);
		graphs.put(OVERDUE_GRAPH_ID, barChart);
	}	
	
}
