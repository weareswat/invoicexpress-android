package pt.rupeal.invoicexpress.layouts;

import pt.rupeal.invoicexpress.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubTitleLayout extends LinearLayout {

	public SubTitleLayout(Context context) {
		super(context);
	}
	
	public SubTitleLayout(Context context, AttributeSet attr) {
		super(context, attr);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((Activity) getContext()).getLayoutInflater().inflate(R.layout.details_sub_title, this);
	}
	
	public void setTextToTextViewLeft(int resource) {
		((TextView) findViewById(R.id.sub_title_text_view_1)).setText(resource);
	}

	public void setTextToTextViewRight(int resource, int marginRight) {
		TextView textView = ((TextView) findViewById(R.id.sub_title_text_view_2));
		((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).rightMargin += marginRight;
		textView.setText(resource);
	}		
	
	public void setMarginTop(int top){
		ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.getLayoutParams();
	    layoutParams.setMargins(0, top, 0, 0);
	    this.setLayoutParams(layoutParams);
	}
	
}
