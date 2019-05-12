package northwind.models;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.springframework.stereotype.Component;
import org.apache.olingo.commons.api.data.Property;
import northwind.documents.Customer;
import northwind.documents.Order;
import northwind.documents.Product;
import northwind.documents.Shipper;
import northwind.exception.CoreException;
import northwind.repositories.CustomerRepo;
import northwind.repositories.OrderRepo;
import northwind.repositories.ProductRepo;
import northwind.repositories.ShipperRepo;

@Component
public class NorthwindReadHandler {

	private CustomerRepo customerRepo;
	private OrderRepo orderRepo;
	private ProductRepo productRepo;
	private ShipperRepo shipperRepo;

	public NorthwindReadHandler(CustomerRepo customerRepo, OrderRepo orderRepo, ProductRepo productRepo,
			ShipperRepo shipperRepo) {
		super();
		this.customerRepo = customerRepo;
		this.orderRepo = orderRepo;
		this.productRepo = productRepo;
		this.shipperRepo = shipperRepo;
	}

	public Entity readEntity(String id, Class klass) throws CoreException {
		if (klass.equals(Product.class)) {
			return NorthwindTransformer.createProductEntity(
					this.productRepo.findById(id).orElseThrow(() -> new CoreException("Product not found", 404)));
		} else if (klass.equals(Order.class)) {
			return NorthwindTransformer.createOrderEntity(
					this.orderRepo.findById(id).orElseThrow(() -> new CoreException("Order not found", 404)));
		} else if (klass.equals(Shipper.class)) {
			return NorthwindTransformer.createShipperEntity(
					this.shipperRepo.findById(id).orElseThrow(() -> new CoreException("Shipper not found", 404)));
		} else if (klass.equals(Customer.class)) {
			return NorthwindTransformer.createCustomerEntity(
					this.customerRepo.findById(id).orElseThrow(() -> new CoreException("Customer not found", 404)));
		}
		return null;
	}

	public EntityCollection readEntitySet(Class klass) {
		EntityCollection retEntitySet = new EntityCollection();
		if (klass.equals(Product.class)) {
			List<Entity> entities = this.productRepo.findAll().stream().map((product) -> {
				return NorthwindTransformer.createProductEntity(product);
			}).collect(Collectors.toList());
			retEntitySet.getEntities().addAll(entities);
		} else if (klass.equals(Order.class)) {
			List<Entity> entities = this.orderRepo.findAll().stream().map((order) -> {
				return NorthwindTransformer.createOrderEntity(order);
			}).collect(Collectors.toList());
			retEntitySet.getEntities().addAll(entities);
		} else if (klass.equals(Shipper.class)) {
			List<Entity> entities = this.shipperRepo.findAll().stream().map((shipper) -> {
				return NorthwindTransformer.createShipperEntity(shipper);
			}).collect(Collectors.toList());
			retEntitySet.getEntities().addAll(entities);
		} else if (klass.equals(Customer.class)) {
			List<Entity> entities = this.customerRepo.findAll().stream().map((customer) -> {
				return NorthwindTransformer.createCustomerEntity(customer);
			}).collect(Collectors.toList());
			retEntitySet.getEntities().addAll(entities);
		}
		return retEntitySet;
	}
	
	public Entity readNavigationEntity(String id, Class sourceEntity,Class targetEntity) throws CoreException {
		 if (sourceEntity.equals(Order.class) && ((targetEntity.equals(Shipper.class)) || ((targetEntity.equals(Customer.class))))) {
				Entity order = NorthwindTransformer.createOrderEntity(
						this.orderRepo.findById(id).orElseThrow(() -> new CoreException("Order not found", 404)));
				Predicate<Property> predicate = null;
				if(targetEntity.equals(Shipper.class)) {
					predicate =(property) -> { 
						return property.getName().equals(NorthwindTransformer.SHIPPER);
					};
				}
				else if(targetEntity.equals(Customer.class)) {
					predicate = (property) -> { 
						return property.getName().equals(NorthwindTransformer.CUSTOMER);
					};
				} 
				List<Entity> target = order.getProperties().stream()
					.filter(predicate)
					.map((property)->{
						return (Entity)property.getValue();
					})
					.collect(Collectors.toList());
				target.get(0);
			} 
		return null;
	}
}
