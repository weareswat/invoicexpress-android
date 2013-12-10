package pt.rupeal.invoicexpress.charts;

import org.achartengine.chart.AbstractChart;

import pt.rupeal.invoicexpress.server.InvoiceXpress;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class GraphicalView extends View {

	/** The chart to be drawn. */
	private AbstractChart chart;
	/** The view bounds. */
	private Rect rect = new Rect();
	/** The paint to be used when drawing the chart. */
	private Paint paint = new Paint();	
	
	public GraphicalView(Context context) {
		super(context);
	}
	
	public GraphicalView(Context context, AbstractChart chart) {
		super(context);
		this.chart = chart;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int widthScreen = InvoiceXpress.getInstance().getScreenWidth((Activity) getContext());
		
		canvas.getClipBounds(rect);
		int top = rect.top;
		
		int left = 0;
		int width = widthScreen;

		if(rect.left >= 0) {
			left = 0;
			width = widthScreen;
		} else if(rect.right <= widthScreen) {
			left = rect.left;
			width = widthScreen + Math.abs(rect.left);
		}
		
		int height = rect.height();
		
		chart.draw(canvas, left, top, width, height, paint);
	}	
	
}
