package pt.rupeal.invoicexpress.listeners;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

public class EmailListener implements View.OnClickListener {

	private Activity activity;
	private String to;

	public EmailListener(Activity activity, String to) {
		this.activity = activity;
		this.to = to;
	}	
	
	@Override
	public void onClick(View view) {
		// check if the progress bar is visible to disable clicks
		if(!InvoiceXpress.isInvoiceXpressClickable(activity)) {
			return;
		}
		
    	// vibrate - time in milliseconds
    	((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {to});
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT   , "");
		
		try {
		    activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.email)));
		} catch (ActivityNotFoundException e) {
			Log.e(this.getClass().getCanonicalName(), e.getMessage());
		}
	}

}
