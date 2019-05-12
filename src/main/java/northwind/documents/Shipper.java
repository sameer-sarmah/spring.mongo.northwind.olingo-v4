package northwind.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Shipper {
	@Id
	private String shipperID;
	private String companyName;
	private String phone;

	public Shipper(String shipperID, String companyName, String phone) {
		super();
		this.shipperID = shipperID;
		this.companyName = companyName;
		this.phone = phone;
	}

	public String getShipperID() {
		return shipperID;
	}

	public void setShipperID(String shipperID) {
		this.shipperID = shipperID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
