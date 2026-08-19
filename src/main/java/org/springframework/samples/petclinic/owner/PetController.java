/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Wick Dynex
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {
private static final Logger log = LoggerFactory.getLogger(PetController.class);

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private final OwnerRepository owners;

	private final PetTypeRepository types;

	public PetController(OwnerRepository owners, PetTypeRepository types) {
		log.debug(">>> Entering PetController(owners={},types={})", owners, types);
		this.owners = owners;
		log.debug("PetController(owners={},types={}): this.owners → {}", owners, types, this.owners);
		this.types = types;
		log.debug("<<< Exiting PetController(owners={},types={})", owners, types);
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		log.debug(">>> Entering populatePetTypes()");
		log.debug("<<< Exiting populatePetTypes()");
		return this.types.findPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		log.debug(">>> Entering findOwner(ownerId={})", ownerId);
		long start = System.currentTimeMillis();
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.debug("findOwner(ownerId={}): optionalOwner → {}", ownerId, optionalOwner);
				log.info("findOwner(ownerId)={}: find query executed in {} ms", ownerId, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting findOwner(ownerId={})", ownerId);
		return owner;
	}

	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {
			log.debug(">>> Entering findPet(ownerId={})", ownerId);

		if (petId == null) {
			return new Pet();
		}

		long start = System.currentTimeMillis();
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.debug("findPet(ownerId={}): optionalOwner → {}", ownerId, optionalOwner);
				log.info("findPet(ownerId)={}: find query executed in {} ms", ownerId, (System.currentTimeMillis() - start));
		log.debug("<<< Exiting findPet(ownerId={})", ownerId);
		return owner.getPet(petId);
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		log.debug(">>> Entering initOwnerBinder(dataBinder={})", dataBinder);
		dataBinder.setDisallowedFields("id", "*.id");
		log.debug("<<< Exiting initOwnerBinder(dataBinder={})", dataBinder);
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		log.debug(">>> Entering initPetBinder(dataBinder={})", dataBinder);
		log.debug("initPetBinder(dataBinder={}): dataBinder → {}", dataBinder, dataBinder);
		dataBinder.setValidator(new PetValidator());
		dataBinder.setDisallowedFields("id", "*.id");
		log.debug("<<< Exiting initPetBinder(dataBinder={})", dataBinder);
	}

	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		log.debug(">>> Entering initCreationForm(owner={},model={})", owner, model);
		Pet pet = new Pet();
		owner.addPet(pet);
		log.debug("initCreationForm(owner={},model={}): pet → {}", owner, model, pet);
		log.debug("<<< Exiting initCreationForm(owner={},model={})", owner, model);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
			log.debug(">>> Entering processCreationForm(owner={},pet={},result={},redirectAttributes={})", owner, pet, result, redirectAttributes);

		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			result.rejectValue("name", "duplicate", "already exists");
		}

		LocalDate currentDate = LocalDate.now();
		log.debug("processCreationForm(owner={},pet={},result={},redirectAttributes={}): currentDate → {}", owner, pet, result, redirectAttributes, currentDate);
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		try {
			owner.addPet(pet);
			this.owners.saveAndFlush(owner);
		}
		catch (DataIntegrityViolationException ex) {
			log.error("Exception in processCreationForm(owner={},pet={},result={},redirectAttributes={}): {}", owner, pet, result, redirectAttributes, ex.getMessage(), ex);
			if (!isDuplicatePetNameViolation(ex)) {
				throw ex;
			}
			result.rejectValue("name", "duplicate", "already exists");
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		log.debug("<<< Exiting processCreationForm(owner={},pet={},result={},redirectAttributes={})", owner, pet, result, redirectAttributes);
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm() {
		log.debug(">>> Entering initUpdateForm()");
		log.debug("<<< Exiting initUpdateForm()");
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(Owner owner, @Valid Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
			log.debug(">>> Entering processUpdateForm(owner={},pet={},result={},redirectAttributes={})", owner, pet, result, redirectAttributes);

		String petName = pet.getName();
		log.debug("processUpdateForm(owner={},pet={},result={},redirectAttributes={}): petName → {}", owner, pet, result, redirectAttributes, petName);

		// checking if the pet name already exists for the owner
		if (StringUtils.hasText(petName)) {
			Pet existingPet = owner.getPet(petName, false);
			log.debug("processUpdateForm(owner={},pet={},result={},redirectAttributes={}): existingPet → {}", owner, pet, result, redirectAttributes, existingPet);
			if (existingPet != null && !Objects.equals(existingPet.getId(), pet.getId())) {
				result.rejectValue("name", "duplicate", "already exists");
			}
		}

		LocalDate currentDate = LocalDate.now();
		log.debug("processUpdateForm(owner={},pet={},result={},redirectAttributes={}): currentDate → {}", owner, pet, result, redirectAttributes, currentDate);
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}

		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		try {
			updatePetDetails(owner, pet);
		}
		catch (DataIntegrityViolationException ex) {
			log.error("Exception in processUpdateForm(owner={},pet={},result={},redirectAttributes={}): {}", owner, pet, result, redirectAttributes, ex.getMessage(), ex);
			if (!isDuplicatePetNameViolation(ex)) {
				throw ex;
			}
			result.rejectValue("name", "duplicate", "already exists");
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
		log.debug("<<< Exiting processUpdateForm(owner={},pet={},result={},redirectAttributes={})", owner, pet, result, redirectAttributes);
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Updates the pet details if it exists or adds a new pet to the owner.
	 * @param owner The owner of the pet
	 * @param pet The pet with updated details
	 */
	private void updatePetDetails(Owner owner, Pet pet) {
		log.debug(">>> Entering updatePetDetails(owner={},pet={})", owner, pet);
		Integer id = pet.getId();
		Assert.state(id != null, "'pet.getId()' must not be null");
		log.debug("updatePetDetails(owner={},pet={}): id → {}", owner, pet, id);
		Pet existingPet = owner.getPet(id);
		if (existingPet != null) {
			// Update existing pet's properties
			existingPet.setName(pet.getName());
			log.debug("updatePetDetails(owner={},pet={}): existingPet → {}", owner, pet, existingPet);
			existingPet.setBirthDate(pet.getBirthDate());
			existingPet.setType(pet.getType());
		}
		else {
			owner.addPet(pet);
		}
		this.owners.saveAndFlush(owner);
		log.debug("<<< Exiting updatePetDetails(owner={},pet={})", owner, pet);
	}

	private boolean isDuplicatePetNameViolation(DataIntegrityViolationException ex) {
		log.debug(">>> Entering isDuplicatePetNameViolation(ex={})", ex);
		String message = ex.getMessage();
		log.debug("isDuplicatePetNameViolation(ex={}): message → {}", ex, message);
		log.debug("<<< Exiting isDuplicatePetNameViolation(ex={})", ex);
		return message != null && message.toLowerCase().contains("unique_owner_pet_name");
	}

}
