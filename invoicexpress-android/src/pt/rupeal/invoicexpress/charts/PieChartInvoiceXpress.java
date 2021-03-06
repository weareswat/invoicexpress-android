package pt.rupeal.invoicexpress.charts;

import java.util.List;

import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;
import pt.rupeal.invoicexpress.utils.StringUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class PieChartInvoiceXpress extends PieChart {

	private static final long serialVersionUID = 5873346043462525266L;

	private Context context;
	private List<String> clients;
	
	public PieChartInvoiceXpress(Context context, CategorySeries dataset, List<String> clients, DefaultRenderer renderer) {
		super(dataset, renderer);
		this.context = context;
		this.clients = clients;
	}

	@Override
	public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
		
		Fragment progressBar = ((Activity) context).getFragmentManager().findFragmentByTag(FragmentTagsEnum.DIALOG_PROGRESS.getValue());
		if(progressBar!= null && progressBar.isVisible()) {
			return;
		}
		
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		
	    paint.setTextSize(mRenderer.getLabelsTextSize());
	    int legendSize = getLegendSize(mRenderer, height / 5, 0);
	    int left = x;
	    int top = y;
	    int right = x + width;
	    int sLength = mDataset.getItemCount();
	    
	    double total = 0;
	    double values[] = new double[sLength];
	    // titles should be removed
	    String[] titles = new String[sLength];
	    String[] contactsArray = new String[sLength];
	    for (int i = 0; i < sLength; i++) {
	    	total += mDataset.getValue(i);
	    	values[i] = mDataset.getValue(i);
	    	titles[i] = mDataset.getCategory(i);
	    	contactsArray[i] = clients.get(i);
	    }
	    
	    int bottom = y + height - legendSize;
	    drawBackground(mRenderer, canvas, x, y, width, height, paint, false, mRenderer.getBackgroundColor());
	    
	    // draw Chart Title
	    float heightChartTitle = y + ScreenLayoutUtil.convertDpToPixels(context, 30);	    
	    drawChartTitle(canvas, x + width / 2, heightChartTitle, paint);
	    
	    // set new top value based on the height Chart Title
	    top = Math.round(heightChartTitle + ScreenLayoutUtil.convertDpToPixels(context, 30));

	    // draw legend
	    int legendHeight = drawLegend(canvas, mRenderer, values, contactsArray, left, right, y, width, height, legendSize, paint);
	    
	    mCenterX = (left + right) / 2;
    	mCenterY = (bottom + top - legendHeight + (int) heightChartTitle) / 2;

    	float currentAngle = 0;
    	int mRadius = Math.min(width, bottom + top - legendHeight + (int) heightChartTitle);
    	int radius = mRadius / 4;
		RectF oval = new RectF(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY + radius);
		// get border paint
		Paint borderPaint = getBorderPaint();
		// draw circle
		for (int i = 0; i < sLength; i++) {
			paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
			float value = (float) mDataset.getValue(i);
			float angle = (float) (value / total * 360);
			// draw fill arc
			canvas.drawArc(oval, currentAngle, angle, true, paint);
			// draw border arc
			canvas.drawArc(oval, currentAngle, angle, false, borderPaint);
			
			currentAngle += angle;
		}
		
		// is a sample
    	if(clients.size() == 1 && TopDebtorsChart.NO_CLIENT_SAMPLE.equals(clients.get(0))) {
    		Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.v);
    		b = Bitmap.createScaledBitmap(b, 
    				Math.round(ScreenLayoutUtil.convertDpToPixels(context, 65)), 
    				Math.round(ScreenLayoutUtil.convertDpToPixels(context, 45)), 
    				true);
    		canvas.drawBitmap(b, mCenterX - radius / 2, mCenterY - radius / 3, paint);
    	}		
		
	}
	
	private void drawChartTitle(Canvas canvas, float x, float y, Paint paint) {
		paint.setTextAlign(Align.CENTER);
		paint.setColor(mRenderer.getLabelsColor());
		paint.setTextSize(ScreenLayoutUtil.convertSpToPixels(context, 12));
		canvas.drawText(mRenderer.getChartTitle(), x, y, paint);
	}
	
	private static final int[] RANKING_IMAGES = {R.drawable.dashboard_1, R.drawable.dashboard_2, R.drawable.dashboard_3, R.drawable.dashboard_4, R.drawable.dashboard_5}; 
	
	private static final int TOP_COUNTER = RANKING_IMAGES.length;
	
	private static final String NOT_AVAILABLE = "N/A";
	
	private int drawLegend(Canvas canvas, DefaultRenderer renderer, double[] values, String[] clients, int left,
			int right, int y, int width, int height, int legendSize, Paint paint) {
		
		// get bitmap
		Bitmap bitmapRankingNumber = BitmapFactory.decodeResource(context.getResources(), RANKING_IMAGES[0]);
		// calculate legend height
		float legendHeight = (bitmapRankingNumber.getHeight() / 2) * (3 * TOP_COUNTER + 1);
		
		// draw top black top line
		paint.setColor(context.getResources().getColor(R.color.line_seperator_top_black));
		canvas.drawLine(left, height - legendHeight - 2, width, height - legendHeight - 3, paint);
		// draw top black bottom line	    	
		paint.setColor(context.getResources().getColor(R.color.line_seperator_bottom_black));
		canvas.drawLine(left, height - legendHeight, width, height - legendHeight - 1, paint);
		
		// draw rectangle legend
		paint.setColor(context.getResources().getColor(R.color.dashboard_legend_background));
    	canvas.drawRect(left, height, width, height - legendHeight, paint);
		
    	// is a sample
    	if(clients.length == 1 
    			&& TopDebtorsChart.NO_CLIENT_SAMPLE.equals(clients[0])) {
    		
    		paint.setTextSize(ScreenLayoutUtil.convertSpToPixels(context, 20));
    		paint.setColor(context.getResources().getColor(R.color.dashboard_labels));
    		canvas.drawText(context.getResources().getString(R.string.dashboard_topdebtors_legend_tilte_1), 
    				width / 2, height - ((legendHeight + 30) / 2), paint);

    		paint.setTextSize(ScreenLayoutUtil.convertSpToPixels(context, 10));
    		canvas.drawText(context.getResources().getString(R.string.dashboard_topdebtors_legend_tilte_2), 
    				width / 2, height - ((legendHeight - 35) / 2), paint);
    		return Math.round(legendHeight);
    	}
    	
    	// paint.setTextSize(ScreenLayoutUtil.convertSpToPixels(context, 12));
    	
    	paint.setTextSize(ScreenLayoutUtil.isLowerThanHdpi(context) ? 8 : ScreenLayoutUtil.convertSpToPixels(context, 12));
    	
    	float xPointLegend = ScreenLayoutUtil.convertDpToPixels(context, 18);
    	float topLegend = height - legendHeight + (bitmapRankingNumber.getHeight() / 2);
    	for (int i = 0; i < clients.length; i++) {
			// set bitmap
    		bitmapRankingNumber = BitmapFactory.decodeResource(context.getResources(), RANKING_IMAGES[i]);
    		canvas.drawBitmap(bitmapRankingNumber, xPointLegend, topLegend, paint);
    		// set white color
    		paint.setColor(Color.WHITE);
    		// calculate x point
    		float xPointBalance = width - ScreenLayoutUtil.convertDpToPixels(context, 20);
    		// set right align
    		paint.setTextAlign(Align.RIGHT);
    		String balance = StringUtil.convertToTopDebtorValue(values[i]);
    		canvas.drawText(balance, 
    				xPointBalance, 
    				topLegend + (3 * bitmapRankingNumber.getHeight()) / 4, 
    				paint);
    		
    		paint.setTextAlign(Align.LEFT);
    		float xPointLegendContactName = xPointLegend + bitmapRankingNumber.getWidth() + ScreenLayoutUtil.convertDpToPixels(context, 8);
    		// set client name
    		// calculate margin
    		int margin = Math.round(xPointLegendContactName);
    		margin += paint.measureText(balance);
    		margin += ScreenLayoutUtil.convertDpToPixels(context, 80);
    		String clientName = StringUtil.resizeString((MainActivity) context, 
    				clients[i], 
    				paint.getTextSize(),
    				margin);
    		canvas.drawText(clientName, xPointLegendContactName, topLegend + (3 * bitmapRankingNumber.getHeight()) / 4, paint);    		
    		
    		topLegend += (3 * bitmapRankingNumber.getHeight()) / 2;
		}
    	
    	for (int i = clients.length; i < TOP_COUNTER; i++) {
			// set image
    		bitmapRankingNumber = BitmapFactory.decodeResource(context.getResources(), RANKING_IMAGES[i]);
    		canvas.drawBitmap(bitmapRankingNumber, xPointLegend, topLegend, paint);
    		// set white color
    		paint.setColor(Color.WHITE);
    		// set name
    		paint.setTextAlign(Align.LEFT);
    		float leftName = xPointLegend + bitmapRankingNumber.getWidth() + (width / 20);
    		canvas.drawText(NOT_AVAILABLE, leftName, topLegend + (3 * bitmapRankingNumber.getHeight()) / 4, paint);
    		// set value
    		paint.setTextAlign(Align.RIGHT);
    		float leftBalance = (9 * width) / 10;
    		canvas.drawText(NOT_AVAILABLE, leftBalance, topLegend + (3 * bitmapRankingNumber.getHeight()) / 4, paint);
    		
    		topLegend += (3 * bitmapRankingNumber.getHeight()) / 2;
		}    	
		
		return Math.round(legendHeight);
	}
	
	/**
	 * Generate the border paint
	 * 
	 * @return
	 */
	private Paint getBorderPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(context.getResources().getColor(R.color.dashboard_debtor_border));
		paint.setStrokeWidth(ScreenLayoutUtil.convertDpToPixels(context, 6));
		
		return paint;
	}

}
