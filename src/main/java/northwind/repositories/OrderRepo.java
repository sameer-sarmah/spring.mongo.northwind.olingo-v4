package northwind.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Order;

@Primary
@Repository("mongoOrderRepo")
public interface OrderRepo extends MongoRepository<Order, String>{

}
