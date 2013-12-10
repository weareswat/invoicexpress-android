package pt.rupeal.invoicexpress.widgets;

import java.util.List;

import pt.rupeal.invoicexpress.MainActivity;
import pt.rupeal.invoicexpress.R;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.AboutFragment;
import pt.rupeal.invoicexpress.fragments.AccountDetailsFragment;
import pt.rupeal.invoicexpress.fragments.AccountsFragment;
import pt.rupeal.invoicexpress.fragments.ContactDetailsFragment;
import pt.rupeal.invoicexpress.fragments.ContactsFragment;
import pt.rupeal.invoicexpress.fragments.DashBoardFragment;
import pt.rupeal.invoicexpress.fragments.DocumentDetailsFragment;
import pt.rupeal.invoicexpress.fragments.DocumentsFragment;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment;
import pt.rupeal.invoicexpress.fragments.DocumentsListFragment.DocumentFilterFragment;
import pt.rupeal.invoicexpress.fragments.EmailFragment;
import pt.rupeal.invoicexpress.fragments.ItemDetailsFragment;
import pt.rupeal.invoicexpress.fragments.MoreFragment;
import pt.rupeal.invoicexpress.model.FragmentNavigationModel;
import pt.rupeal.invoicexpress.server.AccountDetailsRestHandler;
import pt.rupeal.invoicexpress.server.AsyncTask;
import pt.rupeal.invoicexpress.server.ChartRestHandler;
import pt.rupeal.invoicexpress.server.ContactsRestHandler;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.widget.Toast;

public class TabListener<T extends Fragment> implements android.app.ActionBar.TabListener {
    
    private final MainActivity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    
    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(MainActivity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }
    
    /* The following are each of the ActionBar.TabListener callbacks */
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	
    	// get active async task
    	AsyncTask<?, ?, ?> asyncTaskActive = InvoiceXpress.getInstance().getAsyncTaskActive();
    	if(asyncTaskActive != null) {
    		// if the active async task is the account details task and it's running then the application will cancel it and show a warning message
    		if(asyncTaskActive instanceof AccountDetailsRestHandler && asyncTaskActive.cancel(true)) {
    			Toast.makeText(mActivity, R.string.error_actionbar_navigation, Toast.LENGTH_LONG).show();
    			return;
    		} else {
    			// cancel active async task
	    		asyncTaskActive.cancel(true);
	    		// dismiss progress bar
				asyncTaskActive.dismissProgressBar();
    		}
    	}
    	
    	// remove all fragments, to prevent breaking fragments
    	removeAllFragments(ft);
    	// the wanted fragment is dash board and there is no data chart
		if(FragmentTagsEnum.DASHBOARD.getValue().equals(mTag)) {
			if(!InvoiceXpress.getInstance().isChartsRequested()) {
				String[] params = new String[] {String.valueOf(DocumentFilterFragment.NO_FILTER)};
				ChartRestHandler restHandler = new ChartRestHandler(mActivity);
				InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
				restHandler.execute(params);
				updateToSelectedTabIcon(tab);
				return;
			}
		// the wanted fragment is contacts and there are no contacts
    	} else if(FragmentTagsEnum.CONTACTS.getValue().equals(mTag)) {
    		if(!InvoiceXpress.getInstance().isContactsRequested()) {
    			String[] params = new String[] {"false", ""};
    			ContactsRestHandler restHandler = new ContactsRestHandler(mActivity);
    			InvoiceXpress.getInstance().setAsyncTaskActive(restHandler);
    			restHandler.execute(params);
    			updateToSelectedTabIcon(tab);
    			return;
    		}
    	} 
		
    	// the indexAux is used to create an unique id for new fragment
		int indexAux = InvoiceXpress.getInstance().getFragmentsSize();
		Fragment fragment = Fragment.instantiate(mActivity, mClass.getName());
		ft.add(android.R.id.content, fragment, mTag + indexAux);
		
