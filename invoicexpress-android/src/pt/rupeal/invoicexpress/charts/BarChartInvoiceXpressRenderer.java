package pt.rupeal.invoicexpress.charts;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.utils.StringUtil;
import android.content.Context;

public class BarChartInvoiceXpressRenderer extends XYMultipleSeriesRenderer {

	private static final long serialVersionUID = -8797680821311119577L;

	private float barWidth;
	private int legendBackgroundColor;

	public BarChartInvoiceXpressRenderer(Context context, int width, int height, double[] ...values) {
		setBarSpacing(0.5);
		
		setChartTitle(context.getResources().getString(R.string.dashboard_title) 
				+ " "
				+ InvoiceXpress.getInstance().getActiveAccountDetails().getCurrencySymbol());
		
		setXLabels(0);

		// Invoicing chart length will be 6 
		// Treasury chart length will be 7
		int yLabelsCounter = values[0].length;
        
		clearXTextLabels();
		addXTextLabel(0, "");

		// x min axis value, always 0
		setXAxisMin(0);
		
		// init and fill array to find the min and max values for Y axis
		double[] yAxisMinArray = initArrayYMinMax(yLabelsCounter);
		double[] yAxisMaxArray = initArrayYMinMax(yLabelsCounter);
		fillArrayYMinMax(yAxisMinArray, yAxisMaxArray, values);
		// get min e max for Y axis
		double yAxisMin = getMinFromArrayY(yAxisMinArray);
		double yAxisMax = getMaxFromArrayY(yAxisMaxArray);
		// set number of Y axis labels
		// setYLabels(yAxisMin == 0 ? yLabelsCounter + 1 : yLabelsCounter + 2);
		setYLabels(yLabelsCounter);
		
	    double[] labelParams = computeLabels(yAxisMin, yAxisMax, getYLabels());
	    
	    // set the Y min e max values after labels generation
	    // this allow us to have grid X after y values
	    // y max axis value
	    setYAxisMax(labelParams[1]);
	    // y min axis value
	    setYAxisMin(labelParams[0]);
		
		// get y label length and convert it to thousand or million or billion
		List<Double> yLabels = getLabels(labelParams);
		for (Double yLabel : yLabels) {
			addYTextLabel(yLabel, StringUtil.convertNumberToThounsandMillionBillion(yLabel, true));
		}
		
		// set label text size X & Y
		setLabelsTextSize(height / 50);
		setLabelsColor(context.getResources().getColor(R.color.dashboard_labels));
		
		setApplyBackgroundColor(true);
		setBackgroundColor(context.getResources().getColor(R.color.background));
		setMarginsColor(context.getResources().getColor(R.color.background));
		
		setShowAxes(false);
		
		// top, left, bottom right
		// this value should be calculated since the width and height values
		// my device has width 720 and height 1280, but with my action bar we have height 882
		// setMargins(new int[]{height / 8, width / 5, height / 12, width / 8});
		setMargins(new int[]{0, width / 5, height / 12, width / 8});
		
		// disable pan and zoom
		setPanEnabled(false, false);
		setZoomEnabled(false, false);
		
		setShowGridX(true);
		setGridColor(context.getResources().getColor(R.color.dashboard_line_horizontal));

		// show legend and properties (text size and background)
		setShowLegend(true);
		setLegendHeight(height / 16);
		setLegendBackgroundColor(context.getResources().getColor(R.color.dashboard_legend_background));
		
	}
	
	private static double[] initArrayYMinMax(int length) {
		double[] yAxisArray = new double[length];
		
		for (int i = 0; i < yAxisArray.length; i++) {
			yAxisArray[i] = 0;
		}
		
		return yAxisArray;
	}
	
