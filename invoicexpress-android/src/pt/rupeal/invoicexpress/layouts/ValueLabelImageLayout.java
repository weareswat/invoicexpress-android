package pt.rupeal.invoicexpress.layouts;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ValueLabelImageLayout extends LinearLayout {

	public ValueLabelImageLayout(Context context) {
		super(context);
	}
	
	public ValueLabelImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((Activity)getContext()).getLayoutInflater().inflate(R.layout.details_value_label_image_row, this);
	}
	
	public void setText(Context context, String text) {
		// resized text
		String textResized = StringUtil.resizeString((Activity) context, 
				text,
				getTextSize(),
				getMarginsToResizeDetailsValue(context));
		// set text
		((TextView) findViewById(R.id.details_value)).setText(textResized);
	}
	
	public void setLabel(int resource) {
		((TextView) findViewById(R.id.details_label)).setText(resource);
	}
	
	public void setImage(int resource) {
		((ImageView) findViewById(R.id.details_image)).setImageResource(resource);
	}
	
	private float getTextSize() {
		return ((TextView) findViewById(R.id.details_value)).getTextSize();
	}
	
	private int getMarginsToResizeDetailsValue(Context context) {
		// margin layout
		RelativeLayout layout = ((RelativeLayout) findViewById(R.id.details_value_label_image_layout));
		int marginLeftRightSum = ((ViewGroup.MarginLayoutParams) layout.getLayoutParams()).leftMargin; 
		marginLeftRightSum += ((ViewGroup.MarginLayoutParams) layout.getLayoutParams()).rightMargin;
		// margin text view value
		marginLeftRightSum += ((ViewGroup.MarginLayoutParams) ((TextView) findViewById(R.id.details_value)).getLayoutParams()).leftMargin;
		marginLeftRightSum += ((ViewGroup.MarginLayoutParams) ((TextView) findViewById(R.id.details_value)).getLayoutParams()).rightMargin;
		// margin right between image and horizontal separate line
		marginLeftRightSum += ((ViewGroup.MarginLayoutParams) (findViewById(R.id.details_horizontal_separator).getLayoutParams())).rightMargin;
		// missed image width
		marginLeftRightSum += ScreenLayoutUtil.convertDpToPixels(context, 50);
		// return
		return marginLeftRightSum;
	}

}
