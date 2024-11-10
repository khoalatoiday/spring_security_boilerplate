package com.example.spring_security.repository;

import com.example.spring_security.model.Accounts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AccountsRepository extends CrudRepository<Accounts, Long> {

    List<Accounts> findByCustomerIdIn(List<Long> ids);
    Accounts findByCustomerId(long customerId);

}
