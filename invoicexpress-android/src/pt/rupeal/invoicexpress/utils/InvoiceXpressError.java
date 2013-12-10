package pt.rupeal.invoicexpress.utils;

import android.content.Context;

public class InvoiceXpressError {

	private String message;
	
	/**
	 * ERROR - Show to user with specified message
	 * FATAL - Show to user with generic message, that error type can shutdown application
	 */
	public enum InvoiceXpressErrorType {
		ERROR,
		FATAL
	}
	
	private InvoiceXpressErrorType type;
	
	public InvoiceXpressError(String message, InvoiceXpressErrorType type) {
		this.message = message;
		this.type = type;
	}
	
	public InvoiceXpressError(Context context, int id, InvoiceXpressErrorType type) {
		this.message = context.getResources().getString(id);
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InvoiceXpressErrorType getType() {
		return type;
	}

	public void setType(InvoiceXpressErrorType type) {
		this.type = type;
	}
	
	public boolean isFatal() {
		return type == InvoiceXpressErrorType.FATAL;
	}

	public boolean isError() {
		return type == InvoiceXpressErrorType.ERROR;
	}	
	
}
