package pt.rupeal.invoicexpress.layouts;

import pt.rupeal.invoicexpress.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LinkLayout extends RelativeLayout {

	public LinkLayout(Context context) {
		super(context);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((Activity) getContext()).getLayoutInflater().inflate(R.layout.link, this);
	}
	
	public LinkLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setLabel(int resource){
		((TextView) findViewById(R.id.link_label)).setText(resource);
	}
	
	public void setLabel(String text){
		((TextView) findViewById(R.id.link_label)).setText(text);
	}
	
	public float getTextSize() {
		return ((TextView) findViewById(R.id.link_label)).getTextSize();
	}

}
