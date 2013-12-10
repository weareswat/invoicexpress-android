package pt.rupeal.invoicexpress.model;

import java.io.Serializable;

public class ItemModel implements Serializable {

	private static final long serialVersionUID = -3487513440308855370L;
	
	public static final String ITEM = "item";
	
	private String name;
	private String description;
	
	private double unitPrice;
	private double quantity;
	private double taxAmount;
	private double discount;
	private double subTotal;
	private double total;	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
//	public String getUnitPrice() {
//		return unitPrice;
//	}
//
//	public void setUnitPrice(String unitPrice) {
//		this.unitPrice = unitPrice;
//	}
//
//	public String getQuantity() {
//		return quantity;
//	}
//
//	public void setQuantity(String quantity) {
//		this.quantity = quantity;
//	}
//
//	public String getTaxAmount() {
//		return taxAmount;
//	}
//
//	public void setTaxAmount(String taxAmount) {
//		this.taxAmount = taxAmount;
//	}
//
//	public String getDiscount() {
//		return discount;
//	}
//
//	public void setDiscount(String discount) {
//		this.discount = discount;
//	}
//
//	public String getTotal() {
//		return total;
//	}
//	
//	public void setTotal(String total) {
//		this.total = total;
//	}
	
	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getSubTotal() {
		return subTotal;
	}
	
	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}	
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}	
	
}
