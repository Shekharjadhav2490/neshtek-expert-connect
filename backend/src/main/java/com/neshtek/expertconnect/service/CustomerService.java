package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.CustomerRequest;
import com.neshtek.expertconnect.dto.CustomerResponse;
import com.neshtek.expertconnect.entity.Customer;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) { this.repository = repository; }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A customer with this email already exists");
        }
        Customer customer = new Customer();
        apply(customer, request, email);
        return toResponse(repository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) { return toResponse(find(id)); }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> search(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return list(pageable);
        }
        return repository.search(search.trim(), pageable).map(this::toResponse);
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = find(id);
        String email = normalizeEmail(request.email());
        if (!email.equalsIgnoreCase(customer.getEmail()) && repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("A customer with this email already exists");
        }
        apply(customer, request, email);
        return toResponse(repository.save(customer));
    }

    private Customer find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    private void apply(Customer customer, CustomerRequest request, String email) {
        customer.setCompanyName(request.companyName().trim());
        customer.setContactName(request.contactName().trim());
        customer.setEmail(email);
        customer.setPhone(trim(request.phone()));
        customer.setCountry(trim(request.country()));
        customer.setCity(trim(request.city()));
        customer.setTimezone(trim(request.timezone()));
        customer.setIndustry(trim(request.industry()));
        customer.setCompanySize(trim(request.companySize()));
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getCompanyName(), c.getContactName(), c.getEmail(),
                c.getPhone(), c.getCountry(), c.getCity(), c.getTimezone(), c.getIndustry(),
                c.getCompanySize(), c.getStatus(), c.getCreatedAt(), c.getUpdatedAt());
    }

    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    private String trim(String value) { return value == null ? null : value.trim(); }
}
