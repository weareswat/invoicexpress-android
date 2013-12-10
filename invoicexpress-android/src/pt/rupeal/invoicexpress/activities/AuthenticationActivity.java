package pt.rupeal.invoicexpress.activities;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.server.AuthenticationRestHandler;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.accounts.AccountAuthenticatorActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticationActivity extends AccountAuthenticatorActivity {

	private EditText username;
	private EditText password;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set view login
		setContentView(R.layout.login);
		// user name
		username = (EditText) findViewById(R.id.login_email);
		// the margin top should be settled according the height
		boolean isLowerThanHdpi = ScreenLayoutUtil.isLowerThanHdpi(this);
		int pixels = Math.round(ScreenLayoutUtil.convertDpToPixels(this, isLowerThanHdpi ? 0 : 30));
		((ViewGroup.MarginLayoutParams) username.getLayoutParams()).topMargin = pixels; 
		// password
		password = (EditText) findViewById(R.id.login_password);
		// the margin top should be settled according the height
		pixels = Math.round(ScreenLayoutUtil.convertDpToPixels(this, isLowerThanHdpi ? 0 : 10));
		((ViewGroup.MarginLayoutParams) password.getLayoutParams()).topMargin = pixels;
		// login button
		Button loginButton = (Button) findViewById(R.id.login);
		pixels = Math.round(ScreenLayoutUtil.convertDpToPixels(this, isLowerThanHdpi ? 0 : 36));
		((ViewGroup.MarginLayoutParams) loginButton.getLayoutParams()).topMargin = pixels;
		pixels = Math.round(ScreenLayoutUtil.convertDpToPixels(this, isLowerThanHdpi ? 50 : 70));
		((ViewGroup.MarginLayoutParams) loginButton.getLayoutParams()).height = pixels;
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
				// make login
				if(isValidLoginData()) {
		    		login();
		    	}
			}
		});
		
		Button addAccountButton = (Button) findViewById(R.id.create_account);
		CharSequence text = addAccountButton.getText();
		SpannableString content = new SpannableString(text);
		content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
		addAccountButton.setText(content);
		addAccountButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
		    	// vibrate - time in milliseconds
		    	((Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
		    	// start browser activity
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(InvoiceXpress.INVOICE_EXPRESS_URL));
				startActivity(i);
				// finish activity
				Intent returnIntent = new Intent();
				setResult(Activity.RESULT_CANCELED, returnIntent);     
				finish();
			}
		});
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private void login() {
    	String[] params = new String[] {username.getText().toString(), password.getText().toString()};
		new AuthenticationRestHandler(this).execute(params);
    }
    
    private boolean isValidLoginData() {
    	
    	if(username.getText().toString().isEmpty() 
    			|| password.getText().toString().isEmpty()) {
    		
    		if(InvoiceXpress.DEBUG) {
    			Log.d(this.getClass().getCanonicalName(), "UserName: " + username.getText().toString() + " Password: " +
    					password.getText().toString());
    		}
    		
    		Toast.makeText(this, getResources().getString(R.string.error_fields_mandatory), Toast.LENGTH_LONG).show();
    		return false;
    	}
    	
    	if(!StringUtil.isValidEmailAddress(username.getText().toString())) {
    		if(InvoiceXpress.DEBUG) {
    			Log.d(this.getClass().getCanonicalName(), "UserName: " + username.getText().toString());
    		}
    		Toast.makeText(this, getResources().getString(R.string.error_invalid_username), Toast.LENGTH_LONG).show();
    		return false;
    	}
    	
    	return true;
    }
	
}
