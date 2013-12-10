package pt.rupeal.invoicexpress.fragments;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.model.DocumentModel;
import pt.rupeal.invoicexpress.model.EmailModel;
import pt.rupeal.invoicexpress.server.SendEmailRestHandler;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EmailFragment extends Fragment {

	private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// eanble menu
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.email, container, false);
		return view;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 * 
	 * The method onResume is override because the editTexts. The editTexts are saved in mSavedViewstate property.
	 * It's necessarily call the setText here in onResume method.
	 */
	@Override
	public void onResume() {
		
		EmailModel email = (EmailModel) getArguments().getSerializable(EmailModel.EMAIL);
		
		((EditText) view.findViewById(R.id.email_to)).setText(email.getTo());
		((EditText) view.findViewById(R.id.email_subject)).setText(email.getSubject());
		((EditText) view.findViewById(R.id.body)).setText(email.getBody());
		
		super.onResume();
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.action_bar_email, menu);
        MenuItem emailMenuItem = menu.findItem(R.id.email_tab);
        emailMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

        	@Override
			public boolean onMenuItemClick(MenuItem item) {
        		String[] params = new String[] {((EditText) view.findViewById(R.id.email_to)).getText().toString(), 
        				((EditText) view.findViewById(R.id.email_subject)).getText().toString(), 
        				((EditText) view.findViewById(R.id.body)).getText().toString(),
        				getArguments().getString(DocumentModel.ID, ""),
        				getArguments().getString(DocumentModel.DOC_TYPE, "")};
        		new SendEmailRestHandler(getActivity()).execute(params);
        		
				return true;
			}
        	
		});
        
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    
}
