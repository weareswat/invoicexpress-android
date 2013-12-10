package pt.rupeal.invoicexpress.charts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.XYChart;
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

public class InvoicingChart {

	private static final String INVOICING_GRAPH_ID = "invoicing";
	
	public View getView(Context context, Map<String, BarChartModel> invoicingChartData) {

		// if there is no data chart then the application has to generate it
		if(BarChartModel.hasNoChart(invoicingChartData)) {
			 setGeneratedDataInvoicingChart(invoicingChartData);
		}
		
		String[] months = invoicingChartData.get(INVOICING_GRAPH_ID).getMonths();
		double[] totalValues = invoicingChartData.get(INVOICING_GRAPH_ID).getValues();
		
		CategorySeries series = new CategorySeries(context.getResources().getString(R.string.dashboard_legend_total));
		for (int i = 0; i < totalValues.length; i++) {
			series.add(totalValues[i]);
		}
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(context.getResources().getColor(R.color.dashboard_green_color));
		
		int width = InvoiceXpress.getInstance().getScreenWidth((Activity) context);
		int height = InvoiceXpress.getInstance().getScreenHeight((Activity) context);
		
		BarChartInvoiceXpressRenderer multipleRenderer = new BarChartInvoiceXpressRenderer(context, width, height, totalValues);
		multipleRenderer.addSeriesRenderer(renderer);
		
		for (int i = 0; i < months.length; i++) {
			multipleRenderer.addXTextLabel(i+1, months[i]);
		}
		
		multipleRenderer.setXAxisMax(months.length);
		
		XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
		dataSet.addSeries(series.toXYSeries());
		
		XYChart chart = new BarChartInvoiceXpress(context, dataSet, multipleRenderer, Type.DEFAULT);
		return new GraphicalView(context, chart);
		
	}
	
	public static String buildRequestHttpGet() { 
		StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl());
		request.append("/api/charts/invoicing.xml");
		request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
		
		if(InvoiceXpress.DEBUG) {
			Log.d(InvoicingChart.class.getCanonicalName(), request.toString());
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
	
	private static void setGeneratedDataInvoicingChart(Map<String, BarChartModel> graphs) {
		BarChartModel barChart = new BarChartModel();
		// set graph id
		barChart.setGraphId(INVOICING_GRAPH_ID);
		// set months
		String[] months = new String[] {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};
		barChart.setMonths(months);
		// set values
		double[] values = new double[] {1000, 600, 1100, 300, 700, 350};
		barChart.setValues(values);
		// set sample
		barChart.setSample(true);
		// put and return graph	
		graphs.put(INVOICING_GRAPH_ID, barChart);
	}	
	
}
