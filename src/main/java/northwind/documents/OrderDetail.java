package northwind.documents;
/*
 * We are referencing Product in OrderDetail
 * 1.Product is an independent entity
 * 2.Product is not volatile
 * 3.OrderDetail and Product are not queried together.We are interested in the total price ((1-discount)*(quantity*unitPrice))
 * 4.Product can be part of many OrderDetail
 * */
public class OrderDetail {
	private String productID;
	private int quantity;
	private double discount;
	private double unitPrice;
	
	public OrderDetail(String productID, int quantity, double discount, double unitPrice) {
		super();
		this.productID = productID;
		this.quantity = quantity;
		this.discount = discount;
		this.unitPrice = unitPrice;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setunitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	
}
