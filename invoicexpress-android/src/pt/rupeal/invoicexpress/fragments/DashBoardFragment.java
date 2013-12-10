package pt.rupeal.invoicexpress.fragments;

import java.util.List;
import java.util.Map;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.adapters.FragmentPagerAdapter;
import pt.rupeal.invoicexpress.model.BarChartModel;
import pt.rupeal.invoicexpress.model.QuarterChartModel;
import pt.rupeal.invoicexpress.model.TopDebtorsChartModel;
import pt.rupeal.invoicexpress.server.ChartRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.charts.InvoicingChart;
import pt.rupeal.invoicexpress.charts.QuarterlyChart;
import pt.rupeal.invoicexpress.charts.TopDebtorsChart;
import pt.rupeal.invoicexpress.charts.TreasuryChart;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DashBoardFragment extends Fragment {

	private ViewPager viewPager;
	private DashBoardFragmentPagerAdapter adpater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((MainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.dashboard, container, false);
		viewPager = (ViewPager) view.findViewById(R.id.dashboard_pager);

		((PagerTitleStrip) viewPager.findViewById(R.id.dashboard_pager_title_strip)).setTextColor(getResources().getColor(R.color.horizontal_navigation_bar_text));
		((PagerTitleStrip) viewPager.findViewById(R.id.dashboard_pager_title_strip)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		((PagerTitleStrip) viewPager.findViewById(R.id.dashboard_pager_title_strip)).setGravity(Gravity.CENTER_VERTICAL);
		((PagerTitleStrip) viewPager.findViewById(R.id.dashboard_pager_title_strip)).setPadding(-55, 0, -55, 0);
		
		if(adpater == null) {
			adpater = new DashBoardFragmentPagerAdapter(getFragmentManager());
		}
		
		new SetAdapterTask().execute();
		
		return view;
	}
	
	/**
	 * Resolve a fragment exception
	 */
	private class SetAdapterTask extends AsyncTask<Void,Void,Void> {

	    @Override
		protected Void doInBackground(Void... params) {
			return null;
		}

	    @Override
	    protected void onPostExecute(Void result) {
	    	viewPager.setAdapter(adpater);
	    }
	    
	}		
	
	private static final int[] dashboard_filters = new int[] {R.string.dashboard_invoicing, R.string.dashboard_treasury, R.string.dashboard_quarterly, 
		R.string.dashboard_top5debtors};	
	
	private class DashBoardFragmentPagerAdapter extends FragmentPagerAdapter {
		
		public DashBoardFragmentPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
		
		@Override
		public Fragment getItem(int position) {
	        Fragment fragment = new DashBoardFilterFragment();
	        
	        Bundle args = new Bundle();
	        args.putInt(DashBoardFilterFragment.FILTER_CODE, position);
	        
	        fragment.setArguments(args);
	        return fragment;
		}

		@Override
		public int getCount() {
			return dashboard_filters.length;
		}
		
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	return getResources().getString(dashboard_filters[position]);
	    }
		
	}
	
	public static class DashBoardFilterFragment extends Fragment {
		
		public static final String FILTER_CODE = "filter";
		
	    public static final int NO_FILTER = -1;
	    public static final int FILTER_CODE_INVOICING = 0;
	    public static final int FILTER_CODE_TREASURY = 1;
	    public static final int FILTER_CODE_QUARTERLY = 2;
	    public static final int FILTER_CODE_TOPDEBTORS = 3;
		
		private int filterCode;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// enable menu
			setHasOptionsMenu(true);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			// get filterCode from parent fragment
			filterCode = getArguments().getInt(FILTER_CODE);
			// get chart layout
			FrameLayout chartLayout = (FrameLayout) inflater.inflate(R.layout.dashboard_charts, container, false);
			
	        if(FILTER_CODE_INVOICING == filterCode) {
	        	// INVOICING
	        	// get chart data
	        	Map<String, BarChartModel> data = InvoiceXpress.getInstance().getCharts().getInvoicingChartData();
	        	// add chart view
	        	chartLayout.addView(new InvoicingChart().getView(getActivity(), data));
	        	// check if is a sample
	        	BarChartModel dataBarChartModel = data.values().iterator().next();
	        	if(dataBarChartModel.isSample()) {
	        		showSampleImage(chartLayout);
	        	} else {
	        		hideSampleImage(chartLayout);
	        	}
	        	// return view
	        	return chartLayout;
	        } else if(FILTER_CODE_TREASURY == filterCode) {
	        	// TREASURY	        	
	        	// get chart data
	        	Map<String, BarChartModel> data = InvoiceXpress.getInstance().getCharts().getTreasuryChartData();
	        	// add chart view
	        	chartLayout.addView(new TreasuryChart().getView(getActivity(), data));
	        	// check if is a sample
	        	BarChartModel dataBarChartModel = data.values().iterator().next();
	        	if(dataBarChartModel.isSample()) {
	        		showSampleImage(chartLayout);
	        	} else {
	        		hideSampleImage(chartLayout);
	        	}
	        	return chartLayout;
	        } else if(FILTER_CODE_QUARTERLY == filterCode) {
	        	// QUARTERY
	        	View quarteryBoardView = inflater.inflate(R.layout.quarterly, container, false);
	        	// get chart data
	        	List<QuarterChartModel> data = InvoiceXpress.getInstance().getCharts().getQuartersChartData();
	        	// add chart view
	        	chartLayout.addView(new QuarterlyChart().getView(getActivity(), quarteryBoardView, data));
	        	// check if is a sample
	        	if(data.get(0).isSample()) {
	        		showSampleImage(chartLayout);
	        	} else {
	        		hideSampleImage(chartLayout);
	        	}
	        	return chartLayout;	      	
	        } else if(FILTER_CODE_TOPDEBTORS == filterCode) {
	        	// TOP DEBTORS
	        	// get chart data
	        	TopDebtorsChartModel data = InvoiceXpress.getInstance().getCharts().getDebtorsChartData();
	        	// check if is a sample
	        	if(data.isSample()) {
	        		// add chart view
	        		TopDebtorsChart chart = new TopDebtorsChart();
	        		chartLayout.addView(chart.getView(getActivity(), data));
	        	} else {
	        		// set image sample invisible
	        		hideSampleImage(chartLayout);
	        		// add chart view
	        		chartLayout.addView(new TopDebtorsChart().getView(getActivity(), data));
	        	}
	        	
	        	return chartLayout;	     	
	        }
	        
	        return null;
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.action_bar_dashboard, menu);
			super.onCreateOptionsMenu(menu, inflater);
		}
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
				case R.id.refresh_tab:
					String[] params = new String[]{String.valueOf(filterCode)};
					ChartRestHandler restHandler = new ChartRestHandler(getActivity());
					InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
					restHandler.execute(params);
					break;
				default:
					break;
			}
	    	
	    	return super.onOptionsItemSelected(item);
	    }
		
		private void showSampleImage(View chartLayout) {
			// set image resource based on locale language device
			int imageResource = InvoiceXpress.isPortugueseLocale() ? R.drawable.amostra : R.drawable.sample;
    		((ImageView) chartLayout.findViewById(R.id.dashboard_sample)).setImageResource(imageResource);
    		// set image visible
    		chartLayout.findViewById(R.id.dashboard_sample).setVisibility(View.VISIBLE);
    		// bring image to front
    		chartLayout.findViewById(R.id.dashboard_sample).bringToFront();
		}
		
		private void hideSampleImage(View chartLayout) {
			chartLayout.findViewById(R.id.dashboard_sample).setVisibility(View.INVISIBLE);
		}
		
	}
	
}
