package pt.rupeal.invoicexpress.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.util.MathHelper;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.utils.ScreenLayoutUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;

public class BarChartInvoiceXpress extends BarChart {

	private static final long serialVersionUID = 4326070658495433442L;

	private Context context;
	
	public BarChartInvoiceXpress(Context context, XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
		super(dataset, renderer, type);
		this.context = context;
	}

	@Override
	public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
		// disable draw when progress bar is visible
		Fragment progressBar = ((Activity) context).getFragmentManager().findFragmentByTag(FragmentTagsEnum.DIALOG_PROGRESS.getValue());
		if(progressBar!= null && progressBar.isVisible()) {
			return;
		}
		
		paint.setAntiAlias(true);
	    
		// design the main XY chart rect
		int legendSize = getLegendSize(mRenderer, height / 5, mRenderer.getAxisTitleTextSize());
		
		// calculate left, top, right and bottom chart limits
	    int[] margins = mRenderer.getMargins();
	    int left = x + margins[1];
	    int top = y + margins[0];
	    int right = x + width - margins[3];
	    int bottom = y + height - margins[2] - legendSize;

	    // set rect screen
	    if (getScreenR() == null) {
	    	setScreenR(new Rect());
	    }
	    
	    // rectangle for XY chart
	    // getScreenR().set(left, top, right, bottom);
	    getScreenR().set(left, top, right, bottom);
	    drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

	    if (paint.getTypeface() == null
	        || !paint.getTypeface().toString().equals(mRenderer.getTextTypefaceName())
	        || paint.getTypeface().getStyle() != mRenderer.getTextTypefaceStyle()) {
	    	
	    		paint.setTypeface(Typeface.create(mRenderer.getTextTypefaceName(), mRenderer.getTextTypefaceStyle()));
	    }
	    
	    // draw stuff over the margins such as data doesn't render on these areas
	    drawBackground(mRenderer, canvas, x, bottom, width, height - bottom, paint, true, mRenderer.getMarginsColor());
	    drawBackground(mRenderer, canvas, x, y, width, margins[0], paint, true, mRenderer.getMarginsColor());
	    
	    drawBackground(mRenderer, canvas, x, y, left - x, height - y, paint, true, mRenderer.getMarginsColor());
	    drawBackground(mRenderer, canvas, right, y, margins[3], height - y, paint, true, mRenderer.getMarginsColor());
	    
	    // draw Chart Title
	    float heightChartTitle = y + ScreenLayoutUtil.convertDpToPixels(context, 30);
	    drawChartTitle(canvas, x + width / 2, heightChartTitle, paint);
	    
	    // set new top value based on the height Chart Title
	    top = (int) (heightChartTitle + ScreenLayoutUtil.convertDpToPixels(context, 30));
	    
	    // X and Y values
	    double minX = mRenderer.getXAxisMin();
	    double maxX = mRenderer.getXAxisMax();
	    double minY = mRenderer.getYAxisMin();
	    double maxY = mRenderer.getYAxisMax();
	    
	    // y height pixels
	    float yPixelsPerUnit = (float) ((bottom - top) / (maxY - minY));
	    // x width pixels
	    float xPixelsPerUnit = (float) ((right - left) / (maxX - minX));
	    
	    // the bottom dosen't work for negative values
	    // we have to create first line y attribute
    	// drawYTextLabels
	    float gridXfirstLineY = drawYLabels(canvas, left, right, bottom, minY, yPixelsPerUnit, paint);
	    
	    // save the base line y
	    // for the first series the array just only has gridXfirstLineY value
	    // the map saves the last top y value used by last bar
	    Map<Double, Float> baseLineY = new HashMap<Double, Float>();
	    for (double key = 1; key <= maxX; key++) {
	    	baseLineY.put(Double.valueOf(key), Float.valueOf(gridXfirstLineY));
		}
	    
	    // draw series
	    SortedSet<Double> xBarPoints = new TreeSet<Double>();
	    
