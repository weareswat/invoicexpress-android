package pt.rupeal.invoicexpress.adapters;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.ContactDetailsFragment;
import pt.rupeal.invoicexpress.fragments.ContactsFragment;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.widgets.TextViewInvoiceXpress;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ContactListRowAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private ContactsFragment contactsFragment;
	
	private List<ContactModel> contacts;
	private List<ContactModel> originalContacts;
	
	private Filter filter;
	
	public ContactListRowAdapter(Context context, ContactsFragment contactsFragment, List<ContactModel> contacts){
		this.context = context;
		this.contactsFragment = contactsFragment;
		this.contacts = contacts;
		this.originalContacts = contacts;
	}
	
	public void setContacts(List<ContactModel> contacts) {
		this.contacts = contacts;
		this.originalContacts = contacts;		
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(view == null) {
			view = inflater.inflate(R.layout.contacts_list_row_fragment, parent, false);
		}

		LinearLayout letterLayout = (LinearLayout) view.findViewById(R.id.contact_list_letter);
		letterLayout.removeAllViews();
		
		ContactModel contact = contacts.get(position);
		if(contact.isFirst()){
			
			View emptyView = new View(context);
			emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 40));
			letterLayout.addView(emptyView);
			
			ContactsLetterTextView letterTextView = new ContactsLetterTextView(context);
			letterTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			letterTextView.setBackgroundColor(context.getResources().getColor(R.color.green_shadow));
			letterTextView.setTextColor(context.getResources().getColor(R.color.green_normal));
			
			String contactLetter = String.valueOf(Normalizer.normalize(contact.getName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").charAt(0));
			letterTextView.setText(contactLetter);
			
			letterTextView.setTextSize(12);
			letterTextView.setPadding(12, 8, 0, 8);
			letterLayout.addView(letterTextView);
			
			View lineSeperator = new View(context);
			lineSeperator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2));
			lineSeperator.setBackgroundColor(context.getResources().getColor(R.color.green_normal));
			letterLayout.addView(lineSeperator);
			
		}
		// contact name
		TextViewInvoiceXpress contactTitleTextView = (TextViewInvoiceXpress) view.findViewById(R.id.contact_name);
		int margin = contactTitleTextView.getPaddingLeft();
		margin += contactTitleTextView.getPaddingRight();
		// margins in contacts_list_fragment.xml
		margin += Math.round(2 * ScreenLayoutUtil.convertDpToPixels(context, 18));
		// add margin no justification
		margin += Math.round(ScreenLayoutUtil.convertDpToPixels(context, 10));
		contactTitleTextView.setText(context, contact.getName(), margin, TextViewInvoiceXpress.RESIZE);
		// Add click listener
		view.setOnClickListener(new ContactListRowOnClickListener(contact));
		// return contact row view
		return view;
	}
		
	private class ContactListRowOnClickListener implements View.OnClickListener {

		private ContactModel contact;
		
		public ContactListRowOnClickListener(ContactModel contact){
			this.contact = contact;
		}
		
		@Override
		public void onClick(View v) {
	    	// vibrate - time in milliseconds
	    	((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
	    	// set arguments
			Bundle args = new Bundle();
			args.putSerializable(ContactModel.CONTACT, contact);
			// set contact details fragment
			((MainActivity) contactsFragment.getActivity()).addFragment(ContactDetailsFragment.class, 
					FragmentTagsEnum.CONTACTS_DETAILS,
					args);
		}
		
	}
		
	private class ContactsLetterTextView extends TextView {

		public ContactsLetterTextView(Context context) {
			super(context);
		}
		
		@Override
		public void setPressed(boolean pressed) {
			 // If the parent is pressed, do not set to pressed.
	        if (pressed && ((View) getParent()).isPressed()) {
	            return;
	        }
	        
			super.setPressed(pressed);
		}

	}

	@Override
	public Filter getFilter() {
		if(filter == null) {
			filter = new ContactFilter();
		}
		
		return filter;
	}
	
	private class ContactFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// Initialized filter result
			FilterResults result = new FilterResults();
		    // check if the constraint is empty or not
			if(constraint != null && constraint.toString().length() > 0) {
				// normalize and ignore accents
				//String stringToSearch = Normalizer.normalize(constraint, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase(Locale.getDefault());
				String stringToSearch = constraint.toString().toLowerCase(Locale.getDefault());
				
				ArrayList<ContactModel> filteredItems = new ArrayList<ContactModel>();
		 
			    for(int i = 0, l = originalContacts.size(); i < l; i++) {
			    	ContactModel contactModel = originalContacts.get(i);
			    	
			    	String[] contactNameSplited = contactModel.getName().toLowerCase(Locale.getDefault()).split(" ");
			    	boolean found = false;
			    	for(int j = 0; j < contactNameSplited.length && !found; j++) {
			    		String contactNameSplitedNormalized = Normalizer.normalize(contactNameSplited[j], Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			    		if(contactNameSplitedNormalized.startsWith(stringToSearch)) {
			    			ContactModel contactFiltered = new ContactModel(contactModel);
			    			contactFiltered.setFirst(false);
			    			filteredItems.add(contactFiltered);
			    			found = true;
			    		}
					}
			    }
			    
			    result.values = filteredItems.toArray(new ContactModel[]{});
			    result.count = filteredItems.size();
		    } else {
		    	synchronized(this) {
		    		result.values = originalContacts.toArray(new ContactModel[]{});
		    		result.count = originalContacts.size();
		    	}
		    }
			
		    return result;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			contacts = Arrays.asList((ContactModel[]) results.values);
			notifyDataSetChanged();
		}
		
	}
	
}
