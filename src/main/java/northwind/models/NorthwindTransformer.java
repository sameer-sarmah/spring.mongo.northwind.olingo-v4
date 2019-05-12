package northwind.models;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.springframework.stereotype.Component;

import northwind.documents.Address;
import northwind.documents.Category;
import northwind.documents.Customer;
import northwind.documents.Order;
import northwind.documents.OrderDetail;
import northwind.documents.Product;
import northwind.documents.Shipper;

@Component
public class NorthwindTransformer {
	public static final String CONTAINER="Northwind";
	public static final String NAMESPACE="OData";
	public static final String PRODUCT_ET="Product";
	public static final String ORDER_ET ="Order";
	public static final String CUSTOMER_ET ="Customer";
	public static final String SHIPPER_ET ="Shipper";

	
	public static final String PRODUCT_ID="productID";
	public static final String PRODUCT_NAME="productName";
	public static final String UNIT_PRICE="unitPrice";
	public static final String QUANTITY_PER_UNIT="quantityPerUnit";
	public static final String CATEGORY="category";
	public static final String QUANTITY="quantity";
	public static final String DISCOUNT="discount";
	
	public static final String CUSTOMER_ID="customerID";
	public static final String CUSTOMER_NAME="customerName";
	public static final String PHONE="phone";
	public static final String ADDRESS="address";
	
	public static final String SHIPPER_ID="shipperID";
	public static final String COMPANY_NAME="companyName";
	
	public static final String STREET="street";
	public static final String CITY="city";
	public static final String COUNTRY="country";
	public static final String ZIP="ZIP";

	public static final String CATEGORY_ID="categoryID";
	public static final String CATEGORY_NAME="categoryName";
	public static final String DESCRIPTION="description";
	
	public static final String ORDER_ID="orderID";
	public static final String ORDERED_DATE="orderedDate";
	public static final String SHIPPED_DATE="shippedDate";
	public static final String SHIPPER="shipper";
	public static final String CUSTOMER="customer";
	public static final String ORDER_ITEMS="orderitems";
	public static final String SHIPPED_ADDRESS="shippedAddress";
	
	public static Entity createProductEntity(Product product) {
	    Entity entity = new Entity();
	    entity.setId(URI.create(product.getProductID()));
	    entity.addProperty(new Property(null,PRODUCT_ID, ValueType.PRIMITIVE, product.getProductID()));
	    entity.addProperty(new Property(null,PRODUCT_NAME, ValueType.PRIMITIVE,product.getProductName()));
	    entity.addProperty(new Property(null,QUANTITY_PER_UNIT, ValueType.PRIMITIVE,product.getQuantityPerUnit()));
	    entity.addProperty(new Property(null,UNIT_PRICE, ValueType.PRIMITIVE,product.getUnitPrice()));
	    entity.addProperty(new Property(null,CATEGORY, ValueType.COMPLEX,createCategoryEntity(product.getCategory())));
	    entity.setType(NAMESPACE+"."+PRODUCT_ET);
	    return entity;
	}
	

	public static Entity createOrderEntity(Order order) {
	    Entity entity = new Entity();
	    entity.setId(URI.create(order.getOrderID()));
	    entity.setType(NAMESPACE+"."+ORDER_ET);
	    entity.addProperty(new Property(null,ORDER_ID, ValueType.PRIMITIVE, order.getOrderID()));
	    entity.addProperty(new Property(null,ORDERED_DATE, ValueType.PRIMITIVE,order.getOrderedDate()));
	    entity.addProperty(new Property(null,SHIPPED_DATE, ValueType.PRIMITIVE, order.getOrderID()));
	    entity.addProperty(new Property(null,SHIPPED_ADDRESS, ValueType.COMPLEX, createAddressEntity(order.getShippedAddress())));
	    entity.addProperty(new Property(null,SHIPPER, ValueType.ENTITY, createShipperEntity(order.getShipper())));
	    entity.addProperty(new Property(null,CUSTOMER, ValueType.ENTITY, createCustomerEntity(order.getCustomer())));
	    List<ComplexValue> orderDetails = order.getOrderitems().stream()
	    		.map(NorthwindTransformer::createOrderDetailEntity)
	    		.collect(Collectors.toList());
	    entity.addProperty(new Property(null,ORDER_ITEMS, ValueType.COLLECTION_COMPLEX, orderDetails));
//        Link link = new Link();
//        link.setTitle(SIEDMProvider.ATTR_NATIVE_DATA);
//        link.setInlineEntity(nativeDataEntity);
//        entity.getNavigationLinks().add(link);
	    return entity;
	}

