package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            select c from Customer c
            where lower(c.companyName) like lower(concat('%', :search, '%'))
               or lower(c.contactName) like lower(concat('%', :search, '%'))
               or lower(c.email) like lower(concat('%', :search, '%'))
               or lower(coalesce(c.city, '')) like lower(concat('%', :search, '%'))
               or lower(coalesce(c.country, '')) like lower(concat('%', :search, '%'))
               or lower(coalesce(c.industry, '')) like lower(concat('%', :search, '%'))
            """)
    Page<Customer> search(@Param("search") String search, Pageable pageable);
}
