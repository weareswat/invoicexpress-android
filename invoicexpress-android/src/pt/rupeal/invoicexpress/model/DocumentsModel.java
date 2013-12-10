package pt.rupeal.invoicexpress.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.rupeal.invoicexpress.utils.DocumentsComparable;

/**
 * @author dneves
 * 
 * This structure can be find in InvoiceXpress Singleton and ContactsModel.
 * DocumentsModel will save the documents user data and documents client data. 
 */
public class DocumentsModel implements Serializable {

	private static final long serialVersionUID = -7962853032893508474L;
	
	private Map<String, DocumentModel> documents;
	private int currentPage;
	private int totalPages;
	private int downloadedDocuments;
	private int totalDocuments;
	
	public DocumentsModel() {
		documents = new HashMap<String, DocumentModel>();
	}
	
	public Map<String, DocumentModel> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Map<String, DocumentModel> documents) {
		this.documents = documents;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
	public int getDownloadedDocuments() {
		return downloadedDocuments;
	}
	
	public void setDownloadedDocuments(int downloadedDocuments) {
		this.downloadedDocuments = downloadedDocuments;
	}
	
	public void addDownloadedDocuments(int downloadedDocuments) {
		this.downloadedDocuments += downloadedDocuments;
	}
	
	public int getTotalDocuments() {
		return totalDocuments;
	}
	
	public void setTotalDocuments(int totalDocuments) {
		this.totalDocuments = totalDocuments;
	}
	
	public void clear() {
		documents.clear();
		currentPage = 0;
		totalPages = 0;
		downloadedDocuments = 0;
		totalDocuments = 0;
	}
	
	/**
	 * Get documents list sorted by filter to show.
	 * @return
	 */
	public List<DocumentModel> getDocumentsSorted(){
		List<DocumentModel> documentsSorted = new ArrayList<DocumentModel>(documents.values());
		Collections.sort(documentsSorted, new DocumentsComparable());
		return documentsSorted;
	}	
	
}
