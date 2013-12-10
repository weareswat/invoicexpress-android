package pt.rupeal.invoicexpress.enums;

import android.content.Context;
import pt.rupeal.invoicexpress.R;

public enum DocumentStatusEnum {

	CANCELED ("canceled", R.drawable.icon_status_1, R.string.doc_status_canceled, R.string.doc_status_gui_canceled, "canceled"),
	DRAFT ("draft", R.drawable.icon_status_5, R.string.doc_status_draft, R.string.doc_status_gui_draft, ""),
	FINAL ("final", R.drawable.icon_status_4, R.string.doc_status_final, R.string.doc_status_gui_final, "finalized"),
	SECOND_COPY ("second_copy", R.drawable.icon_status_2, R.string.doc_status_secondCopy, R.string.doc_status_gui_secondCopy, "second_copy"),
	SETTLED ("settled", R.drawable.icon_status_3, R.string.doc_status_settled, R.string.doc_status_gui_settled, "settled"),
	// the following types is used just on change status operation
	UNSETTLED("", -1, R.string.doc_status_unsettled, -1, "unsettled"),
	DELETED("deleted", -1, R.string.doc_status_deleted, -1, "deleted");
	
	private String value;
	private int drawableId;
	private int actionLabelId;
	private int labelGui;
	private String xml;
	
	private DocumentStatusEnum(String value, int drawableId, int actionLabelId, int labelGui, String stateXml) {
		this.value = value;
		this.drawableId = drawableId;
		this.actionLabelId = actionLabelId;
		this.labelGui = labelGui;
		this.xml = stateXml;
	}
	
	public String getValue() {
		return value;
	}
	
	public static DocumentStatusEnum getDocumentStatusEnum(String status) {
		DocumentStatusEnum[] statusArray = values();
		for (int i = 0; i < statusArray.length; i++) {
			if(statusArray[i].value.equals(status)){
				return statusArray[i];
			}
		}
		
		return null;
	}	
	
	
	public int getDrawableId() {
		return drawableId;
	}
	
	public static int getDrawableId(String status) {
		DocumentStatusEnum[] statusArray = values();
		for (int i = 0; i < statusArray.length; i++) {
			if(statusArray[i].value.equals(status)){
				return statusArray[i].getDrawableId();
			}
		}
		
		return -1;
	}
	
	public int getActionLabelId() {
		return actionLabelId;
	}
	
	public String getActionLabel(Context context) {
		return context.getResources().getString(actionLabelId);
	}
	
	public static String[] getActionLabels(Context context) {
		DocumentStatusEnum[] statusArray = values();
		String[] labels = new String[statusArray.length];
		for (int i = 0; i < statusArray.length; i++) {
			labels[i] = context.getResources().getString(statusArray[i].actionLabelId);
		}
		
		return labels;
	}
	
	
	public int getLabelGui() {
		return labelGui;
	}
	
	public static String getLabelGui(Context context, String status) {
		DocumentStatusEnum[] statusArray = values();
		for (int i = 0; i < statusArray.length; i++) {
			if(statusArray[i].getValue().equals(status)) {
				return statusArray[i].getLabelGui(context);
			}
		}
		
		return "";
	}
	
	public String getLabelGui(Context context) {
		return context.getResources().getString(labelGui);
	}	

	public void setLabelGui(int labelGui) {
		this.labelGui = labelGui;
	}

	public String getStateXml() {
		return xml;
	}
	
	public static boolean isFinal(String status) {
		return FINAL.getValue().equals(status);
	}

	public static boolean isDeleted(String status) {
		return DELETED.getValue().equals(status);
	}

	public static boolean isCanceled(String status) {
		return CANCELED.getValue().equals(status);
	}	

	public static boolean isSettled(String status) {
		return SETTLED.getValue().equals(status);
	}		

	public static boolean isDraft(String status) {
		return DRAFT.getValue().equals(status);
	}	
	
	public static String convertReceiptStatus(String status) {
		if("sent".equals(status)) {
			return FINAL.value;
		}
		
		return status;
	}

}
