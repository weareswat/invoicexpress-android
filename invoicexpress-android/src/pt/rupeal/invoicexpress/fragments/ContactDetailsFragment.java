package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.enums.RoleEnum;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment.DocumentFilterFragment;
import pt.rupeal.invoicexpress.layouts.SubTitleLayout;
import pt.rupeal.invoicexpress.layouts.ValueLabelImageLayout;
import pt.rupeal.invoicexpress.listeners.EmailListener;
import pt.rupeal.invoicexpress.listeners.PhoneCallListener;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.server.ContactDetailsRestHandler;
import pt.rupeal.invoicexpress.server.DocumentsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.widgets.TextViewInvoiceXpress;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetailsFragment extends Fragment {

	private View view;
	private ContactModel contact;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable option menu
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate view
		view = inflater.inflate(R.layout.contact_details, container, false);
		// attach info to view
		attachView();
		return view;
	}
	
	private void attachView() {
		// get contact
		contact = (ContactModel) getArguments().getSerializable(ContactModel.CONTACT);
		// contact title name
		TextViewInvoiceXpress titleNameTextView = ((TextViewInvoiceXpress) view.findViewById(R.id.contact_details_title_name));
		// calculate margin values
		int margin = titleNameTextView.getPaddingLeft();
		margin += titleNameTextView.getPaddingRight();
		margin += ((ImageView) view.findViewById(R.id.contact_details_title_name_image)).getPaddingRight();
		margin += ScreenLayoutUtil.convertDpToPixels(getActivity(), 80);
		// set text
		titleNameTextView.setText(getActivity(), contact.getName(), margin, TextViewInvoiceXpress.BREAK);

		Button getInvoicesButton = ((Button) view.findViewById(R.id.contact_details_button));
		getInvoicesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		    	
				// debug log
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), "Get invoices clicked for contact: " + contact.getName());
				}
				
				// check if the progress bar is visible to disable clicks
				if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
					return;
				}
				
				// verify if the user can consult invoices according his roles
				if(!RoleEnum.isAllowToConsultInvoices(InvoiceXpress.getInstance().getActiveAccount().getRoles())) {
					Toast.makeText(getActivity(), R.string.error_documents_roles, Toast.LENGTH_LONG).show();
					return;
				}
				
				// call server if there are no documents in cache
				if(InvoiceXpress.getInstance().getContacts().containsKey(contact.getId()) 
						&& InvoiceXpress.getInstance().getContacts().get(contact.getId()).existsDocuments()) {
		    		// set extra contact id. this will be read in DocumentsListFragment
		    		Bundle args = new Bundle();
		    		args.putString(DocumentModel.DOC_TYPE, "");
		    		args.putString(ContactModel.ID, contact.getId());
		    		// call fragment
		    		((MainActivity) getActivity()).addFragment(DocumentsListFragment.class, 
		    				FragmentTagsEnum.DOCUMENTS_LIST,
		    				args); 
				} else {
					// get data from server
					// set parameters
					String[] params = new String[]{"", contact.getId(), 
							String.valueOf(DocumentFilterFragment.NO_FILTER), ""};
					// execute web service
					DocumentsRestHandler resthandler = new DocumentsRestHandler(getActivity());
					InvoiceXpress.getInstance().setAsyncTaskActive(resthandler);
					resthandler.execute(params);
				}				
			}
		});
		
		
		if(contact.hasContactPreferredInfo()) {
			((SubTitleLayout) view.findViewById(R.id.contact_details_preferred_subtitle)).setVisibility(View.VISIBLE);
			
			((SubTitleLayout) view.findViewById(R.id.contact_details_preferred_subtitle)).setTextToTextViewLeft(R.string.contact_details_preferred_title);
		} else {
			((SubTitleLayout) view.findViewById(R.id.contact_details_preferred_subtitle)).setVisibility(View.GONE);
		}
		
		if(contact.hasPreferredName()) {
			((LinearLayout) view.findViewById(R.id.contact_details_preferred_name_layout)).setVisibility(LinearLayout.VISIBLE);
			((View) view.findViewById(R.id.contact_details_preferred_name_layout_line)).setVisibility(View.VISIBLE);
			
			TextViewInvoiceXpress preferredNameTextView = ((TextViewInvoiceXpress) view.findViewById(R.id.contact_details_preferred_name));
			margin = ((ViewGroup.MarginLayoutParams) preferredNameTextView.getLayoutParams()).leftMargin;
			margin += ((ViewGroup.MarginLayoutParams) preferredNameTextView.getLayoutParams()).rightMargin;
			preferredNameTextView.setText(getActivity(), contact.getPreferredName(), margin, TextViewInvoiceXpress.RESIZE);
		} else {
			((LinearLayout) view.findViewById(R.id.contact_details_preferred_name_layout)).setVisibility(LinearLayout.GONE);
			((View) view.findViewById(R.id.contact_details_preferred_name_layout_line)).setVisibility(View.GONE);
		}

		if(contact.hasPreferredPhone()) {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.contact_details_preferred_phone_line)).setVisibility(View.VISIBLE);
			
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setText(getActivity(), contact.getPreferredPhone());
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setLabel(R.string.details_phone);
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setImage(R.drawable.ic_device_access_call);
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setOnClickListener(
					new PhoneCallListener(getActivity(), contact.getPreferredPhone()));
			
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_phone)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.contact_details_preferred_phone_line)).setVisibility(View.GONE);
		}
		
		if(contact.hasPreferredEmail()) {
			ValueLabelImageLayout preferredEmailLayout = ((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_email));
			// set layout visible
			preferredEmailLayout.setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.contact_details_preferred_email_line)).setVisibility(View.VISIBLE);
			// set values
			preferredEmailLayout.setText(getActivity(), contact.getPreferredEmail());
			preferredEmailLayout.setLabel(R.string.details_email);
			preferredEmailLayout.setImage(R.drawable.ic_content_email);
			preferredEmailLayout.setOnClickListener(new EmailListener(getActivity(), contact.getPreferredEmail()));
			
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_preferred_email)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.contact_details_preferred_email_line)).setVisibility(View.GONE);
		}

		if(contact.hasContactInfo()) {
			((SubTitleLayout) view.findViewById(R.id.contact_details_general_subtitle)).setVisibility(View.VISIBLE);
			((SubTitleLayout) view.findViewById(R.id.contact_details_general_subtitle)).setTextToTextViewLeft(R.string.details_general_subTitle);
		} else {
			((SubTitleLayout) view.findViewById(R.id.contact_details_general_subtitle)).setVisibility(View.GONE);
		}

		if(!contact.hasAddress() && !contact.hasPostalCode() && !contact.hasCountry()) {
			((LinearLayout) view.findViewById(R.id.contact_details_address_layout)).setVisibility(LinearLayout.GONE);
			((View) view.findViewById(R.id.contact_details_address_layout_line)).setVisibility(View.GONE);
		} else {
			((LinearLayout) view.findViewById(R.id.contact_details_address_layout)).setVisibility(LinearLayout.VISIBLE);
			((View) view.findViewById(R.id.contact_details_address_layout_line)).setVisibility(View.VISIBLE);
		}
		
		if(contact.hasPostalCode()) {
			((TextView) view.findViewById(R.id.contact_details_postal_code)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.contact_details_postal_code)).setText(contact.getPostalCode());
		} else {
			((TextView) view.findViewById(R.id.contact_details_postal_code)).setVisibility(View.GONE);
		}

		if(contact.hasCountry()) {
			((TextView) view.findViewById(R.id.contact_details_country)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.contact_details_country)).setText(contact.getCountry());
		} else {
			((TextView) view.findViewById(R.id.contact_details_country)).setVisibility(View.GONE);
		}

		if(contact.hasAddress()) {
			((TextView) view.findViewById(R.id.contact_details_address)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.contact_details_address)).setText(contact.getAddress());
		} else {
			((TextView) view.findViewById(R.id.contact_details_address)).setVisibility(View.GONE);
		}
		
		if(contact.hasPhone()) {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.contact_details_phone_line)).setVisibility(View.VISIBLE);
			
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setText(getActivity(), contact.getPhone());
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setLabel(R.string.details_phone);
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setImage(R.drawable.ic_device_access_call);
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setOnClickListener(
					new PhoneCallListener(getActivity(), contact.getPhone()));			
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_phone)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.contact_details_phone_line)).setVisibility(View.GONE);
		}

		if(contact.hasFax()) {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_fax)).setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.contact_details_fax_line)).setVisibility(View.VISIBLE);
			
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_fax)).setText(getActivity(), contact.getFax());
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_fax)).setLabel(R.string.details_fax);
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_fax)).setImage(R.drawable.ic_device_access_call);
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_fax)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.contact_details_fax_line)).setVisibility(View.GONE);
		}

		if(contact.hasEmail()) {
			ValueLabelImageLayout emailLayout = ((ValueLabelImageLayout) view.findViewById(R.id.contact_details_email));
			emailLayout.setVisibility(View.VISIBLE);
			((View) view.findViewById(R.id.contact_details_email_line)).setVisibility(View.VISIBLE);

			emailLayout.setText(getActivity(), contact.getEmail());
			emailLayout.setLabel(R.string.details_email);
			emailLayout.setImage(R.drawable.ic_content_email);
			emailLayout.setOnClickListener(new EmailListener(getActivity(), contact.getEmail()));		
		} else {
			((ValueLabelImageLayout) view.findViewById(R.id.contact_details_email)).setVisibility(View.GONE);
			((View) view.findViewById(R.id.contact_details_email_line)).setVisibility(View.GONE);
		}
		
		if(contact.hasFiscalId()) {
			((LinearLayout) view.findViewById(R.id.contact_details_nif_layout)).setVisibility(LinearLayout.VISIBLE);
			((View) view.findViewById(R.id.contact_details_nif_layout_line)).setVisibility(View.VISIBLE);
			
			((TextView) view.findViewById(R.id.contact_details_nif)).setText(contact.getFiscalId());
		} else {
			((LinearLayout) view.findViewById(R.id.contact_details_nif_layout)).setVisibility(LinearLayout.GONE);
			((View) view.findViewById(R.id.contact_details_nif_layout_line)).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.action_bar_contact_details, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
			case R.id.refresh_tab:
				// refresh and get contact details from server
				String[] params = new String[] {contact.getId(), "true"};
				new ContactDetailsRestHandler(getActivity()).execute(params);
				break;
			default:
				break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }
	
}
