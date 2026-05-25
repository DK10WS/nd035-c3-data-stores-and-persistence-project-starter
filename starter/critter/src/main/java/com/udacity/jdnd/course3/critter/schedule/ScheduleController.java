package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entity.*;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.EmployeeService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Schedules.
 */

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final PetService petService;
    private final CustomerService customerService;

    public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService, PetService petService, CustomerService customerService) {
        this.scheduleService = scheduleService;
        this.employeeService = employeeService;
        this.petService = petService;
        this.customerService = customerService;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {

        Schedule schedule = new Schedule();

        schedule.setDate(scheduleDTO.getDate());
        schedule.setActivities(scheduleDTO.getActivities());

        var employees = scheduleDTO.getEmployeeIds()
                .stream()
                .map(id -> employeeService.getEmployee(id).orElseThrow())
                .toList();

        List<Pet> pets = new ArrayList<>();

        for (Long petId : scheduleDTO.getPetIds()) {
            Pet pet = petService.getPet(petId).orElseThrow();

            pets.add(pet);
        }

        schedule.setEmployees(employees);
        schedule.setPets(pets);

        for (var pet : pets) {

            if (pet.getSchedules() == null) {
                pet.setSchedules(new java.util.ArrayList<>());
            }

            pet.getSchedules().add(schedule);
        }

        Schedule saved = scheduleService.save(schedule);
        return convertScheduleEntityToDTO(saved);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {

        return scheduleService.getAllSchedules().stream()
                .map(this::convertScheduleEntityToDTO)
                .toList();
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(
            @PathVariable long petId
    ) {

        return scheduleService.getSchedulesForPet(petId)
                .stream()
                .map(this::convertScheduleEntityToDTO)
                .toList();
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(
            @PathVariable long employeeId
    ) {

        return scheduleService.getSchedulesForEmployee(employeeId)
                .stream()
                .map(this::convertScheduleEntityToDTO)
                .toList();
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(
            @PathVariable long customerId
    ) {

        Customer customer = customerService.getCustomerById(customerId).orElseThrow();

        var petIds = customer.getPets()
                .stream()
                .map(Pet::getId)
                .toList();

        return scheduleService.getAllSchedules()
                .stream()
                .filter(schedule ->
                        schedule.getPets()
                                .stream()
                                .anyMatch(pet ->
                                        petIds.contains(pet.getId())))
                .map(this::convertScheduleEntityToDTO)
                .toList();
    }

    private ScheduleDTO convertScheduleEntityToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setActivities(schedule.getActivities());

        List<Long> employeeIds = new ArrayList<>();

        for (Employee employee : schedule.getEmployees()) {
            employeeIds.add(employee.getId());
        }
        dto.setEmployeeIds(employeeIds);

        List<Long> petIds = new ArrayList<>();

        for (Pet pet : schedule.getPets()) {
            petIds.add(pet.getId());
        }

        dto.setPetIds(petIds);

        return dto;
    }}