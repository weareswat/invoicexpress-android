/* * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.rupeal.invoicexpress.adapters;

import pt.rupeal.invoicexpress.model.FragmentNavigationModel;
import pt.rupeal.invoicexpress.server.InvoiceXpress;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * represents each page as a {@link Fragment} that is persistently
 * kept in the fragment manager as long as the user can return to the page.
 */
public abstract class FragmentPagerAdapter extends PagerAdapter {
	
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    
    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     * This method is called just when the fragment dosen't exists in fragment manager
     */
    public abstract Fragment getItem(int position);
    
    // public abstract void updateArguments(Fragment fragment, Bundle args);

    // public abstract Bundle getArgs();
    
    @Override
    public void startUpdate(View container) {
    	// do nothing
    }
    
    @Override
    public Object instantiateItem(View container, int position) {
    	
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        String name = makeFragmentName(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        // Do we already have this fragment?
        if (fragment != null) {
//        	updateArguments(fragment, getArgs());
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
        	mCurTransaction.add(container.getId(), fragment, name);
        	// adiconar o filho
        	InvoiceXpress.getInstance().getLastFragment().getFragmentsTagChildren().add(name);
        }
        
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
        }
        
        if(InvoiceXpress.DEBUG) {
        	Log.d(this.getClass().getCanonicalName(), "attach or add fragment " + name);
        }
        
        return fragment;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
//        if (DEBUG) Log.v(TAG, "Detaching item #" + position + ": f=" + object
//                + " v=" + ((Fragment)object).getView());

//        mCurTransaction.hide((Fragment) object);
        mCurTransaction.detach((Fragment)object);
//        mCurTransaction.remove((Fragment)object);
        // InvoiceXpress.getInstance().getFragments().remove((Fragment)object);
    }

    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(View container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
        	// mCurTransaction.commit();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
        
        container.setVisibility(ViewPager.VISIBLE);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    	// do nothing
    }

    private String makeFragmentName(int index) {
    	// obter o ultimo fragment adiconado na lista e aki criar o novo novo a partir deste gajo...
    	FragmentNavigationModel fragmentNavModel = InvoiceXpress.getInstance().getLastFragment();
    	return fragmentNavModel.getFragmentTag() + index;
        //return tagName.getValue() + indexAux + "filtered" + index;
    }
    
}