	    int sLength = mDataset.getSeriesCount();
	    for (int i = 0; i < sLength; i++) {
	    	XYSeries series = mDataset.getSeriesAt(i);

	    	if (series.getItemCount() == 0) {
	    		continue;
	    	}

	    	SimpleSeriesRenderer seriesRenderer = mRenderer.getSeriesRendererAt(i);

	    	// points X and Y bar
	    	List<Float> points = new ArrayList<Float>(2);

	    	// range, key X value (1, 2, 3, ..) and value Y value
	    	SortedMap<Double, Double> range = series.getRange(minX, maxX, 1);
	    	
	    	// draw series bar, initiate x point
	    	double pointX = left;

	    	for (Entry<Double, Double> value : range.entrySet()) {
	    		
	    		// point X
	    		points.add((float) pointX);
	    		xBarPoints.add(pointX);
	    		// set the next x point
	    		pointX = pointX + xPixelsPerUnit;
	    		
	    		// point Y
	    		double yValue = value.getValue();
	    		// draw series if yValue != 0
	    		if(yValue != 0) {
	    			// get last point y
	    			float yMinValue = baseLineY.get(value.getKey());
	    			// calculate new last point y if the y Value is positive and the last one is negative or vice versa
	    			if((yMinValue > gridXfirstLineY && yValue > 0)
	    					|| (yMinValue < gridXfirstLineY && yValue < 0)) {
	    				yMinValue = gridXfirstLineY;
	    			}
	    			// calculate point y based on bottom value
	    			float pointY = (float) (bottom - yPixelsPerUnit * (yValue - minY));
	    			// the new point y is the last calculated point y and the difference between min y value and the y line 0
	    			pointY += yMinValue - gridXfirstLineY;
	    			points.add(pointY);
	    			// draw series
	    			drawSeries(canvas, paint, MathHelper.getFloats(points), seriesRenderer, yMinValue, xPixelsPerUnit);
	    			baseLineY.put(value.getKey(), Float.valueOf(pointY));
	    		}
    			
    			points.clear();
	    	}
	    
	    }
	    
	    // draw 0 grid X line
	    // draw over then chart bars
    	paint.setColor(mRenderer.getGridColor());
    	canvas.drawRect(left, gridXfirstLineY - 1, right, gridXfirstLineY + 1, paint);
	    
