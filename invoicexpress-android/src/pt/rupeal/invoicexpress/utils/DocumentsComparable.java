package pt.rupeal.invoicexpress.utils;

import java.util.Comparator;
import java.util.Date;

import pt.rupeal.invoicexpress.model.DocumentModel;

public class DocumentsComparable implements Comparator<DocumentModel> {

	@Override
	public int compare(DocumentModel lhs, DocumentModel rhs) {
		Date leftDate = DateUtil.formatDate(lhs.getDate());
		Date rightDate = DateUtil.formatDate(rhs.getDate());
		
		if(leftDate.after(rightDate)) {
			return -1;
		} else if(rightDate.after(leftDate)) {
			return 1;
		} else {
			Long leftDocId = Long.parseLong(lhs.getId());
			Long rigthDocId = Long.parseLong(rhs.getId());
			return leftDocId > rigthDocId ? -1 : (leftDocId == rigthDocId ? 0 : 1);
		}
	}

}
