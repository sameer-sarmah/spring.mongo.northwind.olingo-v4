package northwind.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Product;

@Primary
@Repository("mongoProductRepo")
public interface ProductRepo extends MongoRepository<Product, String>{

}
