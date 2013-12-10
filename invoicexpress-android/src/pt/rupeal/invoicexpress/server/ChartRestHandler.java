package pt.rupeal.invoicexpress.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.charts.InvoicingChart;
import pt.rupeal.invoicexpress.charts.QuarterlyChart;
import pt.rupeal.invoicexpress.charts.TopDebtorsChart;
import pt.rupeal.invoicexpress.charts.TreasuryChart;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.DashBoardFragment;
import pt.rupeal.invoicexpress.fragments.DashBoardFragment.DashBoardFilterFragment;
import pt.rupeal.invoicexpress.model.ChartModel;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.utils.InvoiceXpressException;

import android.content.Context;
import android.util.Log;

public class ChartRestHandler extends AsyncTask<String, Void, ChartModel> {

	private boolean isRefreshing;
	private int requestedFilterCode;
	
	public ChartRestHandler(Context context) {
		this.context = context;
	}
	
	@Override
	protected void onPostExecute(ChartModel result) {
		super.onPostExecute(result);

		// check if there is an error
    	if(existsError()) {
    		processError(result);
    		return;
    	}
    	// success
    	// is a refresh request
    	if(isRefreshing) {
    		// set data just for specified chart
    		InvoiceXpress.getInstance().setCharts(result, requestedFilterCode);
    		// refresh
    		((MainActivity) context).refreshFragment(requestedFilterCode);
    	} else {
    		// not a refresh request
    		// set data
    		InvoiceXpress.getInstance().setCharts(result);
    		// set chart requested to true
    		InvoiceXpress.getInstance().setChartsRequested(true);
    		// call fragment
    		((MainActivity) context).addFragment(DashBoardFragment.class, 
    				FragmentTagsEnum.DASHBOARD);
    	}
	}
	
	private void processError(ChartModel result) {
		// show error message		
		super.processError();
		// if there is a normal request show sample interface
		if(!isRefreshing) {
    		// set data
    		InvoiceXpress.getInstance().setCharts(result);
    		// set chart requested to false
    		InvoiceXpress.getInstance().setChartsRequested(false);
    		// call fragment
    		((MainActivity) context).addFragment(DashBoardFragment.class, 
    				FragmentTagsEnum.DASHBOARD);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 * 
	 * params[0] = requestedFilterCode
	 */
	@Override
	protected ChartModel doInBackground(String... params) {
		
		ChartModel charts = new ChartModel();
		HttpGet httpGet;
		String response;
		
		try {
			requestedFilterCode = Integer.valueOf(params[0]);
			switch (requestedFilterCode) {
				case DashBoardFilterFragment.NO_FILTER:
					isRefreshing= false;
					// invoicing
					httpGet = new HttpGet(InvoicingChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setInvoicingChartData(InvoicingChart.getChart(context, response));
					// treasury
					httpGet = new HttpGet(TreasuryChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setTreasuryChartData(TreasuryChart.getChart(context, response));
					// quarterly
					httpGet = new HttpGet(QuarterlyChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setQuartersChartData(QuarterlyChart.getChart(context, response));
					// top debtors
					httpGet = new HttpGet(TopDebtorsChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setDebtorsChartData(TopDebtorsChart.getChart(context, response));
					break;		
				case DashBoardFilterFragment.FILTER_CODE_INVOICING:
					isRefreshing= true;
					httpGet = new HttpGet(InvoicingChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setInvoicingChartData(InvoicingChart.getChart(context, response));
					break;
				case DashBoardFilterFragment.FILTER_CODE_TREASURY:
					isRefreshing= true;
					httpGet = new HttpGet(TreasuryChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setTreasuryChartData(TreasuryChart.getChart(context, response));
					break;
				case DashBoardFilterFragment.FILTER_CODE_QUARTERLY:
					isRefreshing= true;
					httpGet = new HttpGet(QuarterlyChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setQuartersChartData(QuarterlyChart.getChart(context, response));
					break;
				case DashBoardFilterFragment.FILTER_CODE_TOPDEBTORS:
					isRefreshing= true;
					httpGet = new HttpGet(TopDebtorsChart.buildRequestHttpGet());
					response = doInBackgroundByDashBoardType(httpGet);
					charts.setDebtorsChartData(TopDebtorsChart.getChart(context, response));
					break;
				default:
					break;
			}
			
		} catch(InvoiceXpressException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage(), e);
			setError(e.getMessage(), e.getType());
		}

		return charts;
	}
	
	private String doInBackgroundByDashBoardType(HttpGet httpGet) throws InvoiceXpressException {
		
		StringBuffer responseString = new StringBuffer();
		
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient(InvoiceXpress.getHttpParameters());
			HttpResponse response = httpClient.execute(httpGet, new BasicHttpContext());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	
			String line;
			while ((line = reader.readLine()) != null) {
			    responseString.append(line);
			}
			
			if(InvoiceXpress.DEBUG) {
				Log.d(this.getClass().getCanonicalName(), responseString.toString());
			}
		
		} catch (ClientProtocolException e) {
			throw new InvoiceXpressException(context, R.string.error_get_dashboard_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (IOException e) {
			throw new InvoiceXpressException(context, R.string.error_get_dashboard_unexpected, InvoiceXpressErrorType.ERROR);
		} catch (Exception e) {
			throw new InvoiceXpressException(context, R.string.error_get_dashboard_unexpected, InvoiceXpressErrorType.FATAL);
		}
		
		return responseString.toString();
	}
	
}
