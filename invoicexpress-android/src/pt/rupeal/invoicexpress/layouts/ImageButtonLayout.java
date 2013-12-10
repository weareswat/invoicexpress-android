package pt.rupeal.invoicexpress.layouts;

import java.util.Locale;

import pt.rupeal.invoicexpress.R;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageButtonLayout extends LinearLayout {

	public ImageButtonLayout(Context context) {
		super(context);
	}	
	
	public ImageButtonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		((Activity) getContext()).getLayoutInflater().inflate(R.layout.image_button, this);
	}
	
	public void setImage(int resource) {
		((ImageView) findViewById(R.id.button_image)).setImageResource(resource);
	}
	
	public void setLabel(int resource) {
		setLabel(resource, false);
	}
	
	public void setLabel(int resource, boolean upperCase) {
		if(upperCase) {
			((TextView) findViewById(R.id.button_label)).setText(
					getContext().getResources().getString(resource).toUpperCase(Locale.getDefault()));
		} else {
			((TextView) findViewById(R.id.button_label)).setText(resource);
		}
	}
	
	public void setGravity(int gravity) {
		((LinearLayout.LayoutParams)((TextView) findViewById(R.id.button_label)).getLayoutParams()).gravity = gravity;
		((TextView) findViewById(R.id.button_label)).setGravity(gravity);
	}
	
	public void setBackground() {
		findViewById(R.id.button_image_layout).setBackgroundResource(R.color.documents_button_image_background);
		findViewById(R.id.button_label_layout).setBackgroundResource(R.color.documents_button_label_background);
	}	
	
	public void setPressedBackground() {
		findViewById(R.id.button_image_layout).setBackgroundResource(R.color.documents_button_image_background_pressed);
		findViewById(R.id.button_label_layout).setBackgroundResource(R.color.documents_button_label_background_pressed);
	}
	
}