		FragmentNavigationModel fragmentNavModel = new FragmentNavigationModel(fragment.getTag());
		InvoiceXpress.getInstance().getFragments().add(fragmentNavModel);

    	// update selected icon
        updateToSelectedTabIcon(tab);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	// update tab icon
        updateToUnSelectedTabIcon(tab);
    }
    
    private void removeAllFragments(FragmentTransaction ft) {
    	
    	List<FragmentNavigationModel> fragmentsNavModel = InvoiceXpress.getInstance().getFragments();
    	for (FragmentNavigationModel fragmentNavModel : fragmentsNavModel) {
    		
    		if(fragmentNavModel.hasChildren()) {
    			List<String> fragmentsTagsChildren = fragmentNavModel.getFragmentsTagChildren();
    			for (String fragmentTagChild : fragmentsTagsChildren) {
    				Fragment fragment = mActivity.getFragmentManager().findFragmentByTag(fragmentTagChild);
    				if(fragment != null) {
    					ft.remove(fragment);
    				}
				}
    		}
    		
    		Fragment fragment = mActivity.getFragmentManager().findFragmentByTag(fragmentNavModel.getFragmentTag());
    		if(fragment != null) {
    			ft.remove(fragment);
    		}
		}
    	
    	// clear application cache fragments
    	InvoiceXpress.getInstance().getFragments().clear();
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    	onTabUnselected(tab, ft);
    	onTabSelected(tab, ft);
    }
    
    public static final String TAB_POSITION = "tab_position";
    
    private void updateToSelectedTabIcon(Tab tab) {
    	
    	switch (tab.getPosition()) {
			case 0:
				tab.setIcon(R.drawable.icon_1_selected);
				break;
			case 1:
				tab.setIcon(R.drawable.icon_2_selected);
				break;
			case 2:
				tab.setIcon(R.drawable.icon_3_selected);
				break;
			case 3:
				tab.setIcon(R.drawable.icon_4_selected);
				break;			
			default:
				break;
		}
    	
    	if(mClass.getName().equals(DashBoardFragment.class.getName())) {
    		mActivity.getActionBar().setTitle(mActivity.getResources().getString(R.string.dashBoard_actionBar_title));
    	} else if(mClass.getName().equals(DocumentsFragment.class.getName())) {
    		mActivity.getActionBar().setTitle(mActivity.getResources().getString(R.string.documents_actionBar_title));
    	} else if(mClass.getName().equals(DocumentsListFragment.class.getName())) {
    	} else if(mClass.getName().equals(DocumentDetailsFragment.class.getName())) {
    	} else if(mClass.getName().equals(ItemDetailsFragment.class.getName())) {
    	} else if(mClass.getName().equals(ContactsFragment.class.getName())) {
    		mActivity.getActionBar().setTitle(mActivity.getResources().getString(R.string.conctacs_actionBar_title));
    	} else if(mClass.getName().equals(ContactDetailsFragment.class.getName())) {
    	} else if(mClass.getName().equals(MoreFragment.class.getName())) {
    		mActivity.getActionBar().setTitle(mActivity.getResources().getString(R.string.more_actionBar_title));
    	} else if(mClass.getName().equals(AccountDetailsFragment.class.getName())) {
    	} else if(mClass.getName().equals(AccountsFragment.class.getName())) {
    	} else if(mClass.getName().equals(AboutFragment.class.getName())) {
    	} else if(mClass.getName().equals(EmailFragment.class.getName())) {
    	}
    }
    
    private void updateToUnSelectedTabIcon(Tab tab) {
    	
    	switch (tab.getPosition()) {
		case 0:
			tab.setIcon(R.drawable.icon_1);
			break;
		case 1:
			tab.setIcon(R.drawable.icon_2);
			break;
		case 2:
			tab.setIcon(R.drawable.icon_3);
			break;
		case 3:
			tab.setIcon(R.drawable.icon_4);
			break;			
		default:
			break;
		}
    }

}
