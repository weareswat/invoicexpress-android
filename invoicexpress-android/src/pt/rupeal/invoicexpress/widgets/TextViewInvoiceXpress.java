package pt.rupeal.invoicexpress.widgets;

import pt.rupeal.invoicexpress.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

public class TextViewInvoiceXpress extends android.widget.TextView {

	public static final String RESIZE = "resize";
	public static final String BREAK = "break";
	public static final String DO_NOTHING = "do nothing";
	
	public TextViewInvoiceXpress(Context context) {
		super(context);
	}
	
	public TextViewInvoiceXpress(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TextViewInvoiceXpress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setText(Context context, String text, int margin, String option) {
		if(RESIZE.equals(option)) {
			text = StringUtil.resizeString((Activity) context, text, getTextSize(), margin);
		} else if(BREAK.equals(option)) {
			text = StringUtil.convertToBreakedString((Activity) context, text, getTextSize(), margin);
		}
		
		super.setText(text, BufferType.NORMAL);
	}

}
