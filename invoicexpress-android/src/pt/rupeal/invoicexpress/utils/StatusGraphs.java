package pt.rupeal.invoicexpress.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.rupeal.invoicexpress.enums.DocumentStatusEnum;

/**
 * @author dneves
 * 
 * The Status Graphs class is an auxiliary class to help us on an update status document.
 * 
 */
public class StatusGraphs {

	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> cashInvoiceGraph;
	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> receiptGraph;
	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> creditNoteGraph;
	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> debitNoteGraph;
	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> invoiceGraph;
	public final Map<DocumentStatusEnum, List<DocumentStatusEnum>> simplifiedInvoiceGraph;
	
	public StatusGraphs() {
		
		cashInvoiceGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		List<DocumentStatusEnum> canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		cashInvoiceGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		List<DocumentStatusEnum> draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.SETTLED);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		cashInvoiceGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		List<DocumentStatusEnum> finalPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
		cashInvoiceGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);

		List<DocumentStatusEnum> secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		cashInvoiceGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);

		List<DocumentStatusEnum> settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
		cashInvoiceGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);		
		
		receiptGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		receiptGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.FINAL);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		receiptGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SECOND_COPY);
		receiptGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);

		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		secondCopyPossibleStatus.add(DocumentStatusEnum.CANCELED);
		receiptGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);

		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		receiptGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);		
		
		creditNoteGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		creditNoteGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.FINAL);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		creditNoteGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(3);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SECOND_COPY);
		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
		creditNoteGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);

		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		secondCopyPossibleStatus.add(DocumentStatusEnum.CANCELED);
		secondCopyPossibleStatus.add(DocumentStatusEnum.SETTLED);
		creditNoteGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);

		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
		creditNoteGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);		
		
		debitNoteGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		debitNoteGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.FINAL);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		debitNoteGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(3);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SECOND_COPY);
		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
		debitNoteGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);

		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		secondCopyPossibleStatus.add(DocumentStatusEnum.CANCELED);
		secondCopyPossibleStatus.add(DocumentStatusEnum.SETTLED);
		debitNoteGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);

		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
		debitNoteGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);			
		
		invoiceGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		invoiceGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.FINAL);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		invoiceGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(3);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SECOND_COPY);
		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
		invoiceGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);
		
		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		secondCopyPossibleStatus.add(DocumentStatusEnum.CANCELED);
		secondCopyPossibleStatus.add(DocumentStatusEnum.SETTLED);
		invoiceGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);
		
		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
		invoiceGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);
		
		simplifiedInvoiceGraph = new HashMap<DocumentStatusEnum, List<DocumentStatusEnum>>(5);
		
		canceledPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
		simplifiedInvoiceGraph.put(DocumentStatusEnum.CANCELED, canceledPossibleStatus);
		
		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		draftPossibleStatus.add(DocumentStatusEnum.FINAL);
		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
		simplifiedInvoiceGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
		
		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(3);
		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
		finalPossibleStatus.add(DocumentStatusEnum.SECOND_COPY);
		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
		simplifiedInvoiceGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);
		
		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
		secondCopyPossibleStatus.add(DocumentStatusEnum.CANCELED);
		secondCopyPossibleStatus.add(DocumentStatusEnum.SETTLED);
		simplifiedInvoiceGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);
		
		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
		simplifiedInvoiceGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);		
		
//		draftPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
//		draftPossibleStatus.add(DocumentStatusEnum.SETTLED);
//		draftPossibleStatus.add(DocumentStatusEnum.DELETED);
//		simplifiedInvoiceGraph.put(DocumentStatusEnum.DRAFT, draftPossibleStatus);
//		
//		finalPossibleStatus = new ArrayList<DocumentStatusEnum>(2);
//		finalPossibleStatus.add(DocumentStatusEnum.CANCELED);
//		finalPossibleStatus.add(DocumentStatusEnum.SETTLED);
//		simplifiedInvoiceGraph.put(DocumentStatusEnum.FINAL, finalPossibleStatus);
//
//		secondCopyPossibleStatus = new ArrayList<DocumentStatusEnum>(0);
//		simplifiedInvoiceGraph.put(DocumentStatusEnum.SECOND_COPY, secondCopyPossibleStatus);
//
//		settledPossibleStatus = new ArrayList<DocumentStatusEnum>(1);
//		settledPossibleStatus.add(DocumentStatusEnum.UNSETTLED);
//		simplifiedInvoiceGraph.put(DocumentStatusEnum.SETTLED, settledPossibleStatus);		
		
		
	}
	
	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getCashInvoiceGraph() {
		return cashInvoiceGraph;
	}

	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getReceiptGraph() {
		return receiptGraph;
	}

	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getCreditNoteGraph() {
		return creditNoteGraph;
	}

	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getDebitNoteGraph() {
		return debitNoteGraph;
	}
	
	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getInvoiceGraph() {
		return invoiceGraph;
	}

	public Map<DocumentStatusEnum, List<DocumentStatusEnum>> getSimplifiedInvoiceGraph() {
		return simplifiedInvoiceGraph;
	}
	
}
