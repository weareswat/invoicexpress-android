package pt.rupeal.invoicexpress.fragments;

import java.util.List;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.model.AccountModel;
import pt.rupeal.invoicexpress.server.AccountDetailsRestHandler;
import pt.rupeal.invoicexpress.server.AccountsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountsFragment extends Fragment {
	
	private List<AccountModel> accounts;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// get and inflate view
		View view = inflater.inflate(R.layout.accounts, container, false);
		// get account from cache
		accounts = InvoiceXpress.getInstance().getAccounts();

		for (AccountModel account : accounts) {
			
			LinearLayout imageButtonLayout = new LinearLayout(view.getContext());
			LayoutInflater.from(view.getContext()).inflate(R.layout.image_button, imageButtonLayout);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 110)));
			layoutParams.setMargins(
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 12)), 
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 14)), 
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 12)), 
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 6)));
			// set calculated layout parameters
			imageButtonLayout.setLayoutParams(layoutParams);
			
			ImageView image = (ImageView) imageButtonLayout.findViewById(R.id.button_image);
			
			((ViewGroup.MarginLayoutParams) image.getLayoutParams()).leftMargin = Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 4));
			((ViewGroup.MarginLayoutParams) image.getLayoutParams()).rightMargin = Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 4));
			
			if(account.getApiKey().equals(InvoiceXpress.getInstance().getActiveAccount().getApiKey())) {
				image.setImageResource(R.drawable.icon_3_selected);
			} else {
				image.setImageResource(R.drawable.icon_3_unselected);
			}
			
			((TextView) imageButtonLayout.findViewById(R.id.button_label)).setText(account.getName());
			((TextView) imageButtonLayout.findViewById(R.id.button_label)).setTextSize(18);
			
			if(account.getApiKey().equals(InvoiceXpress.getInstance().getActiveAccount().getApiKey())) {
				((TextView) imageButtonLayout.findViewById(R.id.button_label)).setTextColor(getResources().getColor(R.color.green_normal));
			}
			
			LayoutParams textViewParams = ((TextView) imageButtonLayout.findViewById(R.id.button_label)).getLayoutParams();
			layoutParams = new LinearLayout.LayoutParams(textViewParams);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			layoutParams.setMargins(
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 14)), 0, 
					Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 14)), 0);
			((TextView) imageButtonLayout.findViewById(R.id.button_label)).setLayoutParams(layoutParams);
			
			// just set listener for inactive accounts
			if(!account.getApiKey().equals(InvoiceXpress.getInstance().getActiveAccount().getApiKey())) {
				imageButtonLayout.setOnClickListener(new SwitchAccountListener(account));
			}
			
			((LinearLayout)view.findViewById(R.id.accounts)).addView(imageButtonLayout);
		}
		// return view
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// check if there is any icon inflated
		if(menu.size() == 0) {
			inflater.inflate(R.menu.action_bar_accounts, menu);
		}
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
    	switch (item.getItemId()) {
			case R.id.refresh_tab:
				new AccountsRestHandler(getActivity()).execute();
			default:
				break;
				
		}
    	
    	return false;
    }
	
	private class SwitchAccountListener implements View.OnClickListener {

		private AccountModel account;
		
		public SwitchAccountListener(AccountModel account) {
			this.account = account;
		}
		
		@Override
		public void onClick(View v) {
			// check if the progress bar is visible to disable clicks
			if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
				return;
			}
	    	// vibrate - time in milliseconds
	    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
			// get account details from server
	    	AccountDetailsRestHandler restHandler = new AccountDetailsRestHandler(getActivity(), account);
	    	InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
			restHandler.execute();
		}	
	}
}
