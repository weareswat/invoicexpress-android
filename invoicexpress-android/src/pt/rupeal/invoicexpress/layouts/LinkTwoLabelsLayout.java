package pt.rupeal.invoicexpress.layouts;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LinkTwoLabelsLayout {

	private RelativeLayout layout;
	
	public LinkTwoLabelsLayout(Context context, ViewGroup parent) {
		layout = (RelativeLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.link_two_labels, parent, false);
	}	
	
	public RelativeLayout getLinkView(){
		return layout;
	}
	
	public void setLabel(Context context, String text, int margin){
		TextView label = ((TextView) layout.findViewById(R.id.link_label));
		margin += ((ViewGroup.MarginLayoutParams) label.getLayoutParams()).leftMargin;
		margin += ((ViewGroup.MarginLayoutParams) label.getLayoutParams()).rightMargin;
		
		View verticalLine = layout.findViewById(R.id.link_vertical_line);
		margin += ((ViewGroup.MarginLayoutParams) verticalLine.getLayoutParams()).rightMargin;
		
		TextView value = ((TextView) layout.findViewById(R.id.link_value));
		Paint paint = new Paint();
		paint.setTextSize(value.getTextSize());
		margin += paint.measureText(value.getText().toString());
		margin += ((ViewGroup.MarginLayoutParams) value.getLayoutParams()).leftMargin;
		margin += ((ViewGroup.MarginLayoutParams) value.getLayoutParams()).rightMargin;
		margin += ScreenLayoutUtil.convertDpToPixels(context, 20);
		
		text = StringUtil.resizeString((Activity) context, text, label.getTextSize(), margin);
		label.setText(text);
	}	

	public void setValue(String text){
		((TextView) layout.findViewById(R.id.link_value)).setText(text);
	}
	
	public int getMarginRight(Context context) {
		View verticalLine = layout.findViewById(R.id.link_vertical_line);
		int margin = ((ViewGroup.MarginLayoutParams) verticalLine.getLayoutParams()).rightMargin;
		margin += 2 * ScreenLayoutUtil.convertDpToPixels(context, 18);
		
		return margin;
	}
	
}
