package pt.rupeal.invoicexpress.utils;

import pt.rupeal.invoicexpress.utils.InvoiceXpressError.InvoiceXpressErrorType;
import android.content.Context;

public class InvoiceXpressException extends Exception {

	private static final long serialVersionUID = 2162628751450984378L;

	private InvoiceXpressErrorType type;
	
	public InvoiceXpressException(String message) {
		this(message, InvoiceXpressErrorType.ERROR);
	}
	
	public InvoiceXpressException(String message, InvoiceXpressErrorType type) {
		super(message);
		this.type = type;
	}	
	
	public InvoiceXpressException(Context context, int idMessage) {
		this(context.getResources().getString(idMessage), InvoiceXpressErrorType.ERROR);
	}
	
	public InvoiceXpressException(Context context, int idMessage, InvoiceXpressErrorType type) {
		super(context.getResources().getString(idMessage));
		this.type = type;
	}	
	
	public InvoiceXpressErrorType getType() {
		return type;
	}
	
	public boolean isFatal() {
		return type == InvoiceXpressErrorType.FATAL;
	}
	
}
