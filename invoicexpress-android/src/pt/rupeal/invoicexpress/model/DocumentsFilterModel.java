package pt.rupeal.invoicexpress.model;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

public class DocumentsFilterModel implements Serializable {

	private static final long serialVersionUID = -830822005671236438L;
	
	private SortedMap<Integer, DocumentsModel> documents;

	public DocumentsFilterModel() {
		this.documents = new TreeMap<Integer, DocumentsModel>();
	}
	
	public SortedMap<Integer, DocumentsModel> getDocuments() {
		return documents;
	}

	public void setDocuments(SortedMap<Integer, DocumentsModel> documents) {
		this.documents = documents;
	}

}
