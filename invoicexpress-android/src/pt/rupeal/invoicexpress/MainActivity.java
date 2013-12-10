package pt.rupeal.invoicexpress;

import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import pt.rupeal.invoicexpress.activities.AuthenticationActivity;
import pt.rupeal.invoicexpress.enums.FragmentTagsEnum;
import pt.rupeal.invoicexpress.fragments.ContactsFragment;
import pt.rupeal.invoicexpress.fragments.DashBoardFragment;
import pt.rupeal.invoicexpress.fragments.DocumentsFragment;
import pt.rupeal.invoicexpress.fragments.MoreFragment;
import pt.rupeal.invoicexpress.model.FragmentNavigationModel;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import pt.rupeal.invoicexpress.server.AuthenticationRestHandler;
import pt.rupeal.invoicexpress.widgets.TabListener;

public class MainActivity extends Activity {
    
	private ActionBar actionBar;
	private Tab dashBoardTab;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// set account manager
    	InvoiceXpress.getInstance().setAccountManager(AccountManager.get(this));
		// get android invoiceXpress saved accounts
		Account[] invoiceXpressAccounts = InvoiceXpress.getInstance().getInvoiceXpressAccount();
		// if there is no account then start authentication activity
		if(invoiceXpressAccounts == null || invoiceXpressAccounts.length == 0) {
			// get login activity
			Intent intent = new Intent(this, AuthenticationActivity.class);
			startActivityForResult(intent, 1);
		} else {
			// login & start application
			login(invoiceXpressAccounts);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1) {
		     if(resultCode == RESULT_OK){
		         initActionBar();       
		     } else if (resultCode == RESULT_CANCELED) {
		    	 finish();
		     }
		} else if(requestCode == 2) {
			 if(resultCode == RESULT_OK){      
				 actionBar.selectTab(dashBoardTab);
		     } else if (resultCode == RESULT_CANCELED) {
		    	 finish();
		     }
		}
	}
	
	public void initActionBar() {
		// debug
		if(InvoiceXpress.DEBUG) {
			Log.d(this.getClass().getCanonicalName(), "Init Action Bar");
		}
		
		// setup action bar for tabs       
		actionBar = super.getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setLogo(R.drawable.icon_logo);
		
		dashBoardTab = actionBar.newTab()
				.setIcon(pt.rupeal.invoicexpress.R.drawable.icon_1)
				.setTabListener(new TabListener<DashBoardFragment>(
						this, FragmentTagsEnum.DASHBOARD.getValue(), DashBoardFragment.class));
		actionBar.addTab(dashBoardTab, true);
		
		Tab tab = actionBar.newTab()
				.setIcon(pt.rupeal.invoicexpress.R.drawable.icon_2)
				.setTabListener(new TabListener<DocumentsFragment>(
						this, FragmentTagsEnum.DOCUMENTS.getValue(), DocumentsFragment.class));
		actionBar.addTab(tab, false);
		
		tab = actionBar.newTab()
				.setIcon(pt.rupeal.invoicexpress.R.drawable.icon_3)
				.setTabListener(new TabListener<ContactsFragment>(
						this, FragmentTagsEnum.CONTACTS.getValue(), ContactsFragment.class));
		actionBar.addTab(tab, false);
		
		tab = actionBar.newTab()
				.setIcon(pt.rupeal.invoicexpress.R.drawable.icon_4)
				.setTabListener(new TabListener<MoreFragment>(
						this, FragmentTagsEnum.MORE.getValue(), MoreFragment.class));
		actionBar.addTab(tab, false);
		
	}
	
	public ActionBar getActionBar() {
		return actionBar;
	}
	
	private void login(Account[] androidAccounts) {
		String password = InvoiceXpress.getInstance().getAccountManager().getPassword(androidAccounts[0]);
		String[] params = new String[] {androidAccounts[0].name, password};
		new AuthenticationRestHandler(this).execute(params);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {        
    		case android.R.id.home:
    			actionBar.selectTab(actionBar.getSelectedTab());
    			return true;        
    		default:            
    			return super.onOptionsItemSelected(item);    
    	}
    	
    }
    
    @Override
    public void onBackPressed() {
		// check if the progress bar is visible to disable clicks
		if(!InvoiceXpress.isInvoiceXpressClickable(this)) {
			return;
		}
    	
    	// vibrate - time in milliseconds
    	((Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);
    	
    	// remove fragment
    	if(InvoiceXpress.getInstance().hasFragment()) {
    		
        	// if there is just one fragment, show alert dialog
        	if(InvoiceXpress.getInstance().hasOneLastFragment()) { 
        		showAlertDialog();
        		return;
        	}
    		
    		// remove last fragment based on application fragments list cache
    		String lastFragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
    		removeFragment(lastFragmentTag);
    	}
    }
    
    /**
     * Set, add and show new fragment
     * @param mClass, Fragment class
     * @param fragmentTag, Fragment tag (FragmentTagsEnum) to show
     */
    public void addFragment(Class<? extends Fragment> mClass, FragmentTagsEnum fragmentTag) {
    	addFragment(mClass, fragmentTag, null);
    }
    
    
    public void addFragment(Class<? extends Fragment> mClass, FragmentTagsEnum fragmentTag, Bundle args) {    	
    	// get FragmentManager and FragmentTransaction
    	final FragmentManager fm = getFragmentManager();
    	// begin transaction
    	FragmentTransaction ft = fm.beginTransaction();
    	
    	// detach the previous fragment
    	String lastFragmentTag = "";
    	if(InvoiceXpress.getInstance().hasFragment()) {
    		// get fragments
    		FragmentNavigationModel fragmentNavigationModel = InvoiceXpress.getInstance().getLastFragment();
    		// remove fragment children (for dash board or documents list fragments)
			if(fragmentNavigationModel.hasChildren()) {
				List<String> fragmentsTagsChildreen = fragmentNavigationModel.getFragmentsTagChildren();
				for (String child : fragmentsTagsChildreen) {
					Fragment fragmentToRemove = fm.findFragmentByTag(child);
					ft.remove(fragmentToRemove);
				}
			}
    		// detach last attach fragment
			lastFragmentTag = fragmentNavigationModel.getFragmentTag();
			Fragment fragment = fm.findFragmentByTag(lastFragmentTag);
			if(fragment != null) {
				ft.detach(fragment);
			}
    	}
    	// the indexAux is used to create an unique id for new fragment
        int indexAux = InvoiceXpress.getInstance().getFragmentsSize();
        // generate fragment tag string
        String newfragmentTag = fragmentTag.getValue() + indexAux;
    	
    	Fragment fragment = fm.findFragmentByTag(newfragmentTag);
    	// Do we already have this fragment? Never..
    	if(fragment != null) {
			Log.e(this.getClass().getCanonicalName(), "addFragment - the fragment was found in fragment manager");
			return;
    	} else {
    		// instantiate new fragment
    		fragment = Fragment.instantiate(this, mClass.getName());
	        // set arguments
	        fragment.setArguments(args);
	        // add fragment to fragment manager
    		ft.add(android.R.id.content, fragment, newfragmentTag);
    		// add to application cache fragments
    		FragmentNavigationModel fragmentNavModel = new FragmentNavigationModel(newfragmentTag);
    		InvoiceXpress.getInstance().getFragments().add(fragmentNavModel);
    	}
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), "to: " + newfragmentTag
    				+ " from: " + lastFragmentTag);
    	}    	
    	
    	ft.commit();
    }
    
    
    public void refreshFragment(int filterCode) {
    	refreshFragment(null, filterCode);
    }
    
    public void refreshFragment() {
    	refreshFragment(null, -1);
    }
    
    public void refreshFragment(Bundle args) {
    	refreshFragment(args, -1);
    }
    
    private void refreshFragment(Bundle args, int filterCode) {
    	
    	// the refresh is about active fragment
    	// get last fragment
    	String fragmentTag = "";
    	FragmentNavigationModel fragmentNavModel = InvoiceXpress.getInstance().getLastFragment();
    	if(fragmentNavModel.hasChildren() && filterCode != -1) {
    		fragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentsTagChildren().get(filterCode);	
    	} else {
    		fragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
    	}
    	
    	if(InvoiceXpress.DEBUG) {
    		Log.d(this.getClass().getCanonicalName(), "Refreshing fragment " + fragmentTag);
    	}
    	
    	final FragmentManager fm = getFragmentManager();
    	Fragment fragment = fm.findFragmentByTag(fragmentTag);
    	// this is impossible to happen
    	if(fragment == null) {
    		Log.e(this.getClass().getCanonicalName(), "Fragment to refresh dosen't exists.");
    		return;
    	}
    	// begin transaction
    	FragmentTransaction ft = fm.beginTransaction();
    	// detach active fragment
    	ft.detach(fragment);
    	
		if(args != null) {
			fragment.getArguments().clear();
			fragment.getArguments().putAll(args);
		}
		// attach active fragment for refreshing
    	ft.attach(fragment);
    	
    	ft.commit();
    }
    
    /**
     * Remove fragments, used onBackPressed
     * @param fragmentTag, Fragment to hide
     */
