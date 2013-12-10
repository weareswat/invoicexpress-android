package pt.rupeal.invoicexpress.fragments;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.layouts.LinkTwoLabelsLayout;
import pt.rupeal.invoicexpress.layouts.SubTitleLayout;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.EmailModel;
import pt.rupeal.invoicexpress.model.ItemModel;
import pt.rupeal.invoicexpress.server.ContactDetailsRestHandler;
import pt.rupeal.invoicexpress.server.DocumentChangeStatusRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;
import pt.rupeal.invoicexpress.widgets.TextViewInvoiceXpress;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DocumentDetailsFragment extends Fragment {

	private View view;
	private DocumentModel document;
	
	private boolean isStatusChanged;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable menu
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.document_detail, container, false);
		
		document = (DocumentModel) getArguments().get(DocumentModel.DOCUMENT);
		
		((ImageView) view.findViewById(R.id.doc_details_image_title)).setImageResource(DocumentStatusEnum.getDrawableId(document.getStatus()));
		
		((TextView) view.findViewById(R.id.doc_details_status)).setText(DocumentStatusEnum.getLabelGui(getActivity(), document.getStatus()));
		
		if(!document.getClientId().isEmpty()) {
			view.findViewById(R.id.doc_details_image_link_name).setVisibility(View.VISIBLE);
			view.findViewById(R.id.doc_details_vertical_line_link_name).setVisibility(View.VISIBLE);
			// set listener 
			view.findViewById(R.id.doc_details_name_layout).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// check if the progress bar is visible to disable clicks
					if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
						return;
					}
					// vibrate - time in milliseconds
					((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
					// if the contact was not in cache then the application will got it from server
					// else just pop it from cache
					if(InvoiceXpress.getInstance().getContacts().containsKey(document.getClientId())) {
						ContactModel contact = InvoiceXpress.getInstance().getContacts().get(document.getClientId());
						Bundle args = new Bundle();
						args.putSerializable(ContactModel.CONTACT, contact);
						// set contact details fragment
						((MainActivity) getActivity()).addFragment(ContactDetailsFragment.class, 
								FragmentTagsEnum.CONTACTS_DETAILS,
								args);
					} else {
						String[] params = new String[] {document.getClientId(), "false"};
						new ContactDetailsRestHandler(getActivity()).execute(params);
					}
					
				}
			});
		} else {
			view.findViewById(R.id.doc_details_image_link_name).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.doc_details_vertical_line_link_name).setVisibility(View.INVISIBLE);
		}
		
		// set contact name
		TextViewInvoiceXpress clientNameTextView = ((TextViewInvoiceXpress) view.findViewById(R.id.doc_details_name));
		int margin = ((MarginLayoutParams) clientNameTextView.getLayoutParams()).leftMargin;
		margin += ((MarginLayoutParams) clientNameTextView.getLayoutParams()).rightMargin;
		// extra document_details 18dp relative layout margins
		margin +=  Math.round(2 * ScreenLayoutUtil.convertDpToPixels(getActivity(), 30));
		if(!document.getClientId().isEmpty()) {
			margin +=  Math.round(ScreenLayoutUtil.convertDpToPixels(getActivity(), 30));
		}
		clientNameTextView.setText(getActivity(), document.getClientName(), margin, TextViewInvoiceXpress.RESIZE);
		
		((TextView) view.findViewById(R.id.doc_details_sequenceNumber_value)).setText(document.getSequenceNumber());
		((TextView) view.findViewById(R.id.doc_details_date_value)).setText(document.getDate());
		
		TextView dueDateTextView = ((TextView) view.findViewById(R.id.doc_details_dueDate_value));
		dueDateTextView.setText(document.getDueDate());
		// set red color for due date document
		if(document.isOverDueDate()) {
			dueDateTextView.setTextColor(getActivity().getResources().getColor(R.color.documents_over_due_red));
		} else {
			dueDateTextView.setTextColor(getActivity().getResources().getColor(R.color.details_text));
		}
		
		// sub title items "Items"
		((SubTitleLayout) view.findViewById(R.id.doc_details_items_subtitle)).setTextToTextViewLeft(R.string.doc_details_items_subtitle_Left);
		
		// Items
		LinearLayout itemsLinearLayout = (LinearLayout) view.findViewById(R.id.doc_details_items);
		HashMap<String, ItemModel> docItems = document.getItems();
		Set<String> docItemsKeys = docItems.keySet();
		LinkTwoLabelsLayout itemLink = null;
		for (String key : docItemsKeys) {
			RelativeLayout rel = new RelativeLayout(getActivity());
			itemLink = new LinkTwoLabelsLayout(getActivity(), rel);
			// set value
			itemLink.setValue(StringUtil.convertToMoneyValue(docItems.get(key).getSubTotal()));
			// set label
			// left and right margin
			margin = Math.round(2 * ScreenLayoutUtil.convertDpToPixels(getActivity(), 18));
			itemLink.setLabel(getActivity(), key, margin);
			// set listener
			itemLink.getLinkView().setOnClickListener(new ItemOnClickListener(document.getSequenceNumber(), docItems.get(key)));
			
			itemsLinearLayout.addView(itemLink.getLinkView());
		}
		// sub title items "Total w/Taxes"
		((SubTitleLayout) view.findViewById(R.id.doc_details_items_subtitle)).setTextToTextViewRight(R.string.doc_details_items_subtitle_Right, itemLink.getMarginRight(getActivity()));
		
		((SubTitleLayout) view.findViewById(R.id.doc_details_values_subtitle)).setTextToTextViewLeft(R.string.doc_details_values_subtitle);

		((TextView) view.findViewById(R.id.doc_details_sum_value)).setText(StringUtil.convertToMoneyValue(document.getSum()));
		((TextView) view.findViewById(R.id.doc_details_discount_value)).setText(StringUtil.convertToMoneyValue(document.getDiscount()));
		((TextView) view.findViewById(R.id.doc_details_beforeTaxes_value)).setText(StringUtil.convertToMoneyValue(document.getBeforeTaxes()));
		((TextView) view.findViewById(R.id.doc_details_taxes_value)).setText(StringUtil.convertToMoneyValue(document.getTaxes()));
		((TextView) view.findViewById(R.id.doc_details_total_value)).setText(StringUtil.convertToMoneyValue(document.getTotal()));
		
		// Notes
		if(!document.getObservations().isEmpty()){
			((SubTitleLayout) view.findViewById(R.id.doc_details_notes_label)).setTextToTextViewLeft(R.string.doc_details_notes_label);
			((TextView) view.findViewById(R.id.doc_details_notes)).setText(document.getObservations());
		} else {
			((SubTitleLayout) view.findViewById(R.id.doc_details_notes_label)).setVisibility(LinearLayout.GONE);
			((TextView) view.findViewById(R.id.doc_details_notes)).setVisibility(TextView.GONE);
		}
		
		// Payment
		if(document.getPayEntity() != null && !document.getPayEntity().isEmpty()){
			((SubTitleLayout) view.findViewById(R.id.doc_details_payment_label)).setTextToTextViewLeft(R.string.doc_details_payment_label);
			((TextView) view.findViewById(R.id.doc_details_invEnt)).setText("Entidade: " + document.getPayEntity());
			((TextView) view.findViewById(R.id.doc_details_invRef)).setText("Referência: " + document.getPayRef());
			((TextView) view.findViewById(R.id.doc_details_invMon)).setText("Montante: " + StringUtil.convertToMoneyValue(document.getTotal()));
		} else {
			((SubTitleLayout) view.findViewById(R.id.doc_details_payment_label)).setVisibility(LinearLayout.GONE);
			((RelativeLayout) view.findViewById(R.id.doc_detail_atm_layout)).setVisibility(RelativeLayout.GONE);
		}
		
		return view;
	}
	
    private class ItemOnClickListener implements View.OnClickListener {

    	private String sequenceNumber;
		private ItemModel item;
		
		public ItemOnClickListener(String sequenceNumber, ItemModel item) {
			this.sequenceNumber = sequenceNumber;
			this.item = item;
		}
		
		@Override
		public void onClick(View v) {
			// check if the progress bar is visible to disable clicks
			if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
				return;
			}
			// get main activity
			MainActivity mainActivity = (MainActivity) getActivity();
	    	// vibrate - time in milliseconds
	    	((Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
			// put sequence number and item data
			mainActivity.getIntent().putExtra(DocumentModel.SEQUENCE_NUMBER, sequenceNumber);
			mainActivity.getIntent().putExtra(ItemModel.ITEM, item);
			// add item details fragment
			mainActivity.addFragment(ItemDetailsFragment.class, 
					FragmentTagsEnum.ITEM);
		}
		
	}
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	menu.clear();
    	inflater.inflate(R.menu.action_bar_document_details, menu);
    	
    	// get document from args
    	document = (DocumentModel) getArguments().get(DocumentModel.DOCUMENT);
    	
		List<DocumentStatusEnum> possibleStatus = InvoiceXpress.getInstance().getStatusGraphs(document.getType()).get(
				DocumentStatusEnum.getDocumentStatusEnum(document.getStatus()));
		for (DocumentStatusEnum status : possibleStatus) {
			String menuItemLabel = status.getActionLabel(getActivity());
			menu.add(Menu.NONE, menuItemLabel.hashCode(), Menu.NONE, menuItemLabel).setOnMenuItemClickListener(
					new ChangeStatusMenuListener(status));
		}
    	
    	menu.findItem(R.id.send_email).setOnMenuItemClickListener(new EmailMenuListener());
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() != R.id.send_email && isStatusChanged) {

			// document was deleted, must remove fragment
			if(DocumentStatusEnum.isDeleted(document.getStatus())) {
				// ((MainActivity) getActivity()).removeFragment(FragmentTagsEnum.DOCUMENTS_DETAILS);
				return true;
			}
			
			((ImageView) view.findViewById(R.id.doc_details_image_title)).setImageResource(DocumentStatusEnum.getDrawableId(document.getStatus()));
		}
		
		return super.onOptionsItemSelected(item);
	}
    
    private class ChangeStatusMenuListener implements MenuItem.OnMenuItemClickListener {

    	private View cancelDialog;
    	
    	private DocumentStatusEnum status;
    	
    	public ChangeStatusMenuListener(DocumentStatusEnum status) {
    		this.status = status;
		}
    	
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			
			if(item.getItemId() == DocumentStatusEnum.CANCELED.getActionLabel(getActivity()).hashCode()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    LayoutInflater inflater = getActivity().getLayoutInflater();

			    // Inflate and set the layout for the dialog
			    // Pass null as the parent view because its going in the dialog layout
			    cancelDialog = inflater.inflate(R.layout.document_detail_cancel_dialog, null);
			    builder.setView(cancelDialog)
			    	// Add action buttons
			    	.setPositiveButton(R.string.doc_details_cancel_dialog_ok, new DialogInterface.OnClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int id) {
		            		   EditText justification = (EditText) cancelDialog.findViewById(R.id.doc_details_cancel_dialog_justification);
		            		   String[] params = new String[] {status.getStateXml(), justification.getText().toString()};
		            		   new DocumentChangeStatusRestHandler(getActivity(), document).execute(params);
			               }
			    	})
			    	.setNegativeButton(R.string.doc_details_cancel_dialog_cancel, new DialogInterface.OnClickListener() {
			    		public void onClick(DialogInterface dialog, int id) {
			    			dialog.dismiss();
			    		}
			    	});
			    
			    builder.show();
			    
			} else {
			
				String[] params = new String[] {status.getStateXml(), ""};
				new DocumentChangeStatusRestHandler(getActivity(), document).execute(params);
					
				return false;
			}
			
			// Return true to consume this click and prevent others from executing.
			return true;
		}
		
    }
    
    private class EmailMenuListener implements MenuItem.OnMenuItemClickListener {

        private MessageFormat emailMessage = new MessageFormat (getActivity().getResources().getString(R.string.email_body_message));
    	
        public EmailMenuListener() {
        	super();
        }	
    	
    	@Override
    	public boolean onMenuItemClick(MenuItem item) {
    		// get document type gui label that it will be show on the interface
    		String typeGuiLabel = getActivity().getResources().getString(DocumentTypeEnum.getLabelGui(document.getType()));
    		// set message parameters, client name, doc type label gui, name account
    		String[] messageParams = new String[] {document.getClientName(), 
    				typeGuiLabel, 
    				InvoiceXpress.getInstance().getActiveAccount().getName()};
    		// generate email body
    		String body = emailMessage.format(messageParams);
			// get main activity
			MainActivity mainActivity = (MainActivity) getActivity();
			// create email model with to, subject and body attributes
			EmailModel email = new EmailModel();
			email.setTo(document.getClientEmail());
			email.setSubject("");
			email.setBody(body);
			
			Bundle args = new Bundle();
			args.putSerializable(EmailModel.EMAIL, email);
			args.putString(DocumentModel.ID, document.getId());
			args.putString(DocumentModel.DOC_TYPE, document.getType());

			mainActivity.addFragment(EmailFragment.class, 
					FragmentTagsEnum.EMAIL,
					args);
    		
    		return true;
    	}

    }    
	
}
