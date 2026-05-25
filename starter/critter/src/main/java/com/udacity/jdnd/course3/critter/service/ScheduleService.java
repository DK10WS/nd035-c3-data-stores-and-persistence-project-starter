package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final PetRepository petRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           EmployeeRepository employeeRepository,
                           PetRepository petRepository) {
        this.scheduleRepository = scheduleRepository;
        this.employeeRepository = employeeRepository;
        this.petRepository = petRepository;
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getSchedulesForEmployee(Long employeeId) {

        return scheduleRepository.findAll()
                .stream().filter(schedule ->
                        schedule.getEmployees().stream().anyMatch(
                                employee -> employee.getId().equals(employeeId)))
                .toList();
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesForPet(Long petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow();

        return pet.getSchedules();
    }

    public List<Employee> findEmployeesForService(
            Set<com.udacity.jdnd.course3.critter.user.EmployeeSkill> skills,
            DayOfWeek day
    ) {

        return employeeRepository.findAll()
                .stream()
                .filter(employee ->
                        employee.getDaysAvailable().contains(day))
                .filter(employee ->
                        employee.getSkills().containsAll(skills))
                .collect(Collectors.toList());
    }
}