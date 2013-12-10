package pt.rupeal.invoicexpress.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;

public class StringUtil {

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
	 * Validate email address
	 * 
	 * @param email
	 * @return true if the email is valid
	 */
	public static boolean isValidEmailAddress(String email) {
		return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
	}

	public static String resizeString(Activity activity, String text, float textSize, int margin) {
		
		int freeSpace = InvoiceXpress.getInstance().getScreenWidth(activity) - margin;
		
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		float stringSpace = paint.measureText(text);
		
		int length = text.length();
		
		if(freeSpace < stringSpace) {
			
			while(freeSpace < stringSpace) {
				stringSpace = paint.measureText(text, 0, length) + paint.measureText("...");
				length--;
			}
			
			String stringResized = text.substring(0, length);
			
			if(stringResized.charAt(stringResized.length() - 1) == ' ') {
				return stringResized.substring(0, stringResized.length() - 1) + "...";				
			} else {
				return stringResized + "...";
			}
			
		}

		return text;
	}
	
	public static String resizeString(Activity activity, String text, float textSize, float freeSpace) {
		
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		float stringSpace = paint.measureText(text);
		
		int length = text.length();
		
		if(freeSpace < stringSpace) {
			
			while(freeSpace < stringSpace) {
				stringSpace = paint.measureText(text, 0, length) + paint.measureText("...");
				length--;
			}
			
			return text.substring(0, length) + "...";
		}

		return text;
	}	
	
	public static String convertToBreakedString(Activity activity, String text, float textSize, int margin) {
		
		int freeSpaceForString = InvoiceXpress.getInstance().getScreenWidth(activity) - margin;
		
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		float stringSpace = paint.measureText(text);
		
		if(stringSpace < freeSpaceForString) {
			return text;
		}
		
		String[] textSplited = text.split(" ");
		StringBuffer textConverted = new StringBuffer();
		StringBuffer textConvertedLine = new StringBuffer();
		int length = textSplited.length;
		int index = 0;
		int countLines = 0;
		final int countMaxLines = 3;
		while(index < length && countLines < countMaxLines) {
			// get string to add
			String stringWillAdded = textSplited[index] + " ";
			// calculate the pixel length of converted line text and the new string to add
			stringSpace = paint.measureText(stringWillAdded) + paint.measureText(textConvertedLine.toString());
			// if there is no space to add string add text converted line to converted text
			if(freeSpaceForString < stringSpace) {
				textConverted.append(textConvertedLine + "\n");
				textConvertedLine = new StringBuffer();
				countLines++;
			} else {
				// if the string legth is minor then freespacing length the application add the string to add
			}
			
			textConvertedLine.append(stringWillAdded);
			
			index++;
		}
		
		if(textConvertedLine.length() > 0) {
			textConverted.append(textConvertedLine + "\n");
		}
		
		return textConverted.toString().substring(0, textConverted.length() - 2) + (countLines == countMaxLines ? "..." : "");
	}	
	
	public static String convertNumberToThounsandMillionBillion(long value) {
		return convertNumberToThounsandMillionBillion(Long.valueOf(value).doubleValue(), false);
	}
	
	public static String convertNumberToThounsandMillionBillion(double value) {
		return convertNumberToThounsandMillionBillion(value, false);
	}	
	
	/**
	 * Convert the given double value to thousand, million or billion number.
	 * For example, the given double value 1234.5 will be converted to 1.23K.
	 * 
	 * @param value
	 * @param isChart
	 * @return the converted double value parameter
	 */
	@SuppressLint("DefaultLocale")
	public static String convertNumberToThounsandMillionBillion(double value, boolean isChart) {
		if (value > -1000 && value < 1000) {
			return isChart ? String.format("%.0f", value) : String.format("%.2f", value);
		}
		
//		boolean isNegativeValue = value < 0;
		
//	    int exp = isNegativeValue ? (int) (Math.log(value*-1) / Math.log(1000)) : (int) (Math.log(value) / Math.log(1000));
//	    String converted = isChart ? String.format("%.1f %c", value / Math.pow(1000, exp), "kmbt".charAt(exp-1)) : 
//	    	String.format("%.2f %c", value / Math.pow(1000, exp), "kmbt".charAt(exp-1));
	    
	    double valueTemp = value / 1000;
	    if(isChart) {
	    	return String.format("%.1f %c", valueTemp, 'k');
	    }
	    
	    // String.format("%.2f %c", valueTemp, 'k');
	    
	    return convertToMoneyValue(valueTemp, "k");
	}
	
	public static final String ZERO = "0,00";
	public static final String NOT_APPLICABLE = "N/A";
	
	public static String convertToQuarterlyValue(String value) {
		if(value.isEmpty()) {
			return NOT_APPLICABLE;
		}
		
		double doubleValue = Double.parseDouble(value);
		double fractionalPart = doubleValue % 1;
		double integralPart = doubleValue - fractionalPart;
		if(fractionalPart == 0) {
			return StringUtil.convertNumberToThounsandMillionBillion(Math.round(integralPart));
		} else {
			return StringUtil.convertNumberToThounsandMillionBillion(doubleValue);
		}
	}
	
	public static String convertToTopDebtorValue(double value) {
		double fractionalPart = value % 1;
		double integralPart = value - fractionalPart;
		if(fractionalPart == 0) {
			return StringUtil.convertNumberToThounsandMillionBillion(Math.round(integralPart));
		} else {
			return StringUtil.convertNumberToThounsandMillionBillion(value);
		}
	}
	
	/**
	 * Convert given double value as 1234.56 to 1.234,56 <currency>.
	 * 
	 * @param value
	 * @return the converted double value
	 */
	public static String convertToMoneyValue(double value) {
		return convertToMoneyValue(value, InvoiceXpress.getInstance().getActiveAccountDetails().getCurrencySymbol());
	}
	
	public static String convertToMoneyValue(double value, String kValue) {
		Locale locale = new Locale("pt", "PT");
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
		decimalFormatSymbols.setGroupingSeparator('.');
		DecimalFormat formatter = new DecimalFormat("#,##0.00", decimalFormatSymbols);
		return formatter.format(value) + " " + kValue;
	}		
	
	private static final List<String> NAME_PROPOSITIONS = new ArrayList<String>() {
		
		private static final long serialVersionUID = 1L;

	{
		add("e");
	    add("o");
	    add("a");
	    add("os");
	    add("as");
	    add("ao");
	    add("à");
	    add("aos");
	    add("às");
	    add("de");
	    add("do");
	    add("da");
	    add("dos");
	    add("das");
	}};
	
	/**
	 * Set the first character from string text to the specified upper case character.
	 * 
	 * @param text
	 * @return the updated string text.
	 */
	public static String setFirstCharacterToUpperCase(String text) {

		final StringBuilder result = new StringBuilder(text.length());
		String[] words = text.split(" ");
		for(int i = 0, l = words.length; i < l; ++i) {
			if(i > 0) {
				result.append(" ");      
			}
			
			String word = words[i];
			if(word.isEmpty()) {
				result.append(" ");
				continue;
			}
			
			if(!NAME_PROPOSITIONS.contains(word)) {
				result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
			} else {
				result.append(words[i].substring(0));
			}
		}
		
		return result.toString();
	}
	
	public static char getFirstCharInLowerCase(String text) {
		
		if(text == null || text.isEmpty()) {
			return ' ';
		} else {
			// normalizing and ignored accents
			text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			return text.toLowerCase(Locale.getDefault()).charAt(0);
		}
		
	}
	
}
