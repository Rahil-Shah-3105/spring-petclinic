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
		this.owners = owners;
		this.types = types;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.types.findPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		log.info("findOwner(ownerId={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — Record access recorded for the audit trail {}", ownerId, ownerId);
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		log.info("findOwner(ownerId={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — Record access recorded for the audit trail {}", ownerId, ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.info("findOwner(ownerId={}): M2614-AUDIT: Log Management / data protection — Record access recorded for the audit trail {}", ownerId, ownerId);
		log.info("findOwner(ownerId={}): M2614-AUDIT: Log integrity (encrypt + validate) — Record access recorded for the audit trail {}", ownerId, ownerId);
		return owner;
	}

	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {
			log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Outcome 4: privilege changes — Record access recorded for the audit trail {}", ownerId, petId, ownerId);

		log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Outcome 3: object and data access — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
		if (petId == null) {
			log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Outcome 5: infrastructure changes — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
			log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
			return new Pet();
		}
		log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Outcome 2: source/destination network traffic — Record access recorded for the audit trail {}", ownerId, petId, ownerId);

		log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Outcome 1: user attribution — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Log Management / data protection — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
		log.info("findPet(ownerId={}, petId={}): M2614-AUDIT: Log integrity (encrypt + validate) — Record access recorded for the audit trail {}", ownerId, petId, ownerId);
		return owner.getPet(petId);
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
		dataBinder.setDisallowedFields("id", "*.id");
	}

	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
			log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 8: quantify data affected in an incident — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 7: anomaly hunting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
			result.rejectValue("name", "duplicate", "already exists");
		}
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 6: suspicious activity & IOC hunting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 5: infrastructure changes — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		LocalDate currentDate = LocalDate.now();
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 4: privilege changes — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 3: object and data access — Database transaction completed with status {}", owner, pet, result, redirectAttributes, result);

		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 2: source/destination network traffic — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 1: user attribution — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		try {
			owner.addPet(pet);
			this.owners.saveAndFlush(owner);
		}
		catch (DataIntegrityViolationException ex) {
			log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Log Management / data protection — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			if (!isDuplicatePetNameViolation(ex)) {
				log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Retention: 6 months searchable / 12 months retrievable — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
				throw ex;
			}
			log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 11: CEM + THIRF capability — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			result.rejectValue("name", "duplicate", "already exists");
			log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 10: automated alerting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 9: trace attack vectors — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Log integrity (encrypt + validate) — Protected data integrity verification completed {}", owner, pet, result, redirectAttributes, result);
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", owner, pet, result, redirectAttributes, result);
		log.info("processCreationForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm() {
		log.info("initUpdateForm(): M2614-AUDIT: Outcome 5: infrastructure changes — Change management operation performed");
		log.info("initUpdateForm(): SOC2-AUDIT [CC8.1]: Change management operation performed");
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(Owner owner, @Valid Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
			log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 10: automated alerting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 9: trace attack vectors — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		String petName = pet.getName();
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 8: quantify data affected in an incident — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 7: anomaly hunting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		// checking if the pet name already exists for the owner
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 6: suspicious activity & IOC hunting — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		if (StringUtils.hasText(petName)) {
			Pet existingPet = owner.getPet(petName, false);
			if (existingPet != null && !Objects.equals(existingPet.getId(), pet.getId())) {
				result.rejectValue("name", "duplicate", "already exists");
			}
		}
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 5: infrastructure changes — Change management operation performed", owner, pet, result, redirectAttributes);

		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 4: privilege changes — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		LocalDate currentDate = LocalDate.now();
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 3: object and data access — Database transaction completed with status {}", owner, pet, result, redirectAttributes, result);
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 2: source/destination network traffic — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 1: user attribution — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);

		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Log Management / data protection — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		try {
			updatePetDetails(owner, pet);
		}
		catch (DataIntegrityViolationException ex) {
			log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Log integrity (encrypt + validate) — Protected data integrity verification completed {}", owner, pet, result, redirectAttributes, result);
			if (!isDuplicatePetNameViolation(ex)) {
				throw ex;
			}
			result.rejectValue("name", "duplicate", "already exists");
			log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Retention: 6 months searchable / 12 months retrievable — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 11: CEM + THIRF capability — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): SOC2-AUDIT [CC6.6]: Database transaction completed with status {}", owner, pet, result, redirectAttributes, result);
		redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): SOC2-AUDIT [CC8.1]: Change management operation performed", owner, pet, result, redirectAttributes);
		log.info("processUpdateForm(owner={}, pet={}, result={}, redirectAttributes={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail {}", owner, pet, result, redirectAttributes, result);
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Updates the pet details if it exists or adds a new pet to the owner.
	 * @param owner The owner of the pet
	 * @param pet The pet with updated details
	 */
	private void updatePetDetails(Owner owner, Pet pet) {
		log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 5: infrastructure changes — Change management operation performed", owner, pet);
		Integer id = pet.getId();
		log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 1: user attribution — State change recorded for the audit trail {}", owner, pet, id);
		Assert.state(id != null, "'pet.getId()' must not be null");
		log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — State change recorded for the audit trail {}", owner, pet, id);
		Pet existingPet = owner.getPet(id);
		log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Log Management / data protection — State change recorded for the audit trail {}", owner, pet, id);
		if (existingPet != null) {
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 10: automated alerting — State change recorded for the audit trail {}", owner, pet, id);
			// Update existing pet's properties
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 9: trace attack vectors — State change recorded for the audit trail {}", owner, pet, id);
			existingPet.setName(pet.getName());
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 8: quantify data affected in an incident — State change recorded for the audit trail {}", owner, pet, id);
			existingPet.setBirthDate(pet.getBirthDate());
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 4: privilege changes — State change recorded for the audit trail {}", owner, pet, id);
			existingPet.setType(pet.getType());
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 3: object and data access — State change recorded for the audit trail {}", owner, pet, id);
		}
		else {
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Log integrity (encrypt + validate) — State change recorded for the audit trail {}", owner, pet, id);
			owner.addPet(pet);
			log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Outcome 2: source/destination network traffic — State change recorded for the audit trail {}", owner, pet, id);
		}
		log.info("updatePetDetails(owner={}, pet={}): SOC2-AUDIT [CC8.1]: Change management operation performed", owner, pet);
		this.owners.saveAndFlush(owner);
		log.info("updatePetDetails(owner={}, pet={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail {}", owner, pet, id);
	}

	private boolean isDuplicatePetNameViolation(DataIntegrityViolationException ex) {
		String message = ex.getMessage();
		log.info("isDuplicatePetNameViolation(ex={}): M2614-AUDIT: Log integrity (encrypt + validate) — Data integrity check performed", ex);
		log.info("isDuplicatePetNameViolation(ex={}): SOC2-AUDIT [CC6.6]: Database transaction completed", ex);
		return message != null && message.toLowerCase().contains("unique_owner_pet_name");
	}

}
