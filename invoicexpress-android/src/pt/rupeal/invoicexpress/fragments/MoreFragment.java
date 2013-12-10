package pt.rupeal.invoicexpress.fragments;

import java.util.ArrayList;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.activities.AuthenticationActivity;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.layouts.LinkLayout;
import pt.rupeal.invoicexpress.layouts.SubTitleLayout;
import pt.rupeal.invoicexpress.model.AccountModel;
import android.accounts.Account;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreFragment extends Fragment {

	// view is an attribute because the onResume method
	private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		((MainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.more, container, false);
		
		((Button) view.findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		    	
				Account[] accounts = InvoiceXpress.getInstance().getInvoiceXpressAccount();
				
				if(accounts.length > 0){
					// remove accounts data
					InvoiceXpress.getInstance().getAccounts().clear();
					InvoiceXpress.getInstance().getAccounts().addAll(new ArrayList<AccountModel>());
					InvoiceXpress.getInstance().setActiveAccount(null);
					InvoiceXpress.getInstance().setActiveAccountDetails(null);
					// remove android invoiceXpress account
					InvoiceXpress.getInstance().getAccountManager().removeAccount(accounts[0], null, null);
					
					// start authentication activity 
					Intent intent =  new Intent(getActivity(), AuthenticationActivity.class);
					getActivity().startActivityForResult(intent, 2);
					
				}
			}
		});
		
		((SubTitleLayout) view.findViewById(R.id.account)).setTextToTextViewLeft(R.string.more_account);
				
		((LinkLayout) view.findViewById(R.id.active_account)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
				// set account details fragment
				((MainActivity) getActivity()).addFragment(AccountDetailsFragment.class, 
						FragmentTagsEnum.ACCOUNTS_DETAILS);
			}
		});
		
		((LinkLayout) view.findViewById(R.id.change_account)).setLabel(R.string.more_change_account);
		((LinkLayout) view.findViewById(R.id.change_account)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		    	
				((MainActivity) getActivity()).addFragment(AccountsFragment.class, 
						FragmentTagsEnum.ACCOUNTS);
			}
		});	
		
		((SubTitleLayout) view.findViewById(R.id.invoiceXpress)).setTextToTextViewLeft(R.string.more_invoiceXpress);

		((LinkLayout) view.findViewById(R.id.about)).setLabel(R.string.more_about);
		((LinkLayout) view.findViewById(R.id.about)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		    	
				((MainActivity) getActivity()).addFragment(AboutFragment.class, 
						FragmentTagsEnum.ABOUT);
			}
		});	
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// just clear
		menu.clear();
	}	
	
	@Override
	public void onResume() {
		
		if(InvoiceXpress.getInstance().getActiveAccount() != null){
			// get link layout
			LinkLayout activeAccountLinkLayout = ((LinkLayout) view.findViewById(R.id.active_account));
			// resize active account name isn't need, the api return a valid short name
			// set link layout label
			activeAccountLinkLayout.setLabel(InvoiceXpress.getInstance().getActiveAccount().getName());
		}
		
		super.onResume();
	}
	
}
