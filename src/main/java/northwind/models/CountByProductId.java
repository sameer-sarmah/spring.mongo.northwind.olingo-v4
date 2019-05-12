package northwind.models;

import org.springframework.data.annotation.Id;

public class CountByProductId {
  	@Id
    private String productID;
    private int totalCount;
    
	public CountByProductId(String productID, int totalCount) {
		super();
		this.productID = productID;
		this.totalCount = totalCount;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
    
    
    
}
