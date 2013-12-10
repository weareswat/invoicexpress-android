package pt.rupeal.invoicexpress.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {

	/* 
	 * This class is called by Android when the user clicks on your Account Authenticator 
	 * in Android's Account Manager.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return new AccountAuthenticator(this).getIBinder();
	}

}
