import java.text.DecimalFormat;
import java.text.Normalizer;

public class Test {

	public static void main(String[] args) {
//        System.out.println(convertToMoneyValye(1000.0));
//        System.out.println(convertToMoneyValye(560.0));
//        System.out.println(convertToMoneyValye(0.0));
//        System.out.println(convertToMoneyValye(8766.56));
//        System.out.println(convertToMoneyValye(9999999.99));
		
		compareStrings();
	}
	
	public static String convertToMoneyValye(double value) {
		DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
		myFormatter.getDecimalFormatSymbols();
		return myFormatter.format(value);
	}
	
	public static void compareStrings() {
		System.out.println(Normalizer.normalize("É", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
		System.out.println(Normalizer.normalize("E", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
		System.out.println("Resultado da comparação " + "E".compareTo("É"));
		System.out.println("Resultado da comparação " + "T".compareTo("É"));
		System.out.println("Resultado da comparação " + "É".compareTo("Z"));
	}

}
