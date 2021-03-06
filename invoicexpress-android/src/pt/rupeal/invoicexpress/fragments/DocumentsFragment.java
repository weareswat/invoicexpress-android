package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.enums.RoleEnum;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment.DocumentFilterFragment;
import pt.rupeal.invoicexpress.layouts.ImageButtonLayout;
import pt.rupeal.invoicexpress.model.ContactModel;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.server.DocumentsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class DocumentsFragment extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable options menu
		setHasOptionsMenu(true);
		// display button home
		((MainActivity) getActivity()).getActionBar().setDisplayHomeAsUpEnabled(true);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate view
		View view = inflater.inflate(R.layout.documents_types, container, false);
		//ALL
		((Button) view.findViewById(R.id.doc_type_all)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.ALL));
		
		// INVOICE
		((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setImage(R.drawable.doc_type_icon_1);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setLabel(R.string.doc_type_invoice, true);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.INVOICE));
		
		if(InvoiceXpress.getInstance().getActiveAccountDetails().isFromPortugal()) {
			// INVOICE
			LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setLayoutParams(layoutParams);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setGravity(Gravity.CENTER_VERTICAL);
			
			// SIMPLIFIED INVOICE
			((ImageButtonLayout) view.findViewById(R.id.doc_type_simple_invoice)).setVisibility(View.VISIBLE);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_simple_invoice)).setImage(R.drawable.doc_type_icon_2);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_simple_invoice)).setLabel(R.string.doc_type_simple_invoice, true);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_simple_invoice)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.SIMPLIFIEDINVOICE));			
		} else {
			// INVOICE
			LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 2);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setLayoutParams(layoutParams);
			((ImageButtonLayout) view.findViewById(R.id.doc_type_invoice)).setGravity(Gravity.CENTER);
			
			// SIMPLIFIED INVOICE			
			((ImageButtonLayout) view.findViewById(R.id.doc_type_simple_invoice)).setVisibility(View.GONE);
		}
		
		// CASH
		((ImageButtonLayout) view.findViewById(R.id.doc_type_cash)).setImage(R.drawable.doc_type_icon_2);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_cash)).setLabel(R.string.doc_type_cash, true);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_cash)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.CASHINVOICE));			
		// CREDIT
		((ImageButtonLayout) view.findViewById(R.id.doc_type_credit)).setImage(R.drawable.doc_type_icon_3);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_credit)).setLabel(R.string.doc_type_credit, true);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_credit)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.CREDITNOTE));			
		// DEBIT
		((ImageButtonLayout) view.findViewById(R.id.doc_type_debit)).setImage(R.drawable.doc_type_icon_4);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_debit)).setLabel(R.string.doc_type_debit, true);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_debit)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.DEBITNOTE));		
		// RECEIPT
		((ImageButtonLayout) view.findViewById(R.id.doc_type_receipt)).setImage(R.drawable.doc_type_icon_5);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_receipt)).setLabel(R.string.doc_type_receipt, true);
		((ImageButtonLayout) view.findViewById(R.id.doc_type_receipt)).setOnClickListener(new DocumentTypeListener(DocumentTypeEnum.RECEIPT));			
		
		return view;
	}
	
    private class DocumentTypeListener implements View.OnClickListener {

    	private DocumentTypeEnum docTypeEnum;
    	
    	public DocumentTypeListener(DocumentTypeEnum docTypeEnum) {
			this.docTypeEnum = docTypeEnum;
		}
    	
		@Override
		public void onClick(View v) {
			// get document type
			String docType = docTypeEnum.getValue();
			
			// logging
			if(InvoiceXpress.DEBUG) {
				Log.d(this.getClass().getCanonicalName(), "Documents type clicked.. " + docType);
			}
			
			// check if the progress bar is visible to disable clicks
			if(!InvoiceXpress.isInvoiceXpressClickable(getActivity())) {
				return;
			}
			
			// vibrate - time in milliseconds
			((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

			// verify if the user can consult invoices according his roles
			if(!RoleEnum.isAllowToConsultInvoices(InvoiceXpress.getInstance().getActiveAccount().getRoles())) {
				Toast.makeText(getActivity(), R.string.error_documents_roles, Toast.LENGTH_LONG).show();
				return;
			}
			
			// check if there is documents in cache
			// if true then add new fragment else get documents from server
			if(InvoiceXpress.getInstance().existsDocumentsTemp(docType)) {
				// logging
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), "Get cached documents: " + docType);
				}
				// set arguments
	    		Bundle args = new Bundle();
	    		args.putString(DocumentModel.DOC_TYPE, docType);
	    		args.putString(ContactModel.ID, "");
	    		// call fragment
	    		((MainActivity) getActivity()).addFragment(DocumentsListFragment.class, 
	    				FragmentTagsEnum.DOCUMENTS_LIST,
	    				args); 
				
			} else {
				// logging
				if(InvoiceXpress.DEBUG) {
					Log.d(this.getClass().getCanonicalName(), "Get documents from server: " + docType);
				}
				// set parameters
				String[] params = new String[]{docType, "", 
						String.valueOf(DocumentFilterFragment.NO_FILTER), ""};
				// execute web service
				DocumentsRestHandler restHandler = new DocumentsRestHandler(getActivity());
				InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
				restHandler.execute(params);
			}
			
		}
    	
    }	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
	}
	
}
