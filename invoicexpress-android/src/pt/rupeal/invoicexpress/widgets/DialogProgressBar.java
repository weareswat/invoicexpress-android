package pt.rupeal.invoicexpress.widgets;

import pt.rupeal.invoicexpress.R;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DialogProgressBar extends DialogFragment {

	/**
	 * This attribute allow me to take the control of show and dismiss methods.
	 */
	private boolean isShowing;
	
    /**
     * Create a new instance. 
     * However this only will be instantiated in MainActivity.
     */
    public static DialogProgressBar newInstace() {
    	return new DialogProgressBar();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate view and return it
		return inflater.inflate(R.layout.dialog_progress, null);
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {
		if(!isShowing) {
			FragmentTransaction ft = manager.beginTransaction();
	        ft.add(android.R.id.content, this, tag);
	        ft.commit();
	        
	        isShowing = true;
		}
	}
	
	@Override
	public void dismiss() {
		if(isShowing) {
	        super.dismiss();
	        isShowing = false;
		}
	}
	
}
