package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.layouts.SubTitleLayout;
import pt.rupeal.invoicexpress.layouts.ValueLabelImageLayout;
import pt.rupeal.invoicexpress.listeners.EmailListener;
import pt.rupeal.invoicexpress.listeners.PhoneCallListener;
import pt.rupeal.invoicexpress.model.AccountDetailsModel;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountDetailsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate view
		View view = inflater.inflate(R.layout.account_details, container, false);
		
		// get account details from cache
		AccountDetailsModel account = InvoiceXpress.getInstance().getActiveAccountDetails();
		// the resized to the name account is not needed
		// set active account name
		((TextView) view.findViewById(R.id.account_details_title_name)).setText(account.getName());
		
		((TextView) view.findViewById(R.id.account_details_title_entity)).setText(account.getEntity());

		((SubTitleLayout) view.findViewById(R.id.account_details_type_subtitle)).setTextToTextViewLeft(R.string.details_type_subTitle);

		((TextView) view.findViewById(R.id.account_details_type)).setText(account.getPlan());
		
		((SubTitleLayout) view.findViewById(R.id.account_details_general_subtitle)).setTextToTextViewLeft(R.string.details_general_subTitle);
		
		if(account.hasAddress()) {
			((LinearLayout) view.findViewById(R.id.account_details_address_layout)).setVisibility(LinearLayout.VISIBLE);
			((View) view.findViewById(R.id.account_details_address_line_seperator)).setVisibility(View.VISIBLE);
			
			((TextView) view.findViewById(R.id.account_details_address)).setText(account.getAddress() + '\n' + account.getPostalCode() + ' ' + account.getCity() );
		} else {
			((LinearLayout) view.findViewById(R.id.account_details_address_layout)).setVisibility(LinearLayout.GONE);
			((View) view.findViewById(R.id.account_details_address_line_seperator)).setVisibility(View.GONE);
		}
		
		if(account.hasPhone()) {
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.account_details_phone_line_seperator)).setVisibility(View.VISIBLE);
			
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setText(getActivity(), account.getPhone());
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setLabel(R.string.details_phone);
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setImage(R.drawable.ic_device_access_call);
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setOnClickListener(
					new PhoneCallListener(getActivity(), account.getPhone()));			
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_phone)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.account_details_phone_line_seperator)).setVisibility(View.GONE);
		}
		
		if(account.hasFax()) {
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_fax)).setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.account_details_fax_line_seperator)).setVisibility(View.VISIBLE);
			
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_fax)).setText(getActivity(), account.getFax());
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_fax)).setLabel(R.string.details_fax);
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_fax)).setImage(R.drawable.ic_device_access_call);
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_fax)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.account_details_fax_line_seperator)).setVisibility(View.GONE);
		}
		
		if(account.hasEmail()) {
			// get account email view
			ValueLabelImageLayout emailLayout = ((ValueLabelImageLayout) view.findViewById(R.id.account_details_email));
			// set account email view to visible
			emailLayout.setVisibility(View.VISIBLE);
			// set account email line to visible			
			((View) view.findViewById(R.id.account_details_email_line_seperator)).setVisibility(View.VISIBLE);
			// set values
			emailLayout.setText(getActivity(), account.getEmail());
			emailLayout.setLabel(R.string.details_email);
			emailLayout.setImage(R.drawable.ic_content_email);
			emailLayout.setOnClickListener(new EmailListener(getActivity(), account.getEmail()));
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.account_details_email)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.account_details_email_line_seperator)).setVisibility(View.GONE);
		}
		
		if(account.hasCurrency()) {
			((SubTitleLayout) view.findViewById(R.id.account_details_currency_subtitle)).setTextToTextViewLeft(R.string.details_currency_subTitle);
			if(account.hasCurrencySymbol()){
				((TextView) view.findViewById(R.id.account_details_currency)).setText(account.getCurrencyName() + " (" + account.getCurrencySymbol() + ")");
			} else {
				((TextView) view.findViewById(R.id.account_details_currency)).setText(account.getCurrencyName());
			}
		}
		
		return view;
	}
	
}
