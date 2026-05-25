package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.service.CustomerService;
import com.udacity.jdnd.course3.critter.service.PetService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */

@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final CustomerService customerService;

    public PetController(PetService petService, CustomerService customerService) {
        this.petService = petService;
        this.customerService = customerService;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {

        Pet pet = new Pet();

        pet.setName(petDTO.getName());
        pet.setNotes(petDTO.getNotes());
        pet.setType(petDTO.getType());
        pet.setBirthDate(petDTO.getBirthDate());

        Customer owner = customerService
                .getCustomerById(petDTO.getOwnerId())
                .orElseThrow();

        pet.setOwner(owner);

        if (owner.getPets() == null) {
            owner.setPets(new java.util.ArrayList<>());
        }
        owner.getPets().add(pet);
        var saved = petService.save(pet);

        return convertPetEntityToDTO(saved);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {

        var pet = petService.getPet(petId).orElseThrow();

        return convertPetEntityToDTO(pet);
    }

    @GetMapping
    public List<PetDTO> getPets() {

        return petService.getAllPets().stream().map(this::convertPetEntityToDTO).toList();
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {

        return customerService.getPetsByOwner(ownerId)
                .stream()
                .map(this::convertPetEntityToDTO)
                .toList();
    }

    private PetDTO convertPetEntityToDTO(Pet pet) {

        var dto = new PetDTO();

        dto.setBirthDate(pet.getBirthDate());
        dto.setNotes(pet.getNotes());
        dto.setName(pet.getName());
        dto.setId(pet.getId());
        dto.setType(pet.getType());

        if (pet.getOwner() != null) {
            dto.setOwnerId(pet.getOwner().getId());
        }

        return dto;
    }
}