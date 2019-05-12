package northwind.models;

import org.springframework.data.annotation.Id;

public class AmountByProductId {
  	@Id
    private String productID;
    private Double totalPrice;
    
	public AmountByProductId(String productID, Double totalPrice) {
		super();
		this.productID = productID;
		this.totalPrice = totalPrice;
	}
	
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
    
    
    
}
