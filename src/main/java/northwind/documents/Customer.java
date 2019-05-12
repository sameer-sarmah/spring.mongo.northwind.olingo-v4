package northwind.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 * We are embedding Address in Customer
 * 1.Our use case is that Address will not be queried independently thus need not be a independent entity,that is Address is a dependent entity
 * 2.Address is not volatile
 * 3.Customer and his Address, both are queried together
 * 4.Our use case is that one customer can have only one address 
 * */
@Document
public class Customer {

	@Id
	private String customerID;
	private String customerName;
	private String phone;

	private Address address;
	
	public Customer(String customerID, String customerName, String phone, Address address) {
		super();
		this.customerID = customerID;
		this.customerName = customerName;
		this.phone = phone;
		this.address = address;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
	
}
