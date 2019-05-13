package northwind.repositories;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import northwind.documents.Product;

@Primary
@Repository("mongoProductRepo")
public interface ProductRepo extends MongoRepository<Product, String>{
	@Query("{ $and : [{ 'unitPrice' :{ $lte: ?0 }},{ 'unitPrice' :{ $gte: ?1 }}]}")
	List<Product> findProductBetweenPrice(double lte,double gte);
	
	
}