	    // draw X Labels
    	drawXTextLabels(canvas, paint, xBarPoints, left, right, top, bottom, xPixelsPerUnit);
	    // draw legend
	    drawLegend(canvas, mRenderer, 0, right, y, width, height, legendSize, paint);
	}
	
	private void drawChartTitle(Canvas canvas, float x, float y, Paint paint) {
	    paint.setTextAlign(Align.CENTER);
	    paint.setColor(mRenderer.getLabelsColor());
	    paint.setTextSize(ScreenLayoutUtil.convertSpToPixels(context, 12));
	    canvas.drawText(mRenderer.getChartTitle(), x, y, paint);
	}
	
	private float drawYLabels(Canvas canvas, int left, int right, int bottom, double minY, float yPixelsPerUnit, Paint paint) {
		float gridXfirstLineY = 0;
		
		Double[] yTextLabelLocations = mRenderer.getYTextLabelLocations();
		for (Double location : yTextLabelLocations) {
			float yLabel = (float) (bottom - yPixelsPerUnit * (location.doubleValue() - minY));
			String label = location == 0 ? "0" : mRenderer.getYTextLabel(location);
			
			paint.setTextSize(mRenderer.getLabelsTextSize());
			paint.setTextAlign(Align.RIGHT);
			paint.setColor(mRenderer.getLabelsColor());
			// draw Y text Label
			int xLabel = left - (int) mRenderer.getLabelsTextSize();
			canvas.drawText(label, xLabel, yLabel + (int) mRenderer.getLabelsTextSize() / 3, paint);

			// draw grid X
			paint.setColor(mRenderer.getGridColor());
			int heightLineX = ScreenLayoutUtil.isLowerThanHdpi(context) ? 1 : 2;
			canvas.drawRect(left, yLabel - heightLineX, right, yLabel + heightLineX, paint);

			// the gridXfirstLineY var is used to draw the grid x line after then all series bar
			if(location == 0) {
				gridXfirstLineY = yLabel;
			}
		}
		
		return gridXfirstLineY;
	}
	
	private void drawSeries(Canvas canvas, Paint paint, float[] points, SimpleSeriesRenderer seriesRenderer, float yAxisValue, float xPixelsPerUnit) {
		// calculate margins
    	float marginLeft = xPixelsPerUnit * 0.1f;
    	float marginRight = xPixelsPerUnit * 0.1f;
    	
	    int length = points.length;
	    
	    paint.setColor(seriesRenderer.getColor());
	    paint.setStyle(Style.FILL);
	    for (int i = 0; i < length; i += 2) {
	    	float x = points[i];
	    	float y = points[i + 1];
	    	drawBar(canvas, x + marginLeft, yAxisValue, x + (xPixelsPerUnit - marginRight), y, paint);
	    }
	}	

	private void drawBar(Canvas canvas, float xMin, float yMin, float xMax, float yMax, Paint paint) {

		if (Math.abs(yMin - yMax) < 1) {
			if (yMin < yMax) {
				yMax = yMin + 1;
	        } else {
	        	yMax = yMin - 1;
	        }
		}
		
		int top = Math.round(yMax);
		int bottom = Math.round(yMin);
		if(top > bottom) {
			int aux = top;
			top = bottom;
			bottom = aux;
		}

		canvas.drawRect(Math.round(xMin), top, Math.round(xMax), bottom, paint);
	}	  
	
	private void drawXTextLabels(Canvas canvas, Paint paint, SortedSet<Double> xBarPoints, int left, int right, int top, int bottom, float xPixelsPerUnit) {
		Double[] xPoints = new Double[xBarPoints.size()];
		xBarPoints.toArray(xPoints);
		
        paint.setTextSize(mRenderer.getLabelsTextSize());
        paint.setTextAlign(mRenderer.getXLabelsAlign());
    	paint.setColor(mRenderer.getLabelsColor());
    	
    	for (int i = 1; i <= xBarPoints.size(); i++) {
        	canvas.drawText(mRenderer.getXTextLabel(Double.valueOf(i)), 
        			Math.round(xPoints[i-1]) + (xPixelsPerUnit / 2), 
        			bottom + mRenderer.getLabelsTextSize() + ScreenLayoutUtil.convertDpToPixels(context, 5), 
        			paint);
    	}
	}
	
	private void drawLegend(Canvas canvas, DefaultRenderer renderer, int left, int right, int y, int width, int height, int legendSize, Paint paint) {

    	// draw top line
    	paint.setColor(context.getResources().getColor(R.color.line_seperator_top_black));
    	canvas.drawLine(left, height - legendSize - 2, width, height - legendSize - 3, paint);
    	// draw bottom line	    	
    	paint.setColor(context.getResources().getColor(R.color.line_seperator_bottom_black));
    	canvas.drawLine(left, height - legendSize, width, height - legendSize - 1, paint);
    	
    	// continue..
    	paint.setColor(((BarChartInvoiceXpressRenderer) renderer).getLegendBackgroundColor());
    	// draw legend rectangle - height calculated by legendSize
    	canvas.drawRect(left, height, width, height - legendSize, paint);
    	
		paint.setTextAlign(Align.CENTER);
		float textSize = ScreenLayoutUtil.convertSpToPixels(context, 12);
		paint.setTextSize(textSize);
		
		// get series count
		int numberOfSeries = mDataset.getSeriesCount();
		// divide legends
		int widthForEachLegend = width / numberOfSeries;
		// calculate y value for legend data
		float yCircleLegend = (height - legendSize) + (legendSize / 2) + ScreenLayoutUtil.convertDpToPixels(context, 0);
		float yLabelLegend = (height - legendSize) + (legendSize / 2) + ScreenLayoutUtil.convertDpToPixels(context, 4);
		// set radius
		float radius = ScreenLayoutUtil.convertDpToPixels(context, 3);

		float xLabelLegend;
		float xCircleLegend;
		for (int i = 0; i < numberOfSeries; i++) {
			
			paint.setColor(renderer.getLabelsColor());
			xLabelLegend = (widthForEachLegend / 2) * ((i * 2) + 1);
			String labelLegend = mDataset.getSeriesAt(i).getTitle();
			canvas.drawText(labelLegend, xLabelLegend, yLabelLegend, paint);
			float labelWidth = paint.measureText(labelLegend, 0, labelLegend.length());
			
			paint.setColor(renderer.getSeriesRendererAt(i).getColor());
			xCircleLegend = xLabelLegend - ScreenLayoutUtil.convertDpToPixels(context, 8) - (labelWidth / 2);
			canvas.drawCircle(xCircleLegend, yCircleLegend, radius, paint);
		}
		
	}
	
}