//    public void removeFragment(FragmentTagsEnum fragmentTag) {
//    	removeFragment(fragmentTag.getValue());
//    }
    
    public void removeFragment(String fragmentTag) {
    	// get fragment manager and begin a transaction
    	FragmentManager fm = getFragmentManager();
    	final FragmentTransaction ft = fm.beginTransaction();
    	// get fragment to remove
    	FragmentNavigationModel fragmentNavigationModel = InvoiceXpress.getInstance().getFragmentNavModelByFragmentTag(fragmentTag);
		// remove all fragment children
    	if(fragmentNavigationModel.hasChildren()) {
			List<String> fragmentsTagsChildren = fragmentNavigationModel.getFragmentsTagChildren();
			for (String child : fragmentsTagsChildren) {
				Fragment fragmentToRemove = fm.findFragmentByTag(child);
				ft.remove(fragmentToRemove);
			}
		}
    	// remove fragment
		Fragment fragmentToRemove = fm.findFragmentByTag(fragmentTag);
		if(fragmentToRemove != null) {
			ft.remove(fragmentToRemove);
		}
		
		// remove from invoice express fragment management structure
		InvoiceXpress.getInstance().getFragments().remove(
				InvoiceXpress.getInstance().getFragments().size() - 1);

		// restore fragment if there is one
		if(InvoiceXpress.getInstance().hasFragment()) {
			String fragmentTagToAttach = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
			Fragment fragmentToAttach = fm.findFragmentByTag(fragmentTagToAttach);
			ft.attach(fragmentToAttach);
		}
    	
    	ft.commit();
    }
    
    private void showAlertDialog() {
    	// show a dialog box to confirm the exit
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle(R.string.app_name);
		// set dialog message
		alertDialogBuilder
			.setMessage(R.string.dialog_exit_title)
			.setCancelable(false)
			.setPositiveButton(R.string.dialog_exit_yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
		    		// remove last fragment based on application fragments list cache
		    		String lastFragmentTag = InvoiceXpress.getInstance().getLastFragment().getFragmentTag();
		    		removeFragment(lastFragmentTag);
					// current activity
					MainActivity.this.finish();
				}
			})
			.setNegativeButton(R.string.dialog_exit_no,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
    }

}
