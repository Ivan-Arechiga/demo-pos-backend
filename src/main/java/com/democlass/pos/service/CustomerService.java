package com.democlass.pos.service;

import com.democlass.pos.dto.CreateCustomerRequest;
import com.democlass.pos.dto.CustomerDTO;
import com.democlass.pos.dto.UpdateCustomerRequest;
import com.democlass.pos.entity.Customer;
import com.democlass.pos.exception.DuplicateEmailException;
import com.democlass.pos.exception.EntityNotFoundException;
import com.democlass.pos.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        return toDTO(customer);
    }

    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        // Check if email already exists
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);
        return toDTO(savedCustomer);
    }

    public CustomerDTO updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));

        // Check email uniqueness if email is being updated
        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail())) {
            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateEmailException(request.getEmail());
            }
            customer.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            customer.setName(request.getName());
        }

        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }

        if (request.getStatus() != null) {
            customer.setStatus(Customer.CustomerStatus.valueOf(request.getStatus()));
        }

        customer.setUpdatedAt(LocalDateTime.now());
        Customer updatedCustomer = customerRepository.save(customer);
        return toDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        
        // Soft delete: set status to INACTIVE
        customer.setStatus(Customer.CustomerStatus.INACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    private CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getPhone(),
            customer.getStatus().toString(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
