package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;

    public CustomerService(CustomerRepository customerRepository,
                           PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
    public Customer getOwnerByPet(Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow();
        return pet.getOwner();
    }
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }
}