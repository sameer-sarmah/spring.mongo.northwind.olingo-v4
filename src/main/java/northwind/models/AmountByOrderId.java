package northwind.models;

import org.springframework.data.annotation.Id;

public class AmountByOrderId {
	  	@Id
	    private String OrderID;
	    private Double totalPrice;
	    
		public AmountByOrderId(String OrderID, Double totalPrice) {
			super();
			this.OrderID = OrderID;
			this.totalPrice = totalPrice;
		}
		public String getOrderID() {
			return OrderID;
		}
		public void setOrderID(String OrderID) {
			this.OrderID = OrderID;
		}
		public Double getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(Double totalPrice) {
			this.totalPrice = totalPrice;
		}
	    
	    
	    
}
