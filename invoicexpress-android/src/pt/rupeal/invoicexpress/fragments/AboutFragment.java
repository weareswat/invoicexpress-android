package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.listeners.EmailListener;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate view
		View view = inflater.inflate(R.layout.about, container, false); 
		// set email listener
		view.findViewById(R.id.about_support).setOnClickListener(
				new EmailListener(getActivity(), InvoiceXpress.EMAIL_INVOICEXPRESS_SUPPORT));
		
		return view;
	}
	
}
