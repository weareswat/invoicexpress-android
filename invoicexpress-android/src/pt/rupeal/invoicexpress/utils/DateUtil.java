package pt.rupeal.invoicexpress.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateUtil {

	/**
	 * Format date read from server response.
	 * 
	 * @param date
	 * @return formatted date
	 */
	public static Date formatDate(String date){
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());  
			return (Date) formatter.parse(date);
		} catch (ParseException e) {
			Log.e(DateUtil.class.getCanonicalName(), e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Check if today is after then parameter date.
	 * 
	 * @param date
	 * @return true if today is greater then parameter date
	 */
	public static boolean isAfter(Date date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
			long time = dateFormat.parse(dateFormat.format(new Date())).getTime();
			Date today = new Date(time);
			if(today.after(date)) {
				return true;
			}
		} catch (ParseException e) {
			Log.e(DateUtil.class.getCanonicalName(), e.getMessage(), e);
		}
	    
	    return false;
	}
	
}