	public static Entity createCustomerEntity(Customer customer) {
	    Entity entity = new Entity();
	    entity.setId(URI.create(customer.getCustomerID()));
	    entity.setType(NAMESPACE+"."+CUSTOMER_ET);
	    entity.addProperty(new Property(null,CUSTOMER_ID, ValueType.PRIMITIVE, customer.getCustomerID()));
	    entity.addProperty(new Property(null,CUSTOMER_NAME, ValueType.PRIMITIVE, customer.getCustomerName()));
	    entity.addProperty(new Property(null,PHONE, ValueType.PRIMITIVE, customer.getPhone()));
	    entity.addProperty(new Property(null,ADDRESS, ValueType.COMPLEX, createAddressEntity(customer.getAddress())));
	    return entity;
	}

	public static Entity createShipperEntity(Shipper shipper ) {
	    Entity entity = new Entity();
	    entity.setId(URI.create(shipper.getShipperID()));
	    entity.setType(NAMESPACE+"."+SHIPPER_ET);
	    entity.addProperty(new Property(null,SHIPPER_ID, ValueType.PRIMITIVE, shipper.getShipperID()));
	    entity.addProperty(new Property(null,COMPANY_NAME, ValueType.PRIMITIVE, shipper.getCompanyName()));
	    entity.addProperty(new Property(null,PHONE, ValueType.PRIMITIVE, shipper.getPhone()));
	    return entity;
	}

	public static ComplexValue createAddressEntity(Address address) {
		 ComplexValue complexValue = new ComplexValue();
		 complexValue.getValue().add(new Property(null,STREET, ValueType.PRIMITIVE, address.getstreet()));
		 complexValue.getValue().add(new Property(null,CITY, ValueType.PRIMITIVE, address.getCity()));
		 complexValue.getValue().add(new Property(null,COUNTRY, ValueType.PRIMITIVE, address.getCountry()));
		 complexValue.getValue().add(new Property(null,ZIP, ValueType.PRIMITIVE, address.getZIP()));
		 return complexValue;
	}

	public static ComplexValue createCategoryEntity(Category category) {
		 ComplexValue complexValue = new ComplexValue();
		 complexValue.getValue().add(new Property(null,CATEGORY_ID, ValueType.PRIMITIVE,category.getCategoryID()));
		 complexValue.getValue().add(new Property(null,CATEGORY_NAME, ValueType.PRIMITIVE,category.getCategoryName()));
		 complexValue.getValue().add(new Property(null,DESCRIPTION, ValueType.PRIMITIVE, category.getDescription()));
			 return complexValue;
	}

	public static ComplexValue createOrderDetailEntity(OrderDetail orderDetail) {
		 ComplexValue complexValue = new ComplexValue();
		 complexValue.getValue().add(new Property(null,PRODUCT_ID, ValueType.PRIMITIVE, orderDetail.getProductID()));
		 complexValue.getValue().add(new Property(null,QUANTITY, ValueType.PRIMITIVE, orderDetail.getQuantity()));
		 complexValue.getValue().add(new Property(null,DISCOUNT, ValueType.PRIMITIVE, orderDetail.getDiscount()));
		 complexValue.getValue().add(new Property(null,UNIT_PRICE, ValueType.PRIMITIVE,orderDetail.getUnitPrice()));
		 return complexValue;
	}
	
	
}
