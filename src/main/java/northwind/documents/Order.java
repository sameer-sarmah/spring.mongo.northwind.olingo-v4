package northwind.documents;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Order {
	
	@Id
	String orderID;
	Date orderedDate;
	Date shippedDate;
	Address shippedAddress;
	Shipper shipper;
	Customer customer;
	List<OrderDetail> orderitems;
	
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public Date getOrderedDate() {
		return orderedDate;
	}
	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}
	public Date getShippedDate() {
		return shippedDate;
	}
	public void setShippedDate(Date shippedDate) {
		this.shippedDate = shippedDate;
	}
	public Address getShippedAddress() {
		return shippedAddress;
	}
	public void setShippedAddress(Address shippedAddress) {
		this.shippedAddress = shippedAddress;
	}
	public Shipper getShipper() {
		return shipper;
	}
	public void setShipper(Shipper shipper) {
		this.shipper = shipper;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public List<OrderDetail> getOrderitems() {
		return orderitems;
	}
	public void setOrderitems(List<OrderDetail> orderitems) {
		this.orderitems = orderitems;
	}
	
	
}
