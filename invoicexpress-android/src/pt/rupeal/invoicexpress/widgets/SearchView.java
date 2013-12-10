package pt.rupeal.invoicexpress.widgets;

import pt.rupeal.invoicexpress.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SearchView extends android.widget.SearchView {

	public SearchView(Context context) {
		super(context);
		update();
	}
	
	public SearchView(Context context, AttributeSet attr) {
		super(context, attr);
		update();
	}

	private void update() {
		// the default search button of android Search View is gray
		// set search button image (white)
        int searchButtonId = getContext().getResources().getIdentifier("android:id/search_button", null, null);
        // get image
        ImageView searchButton = (ImageView) findViewById(searchButtonId);
        // set new image
        searchButton.setImageResource(R.drawable.ic_action_search);	    		

        // set layout background
        int searchPlateId = getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        // getting the 'search_plate' LinearLayout.
        View searchPlate = findViewById(searchPlateId);
        // Setting background of 'search_plate' to earlier defined drawable.            
        searchPlate.setBackgroundResource(R.drawable.textfield_searchview_invoicexpress);
        
        // close button set new green background
        int searchCloseButtonId = getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        View closeButton = findViewById(searchCloseButtonId);
        closeButton.setBackgroundResource(R.drawable.tab_indicator_ab_invoicexpress);
	}
	
}
