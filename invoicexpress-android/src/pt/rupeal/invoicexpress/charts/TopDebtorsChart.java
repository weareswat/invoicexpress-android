package pt.rupeal.invoicexpress.charts;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.model.TopDebtorsChartModel;
import pt.rupeal.invoicexpress.model.TopDebtorsChartModel.TopClient;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.server.InvoiceXpressParser;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class TopDebtorsChart {

	public View getView(Context context, TopDebtorsChartModel debtorsChartData) {
		
		// if there is no data chart then the application has to generate it
		if(TopDebtorsChartModel.isNoChart(debtorsChartData)) {
			 setGeneratedDataTopDebtorsChart(debtorsChartData);
		}
		
		if(debtorsChartData.isSample()) {
			return getViewSample(context);
		}
		
		List<TopClient> clients = debtorsChartData.getClients();
		double[] balance = new double[clients.size()];
		String[] names = new String[clients.size()];
		for (int i = 0; i < clients.size(); i++) {
			TopDebtorsChartModel.TopClient client = clients.get(i);
			balance[i] = client.getBalance();
			names[i] = client.getName();
		}
		
		CategorySeries series = new CategorySeries("");
		for (double value : balance) {
			series.add(value);
		}
		
		List<String> contactsList = new ArrayList<String>();
		for (String name : names) {
			contactsList.add(name);
		}
		
		int[] colors = new int[] {context.getResources().getColor(R.color.dashboard_debtor_1),
				context.getResources().getColor(R.color.dashboard_debtor_2), 
				context.getResources().getColor(R.color.dashboard_debtor_3),
				context.getResources().getColor(R.color.dashboard_debtor_4),
				context.getResources().getColor(R.color.dashboard_debtor_5)};
		
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setChartTitle(context.getResources().getString(R.string.dashboard_title) + " "
				+ InvoiceXpress.getInstance().getActiveAccountDetails().getCurrencySymbol());
		renderer.setBackgroundColor(context.getResources().getColor(R.color.background));
		
		renderer.setLabelsColor(context.getResources().getColor(R.color.dashboard_labels));
		
		renderer.setMargins(new int[]{0, 0, 0, 0});
		
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		
		for (int color : colors) {
			SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
			seriesRenderer.setColor(color);
			renderer.addSeriesRenderer(seriesRenderer);
		}
		
		PieChartInvoiceXpress chart = new PieChartInvoiceXpress(context, series, contactsList, renderer);
	    return new GraphicalView(context, chart);
		    
	}
	
	public static final String NO_CLIENT_SAMPLE = "no_client_sample";
	
	public View getViewSample(Context context) {
		
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setChartTitle("");
		renderer.setBackgroundColor(context.getResources().getColor(R.color.background));
		
		renderer.setLabelsColor(context.getResources().getColor(R.color.dashboard_labels));
		
		renderer.setMargins(new int[]{0, 0, 0, 0});
		
		renderer.setPanEnabled(false);
		renderer.setZoomEnabled(false);
		
		SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
		seriesRenderer.setColor(context.getResources().getColor(R.color.dashboard_no_debtors));
		renderer.addSeriesRenderer(seriesRenderer);
		
		CategorySeries series = new CategorySeries("");
		series.add(1);
		
		List<String> contactsList = new ArrayList<String>();
		contactsList.add(NO_CLIENT_SAMPLE);
		
		PieChartInvoiceXpress chart = new PieChartInvoiceXpress(context, series, contactsList, renderer);
	    return new GraphicalView(context, chart);
	}
	
	/**
	 * Example: https://screen-name.invoicexpress.net/api/charts/top-debtors.xml
	 * 
	 * @param params
	 * @return
	 */
	public static String buildRequestHttpGet() { 
		StringBuffer request = new StringBuffer(InvoiceXpress.getInstance().getActiveAccount().getUrl());
		request.append("/api/charts/top-debtors.xml");
		request.append("?api_key=" + InvoiceXpress.getInstance().getActiveAccount().getApiKey());
		
		if(InvoiceXpress.DEBUG) {
			Log.d(TopDebtorsChart.class.getCanonicalName(), request.toString());
		}
		
		return request.toString();
	}
	
	public static TopDebtorsChartModel getChart(Context context, String xml) throws InvoiceXpressException {
		TopDebtorsChartModel model = new TopDebtorsChartModel();
		
		InvoiceXpressParser parser = new InvoiceXpressParser(context);
		Document documentDomElement = parser.getDomElement(xml);
		NodeList nodeList = documentDomElement.getElementsByTagName("top-debtors");
		
		Element elem = (Element) nodeList.item(0);
		model.setCurrency(parser.getValue(elem, "currency"));
		
		nodeList = documentDomElement.getElementsByTagName("client");
		List<TopDebtorsChartModel.TopClient> clients = new ArrayList<TopDebtorsChartModel.TopClient>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			
			elem = (Element) nodeList.item(i);
			
			TopDebtorsChartModel.TopClient topClient = new TopDebtorsChartModel.TopClient();
			topClient.setName(parser.getValue(elem, "name"));
			topClient.setBalance(Double.parseDouble(parser.getValue(elem, "balance")));
			
			clients.add(topClient);
		}
		
		model.setClients(clients);
		model.setSample(false);
		
		return model;
	}
	
	public static void setGeneratedDataTopDebtorsChart(TopDebtorsChartModel model) {
		List<TopClient> clients = new ArrayList<TopClient>();
		model.setClients(clients);
		model.setSample(true);
	}	
	
}
