package pt.rupeal.invoicexpress.server;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError;
import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import pt.rupeal.invoicexpress.widgets.DialogProgressBar;

public abstract class AsyncTask<Params, Progress, Result> extends android.os.AsyncTask<Params, Progress, Result> {

	protected Context context;

	private DialogProgressBar progressBar;
	
	private InvoiceXpressError error;
	
	protected void setError(String message, InvoiceXpressErrorType type) {
		error = new InvoiceXpressError(message, type);
	}
	
	protected void setError(int id, InvoiceXpressErrorType type) {
		error = new InvoiceXpressError(context, id, type);
	}
	
	protected boolean existsError() {
		return error != null;
	}
	
	protected void processError() {
		Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onPreExecute() {
		// show progress bar
    	progressBar = DialogProgressBar.newInstace();
    	progressBar.show(((Activity) context).getFragmentManager(), FragmentTagsEnum.DIALOG_PROGRESS.getValue());
	}
	
	@Override
	protected void onPostExecute(Result result) {
		// dismiss progress bar
		progressBar.dismiss();
	}
	
	public void dismissProgressBar() {
		progressBar.dismiss();
	}
	
}
