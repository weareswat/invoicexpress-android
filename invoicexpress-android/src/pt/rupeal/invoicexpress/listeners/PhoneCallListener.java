package pt.rupeal.invoicexpress.listeners;

import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;

public class PhoneCallListener implements  View.OnClickListener {

	private Activity activity;
	private String phone;
	
	public PhoneCallListener(Activity activity, String phone) {
		this.activity = activity;
		this.phone = phone;
	}
	
	@Override
	public void onClick(View v) {
		// check if the progress bar is visible to disable clicks
		if(!InvoiceXpress.isInvoiceXpressClickable(activity)) {
			return;
		}
		
    	// vibrate - time in milliseconds
    	((Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
    	
		// telephone action
		String phoneCallUri = "tel:" + phone;
		Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
		phoneCallIntent.setData(Uri.parse(phoneCallUri));
		activity.startActivity(phoneCallIntent);
	}

}
