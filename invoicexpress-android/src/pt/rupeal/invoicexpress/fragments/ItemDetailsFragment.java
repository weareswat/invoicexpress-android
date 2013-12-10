package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.layouts.SubTitleLayout;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.ItemModel;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemDetailsFragment extends Fragment {

	public ItemDetailsFragment() {
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.item_details, container, false);
		
		ItemModel item = (ItemModel) getActivity().getIntent().getExtras().getSerializable(ItemModel.ITEM);
		
		((TextView) view.findViewById(R.id.item_details_title)).setText("Factura nº " + getActivity().getIntent().getExtras().getString(DocumentModel.SEQUENCE_NUMBER));
		
		((SubTitleLayout) view.findViewById(R.id.item_details_name_subtitle)).setTextToTextViewLeft(R.string.item_details_name);
		((TextView) view.findViewById(R.id.item_details_name_value)).setText(item.getName());
		
		if(!item.getDescription().isEmpty()){
			((SubTitleLayout) view.findViewById(R.id.item_details_description_subtitle)).setTextToTextViewLeft(R.string.item_details_description);
			((TextView) view.findViewById(R.id.item_details_description_value)).setText(item.getDescription());
		} else {
			((SubTitleLayout) view.findViewById(R.id.item_details_description_subtitle)).setVisibility(LinearLayout.GONE);
			((TextView) view.findViewById(R.id.item_details_description_value)).setVisibility(TextView.GONE);			
		}
		
		((SubTitleLayout) view.findViewById(R.id.item_details_values_subtitle)).setTextToTextViewLeft(R.string.item_details_values);
		
		((TextView) view.findViewById(R.id.item_details_unitPrice_value)).setText(StringUtil.convertToMoneyValue(item.getUnitPrice()));
		((TextView) view.findViewById(R.id.item_details_quantity_value)).setText(String.valueOf(item.getQuantity()));
		((TextView) view.findViewById(R.id.item_details_taxes_value)).setText(StringUtil.convertToMoneyValue(item.getTaxAmount()));
		((TextView) view.findViewById(R.id.item_details_discounts_value)).setText(StringUtil.convertToMoneyValue(item.getDiscount()));
		
		((TextView) view.findViewById(R.id.item_details_total_value)).setText(StringUtil.convertToMoneyValue(item.getTotal()));		
		
		return view;
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	menu.clear();
    }	
	
}
