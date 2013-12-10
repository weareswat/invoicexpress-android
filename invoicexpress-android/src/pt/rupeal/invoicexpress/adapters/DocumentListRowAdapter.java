package pt.rupeal.invoicexpress.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;
import pt.rupeal.invoicexpress.enums.DocumentTypeEnum;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.DocumentDetailsFragment;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DocumentListRowAdapter extends BaseAdapter implements ListAdapter, Filterable {

	private Context context;
	
	private List<DocumentModel> documents;
	private List<DocumentModel> originalDocuments;
	
	// for search purpose
	private Filter filter;
	
	private LinearLayout documentsMoreLayout;
	
	public DocumentListRowAdapter(Context context, List<DocumentModel> documents, LinearLayout documentsMoreLayout) {
		this.context = context;
		this.documents = documents;

		this.originalDocuments = documents;
		
		this.documentsMoreLayout = documentsMoreLayout;
	}
	
	public void setDocuments(List<DocumentModel> documents) {
		this.documents = documents;
		this.originalDocuments = documents;		
	}
	
	@Override
	public int getCount() {
		return documents.size();
	}

	@Override
	public Object getItem(int position) {
		return documents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView == null){
			convertView = inflater.inflate(R.layout.documents_list_row, parent, false);
		}
		
		DocumentModel document = documents.get(position);
		
		// Fill the Documents Row with associated data.
		
		// Client Name
		TextView clientTextView = (TextView) convertView.findViewById(R.id.doc_client);
		// for cash invoices or simplified invoices the client can be empty
		String clientName = StringUtil.resizeString((Activity) context, document.getClientName(), 
					clientTextView.getTextSize(),
					ScreenLayoutUtil.convertDpToPixels(context, 150));
		clientTextView.setText(clientName);			
		
		// Document Type
		ImageView type = (ImageView) convertView.findViewById(R.id.doc_type);
		type.setImageResource(DocumentTypeEnum.getDrawableId(document.getType()));
		
		// Documents Ammount
		TextView value = (TextView) convertView.findViewById(R.id.doc_ammount);
		value.setText(StringUtil.convertToMoneyValue(document.getTotal()));
		
		// Documents Date
		TextView date = (TextView) convertView.findViewById(R.id.doc_client_date);
		date.setText(document.getSequenceNumber());
		date.setPadding(clientTextView.getCompoundPaddingLeft(), 0, 0, 0);
		
		// Documents Dates (start and end) 
		TextView startDate = (TextView) convertView.findViewById(R.id.doc_start_date);
		startDate.setText(document.getDate());
		
		// Documents Status
		ImageView status = (ImageView) convertView.findViewById(R.id.doc_status);
		status.setImageResource(DocumentStatusEnum.getDrawableId(document.getStatus()));
		
		// Documents End Date
		TextView dueDate = (TextView) convertView.findViewById(R.id.doc_due_date);

		if(document.isOverDueDate()) {
			// TODO remove from xml file
			dueDate.setTextColor(context.getResources().getColor(R.color.documents_over_due_red));
			((ImageView) convertView.findViewById(R.id.doc_image_due_date)).setVisibility(ImageView.INVISIBLE);
		} else {
			// TODO remove from xml file
			dueDate.setTextColor(context.getResources().getColor(R.color.documents_over_due_normal));
			((ImageView) convertView.findViewById(R.id.doc_image_due_date)).setVisibility(ImageView.INVISIBLE);
		}
		dueDate.setText(document.getDueDate());

		// Add click listener
		convertView.setOnClickListener(new DocumentListRowOnClickListener(document));
		
		return convertView;
	}
	
	private class DocumentListRowOnClickListener implements View.OnClickListener {

		private DocumentModel document;
		
		public DocumentListRowOnClickListener(DocumentModel document) {
			this.document = document;
		}
		
		@Override
		public void onClick(View v) {
	    	// vibrate - time in milliseconds
	    	((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
			// set document as argument
			Bundle args = new Bundle();
			args.putSerializable(DocumentModel.DOCUMENT, document);
			// call document details fragment
			((MainActivity) context).addFragment(DocumentDetailsFragment.class, 
					FragmentTagsEnum.DOCUMENTS_DETAILS, 
					args);
		}
		
	}

	@Override
	public Filter getFilter() {
		if(filter == null) {
			filter = new DocumentFilter();
		}
		
		return filter;
	}
	
	private class DocumentFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			constraint = constraint.toString().toLowerCase(Locale.getDefault());
			
			FilterResults result = new FilterResults();
		    
			if(constraint != null && constraint.toString().length() > 0) {
				
				ArrayList<DocumentModel> filteredItems = new ArrayList<DocumentModel>();
		 
			    for(int i = 0, l = originalDocuments.size(); i < l; i++) {
			    	DocumentModel documentModel = getOriginalItem(i);
			     
			    	if(documentModel.getClientName().toLowerCase(Locale.getDefault()).contains(constraint)
				    			|| documentModel.getDate().toLowerCase(Locale.getDefault()).contains(constraint)
				    			|| documentModel.getDueDate().toLowerCase(Locale.getDefault()).contains(constraint)
				    			|| documentModel.getSequenceNumber().toLowerCase(Locale.getDefault()).contains(constraint)) {
			    		
			    		DocumentModel documentFiltered = new DocumentModel(documentModel);
			    		filteredItems.add(documentFiltered);
			    	}
			    }
			    
			    result.values = filteredItems.toArray(new DocumentModel[]{});
			    result.count = filteredItems.size();
		    } else {
		    	synchronized(this) {
		    		result.values = originalDocuments.toArray(new DocumentModel[]{});
		    		result.count = originalDocuments.size();
		    	}
		    }
			
			return result;
		}
		
		public DocumentModel getOriginalItem(int position) {
			int index = 0;
			Iterator<DocumentModel> iterator = originalDocuments.iterator();
			while(iterator.hasNext()) {
				DocumentModel document = iterator.next();
				if(index == position) {
					return document;
				}
				index++;
			}
			
			return null;
		}		

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			documents = Arrays.asList((DocumentModel[]) results.values);
    		if(documentsMoreLayout != null) {
    			if(constraint.equals("")) {
    				documentsMoreLayout.setVisibility(View.VISIBLE);
    			} else {
    				documentsMoreLayout.setVisibility(View.GONE);
    			}
    		}
    		
			notifyDataSetChanged();
		}
		
	}

}

