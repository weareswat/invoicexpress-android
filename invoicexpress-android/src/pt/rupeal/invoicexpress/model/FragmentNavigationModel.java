package pt.rupeal.invoicexpress.model;

import java.util.ArrayList;
import java.util.List;

public class FragmentNavigationModel {

	private String fragmentTag;
	private List<String> fragmentsTagChildreen;
	
	public FragmentNavigationModel() {
		this("");
	}
	
	public FragmentNavigationModel(String fragmentTag) {
		this.fragmentTag = fragmentTag;
		this.fragmentsTagChildreen = new ArrayList<String>();
	}

	public String getFragmentTag() {
		return fragmentTag;
	}

	public void setFragmentTag(String fragmentTag) {
		this.fragmentTag = fragmentTag;
	}

	public List<String> getFragmentsTagChildren() {
		return fragmentsTagChildreen;
	}

	public void setFragmentsTagChildreen(List<String> fragmentsTagChildreen) {
		this.fragmentsTagChildreen = fragmentsTagChildreen;
	}
	
	public boolean hasChildren() {
		return !fragmentsTagChildreen.isEmpty();
	}
	
}
