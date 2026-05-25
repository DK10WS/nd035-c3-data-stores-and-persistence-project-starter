package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Employee;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.entity.Schedule;
import com.udacity.jdnd.course3.critter.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final PetRepository petRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, EmployeeRepository employeeRepository, PetRepository petRepository) {
        this.scheduleRepository = scheduleRepository;
        this.employeeRepository = employeeRepository;
        this.petRepository = petRepository;
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getSchedulesForEmployee(Long employeeId) {
        List<Schedule> matchedSchedules = new ArrayList<>();
        for (Schedule schedule : scheduleRepository.findAll()) {
            for (Employee employee : schedule.getEmployees()) {
                if (employee.getId().equals(employeeId)) {
                    matchedSchedules.add(schedule);
                    break;
                }
            }
        }

        return matchedSchedules;
    }
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesForPet(Long petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow();

        return pet.getSchedules();
    }

    public List<Employee> findEmployeesForService(Set<EmployeeSkill> skills, DayOfWeek day) {

        List<Employee> employees = employeeRepository.findAll();
        List<Employee> matchedEmployees = new ArrayList<>();

        for (Employee employee : employees) {

            boolean available = employee.getDaysAvailable().contains(day);
            boolean hasSkills = employee.getSkills().containsAll(skills);

            if (available && hasSkills) {
                matchedEmployees.add(employee);
            }
        }

        return matchedEmployees;
    }}