	private static void fillArrayYMinMax(double[] yAxisMinArray, double[] yAxisMaxArray, double[] ...values) {
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				if(values[i][j] < 0) {
					yAxisMinArray[j] += values[i][j];
				}
				
				if(values[i][j] > 0) {
					yAxisMaxArray[j] += values[i][j];
				}
			}
		}
	}
	
	private static double getMinFromArrayY(double[] yAxisArray) {
		double yMin = 0;
		for (int i = 0; i < yAxisArray.length; i++) {
			yMin = Math.min(yMin, yAxisArray[i]);
		}
		return yMin;
	}
	
	private static double getMaxFromArrayY(double[] yAxisArray) {
		double yMax = 0;
		for (int i = 0; i < yAxisArray.length; i++) {
			yMax = Math.max(yMax, yAxisArray[i]);
		}
		return yMax;
	}		
	
	public float getBarWidth() {
		return barWidth;
	}
	
	public void setBarWidth(float barWidth) {
		this.barWidth = barWidth;
	}
	
	public int getLegendBackgroundColor() {
		return legendBackgroundColor;
	}

	public void setLegendBackgroundColor(int legendBackgroundColor) {
		this.legendBackgroundColor = legendBackgroundColor;
	}
	
	private static final NumberFormat FORMAT = NumberFormat.getNumberInstance();
	
	/**
	   * Computes a reasonable set of labels for a data interval and number of
	   * labels.
	   * 
	   * @param start start value
	   * @param end final value
	   * @param approxNumLabels desired number of labels
	   * @return collection containing {start value, end value, increment}
	   */
	  public static List<Double> getLabels(final double[] labelParams) {
		  
		  FORMAT.setMaximumFractionDigits(5);
		  List<Double> labels = new ArrayList<Double>();
		  // 	when the start > end the inc will be negative so it will still work
		  int numLabels = 1 + (int) ((labelParams[1] - labelParams[0]) / labelParams[2]);
		  // we want the range to be inclusive but we don't want to blow up when
		  // looping for the case where the min and max are the same. So we loop
		  // on
		  // numLabels not on the values.
		  for (int i = 0; i < numLabels; i++) {
			  double z = labelParams[0] + i * labelParams[2];
			  try {
				  // this way, we avoid a label value like 0.4000000000000000001 instead
				  // of 0.4
				  z = FORMAT.parse(FORMAT.format(z)).doubleValue();
			  } catch (ParseException e) {
				  // do nothing here
			  }
	      
			  labels.add(z);
		  }
		  
		  return labels;
	  }

	  /**
	   * Computes a reasonable number of labels for a data range.
	   * 
	   * @param start start value
	   * @param end final value
	   * @param approxNumLabels desired number of labels
	   * @return double[] array containing {start value, end value, increment}
	   */
	  private static double[] computeLabels(final double start, final double end, final int approxNumLabels) {
		  if (Math.abs(start - end) < 0.0000001f) {
			  return new double[] { start, start, 0 };
		  }
		  double s = start;
		  double e = end;
		  // calculate step between Y values
		  double yStep = roundUp(Math.abs(s - e) / approxNumLabels);
		  // calculate y start value
		  // the y start value has to be always minor then s
		  double yStart = 0;
		  if(s == 0) {
			  yStart = 0;
		  } else if(s < 0) {
			  while(yStart > s) {
				  yStart -= yStep;
			  }
		  } 
		  // calculate y end value
		  // the y end value has to be always greater then e
		  double yEnd = yStep * Math.round(e / yStep);
		  while(yEnd < e) {
			  yEnd += yStep;
		  }
		  // return start, end and step y values
		  return new double[] { yStart, yEnd, yStep };
	  }

	  /**
	   * Given a number, round up to the nearest power of ten times 1, 2, or 5. The
	   * argument must be strictly positive.
	   */
	  private static double roundUp(final double val) {
		  int exponent = (int) Math.floor(Math.log10(val));
		  double rval = val * Math.pow(10, -exponent);
		  if (rval > 5.0) {
			  rval = 10.0;
		  } else if (rval > 2.0) {
			  rval = 5.0;
		  } else if (rval > 1.0) {
			  rval = 2.0;
		  }
		  rval *= Math.pow(10, exponent);
		  return rval;
	  }	
	
}
