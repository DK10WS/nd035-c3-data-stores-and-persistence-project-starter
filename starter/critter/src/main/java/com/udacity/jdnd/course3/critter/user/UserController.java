package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final CustomerService customerService;
    private final EmployeeService employeeService;

    public UserController(CustomerService customerService,
                          EmployeeService employeeService) {
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customer) {

        var c = new Customer();

        c.setName(customer.getName());
        c.setPhoneNumber(customer.getPhoneNumber());
        c.setNotes(customer.getNotes());

        var saved = customerService.save(c);

        return convertCustomerEntityToDTO(saved);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers() {

        return customerService.getAllCustomers()
                .stream()
                .map(this::convertCustomerEntityToDTO)
                .toList();
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId) {

        var customer = customerService.getOwnerByPet(petId);

        return convertCustomerEntityToDTO(customer);
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {

        var employee = new Employee();

        employee.setName(employeeDTO.getName());
        employee.setSkills(employeeDTO.getSkills());
        employee.setAvailable(employeeDTO.getDaysAvailable());

        var saved = employeeService.save(employee);

        return convertEmployeeEntityToDTO(saved);
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {

        var employee = employeeService
                .getEmployee(employeeId)
                .orElseThrow();

        return convertEmployeeEntityToDTO(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        employeeService.setAvailability(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO requestDTO) {

        return employeeService.findEmployeesForService(requestDTO.getSkills(), requestDTO.getDate().getDayOfWeek())
                .stream()
                .map(this::convertEmployeeEntityToDTO)
                .toList();
    }

    private CustomerDTO convertCustomerEntityToDTO(Customer customer) {

        CustomerDTO dto = new CustomerDTO();

        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setNotes(customer.getNotes());

        if (customer.getPets() != null) {
            dto.setPetIds(customer.getPets().stream().map(Pet::getId).toList());
        }

        return dto;
    }
    // The DTO is not directly accesses
    private EmployeeDTO convertEmployeeEntityToDTO(Employee employee) {

        var dto = new EmployeeDTO();

        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSkills(employee.getSkills());
        dto.setDaysAvailable(employee.getDaysAvailable());

        return dto;
    }
}