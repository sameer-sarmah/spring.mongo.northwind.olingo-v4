package northwind.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Customer;

@Primary
@Repository("mongoCustomerRepo")
public interface CustomerRepo extends MongoRepository<Customer, String>{

}
