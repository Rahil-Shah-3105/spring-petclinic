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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Wick Dynex
 */
@Controller
class OwnerController {
private static final Logger log = LoggerFactory.getLogger(OwnerController.class);

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	public OwnerController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		log.info("setAllowedFields(dataBinder={}): CCPA-AUDIT: Admt decision — State change recorded for the audit trail", dataBinder);
		dataBinder.setDisallowedFields("id", "*.id");
		log.info("setAllowedFields(dataBinder={}): CCPA-AUDIT: Consumer rights — State change recorded for the audit trail", dataBinder);
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		log.info("findOwner(ownerId={}): CCPA-AUDIT: Admt decision — Record access recorded for the audit trail {}", ownerId, ownerId);
		log.info("findOwner(ownerId={}): CCPA-AUDIT: Consumer rights — Record access recorded for the audit trail {}", ownerId, ownerId);
		return ownerId == null ? new Owner()
				: this.owners.findById(ownerId)
					.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId
							+ ". Please ensure the ID is correct " + "and the owner exists in the database."));
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		log.info("processCreationForm(owner={}, result={}, redirectAttributes={}): CCPA-AUDIT: Risk assessment — State change recorded for the audit trail {}", owner, result, redirectAttributes, result);
		this.owners.save(owner);
		log.info("processCreationForm(owner={}, result={}, redirectAttributes={}): CCPA-AUDIT: Cyber audit — State change recorded for the audit trail {}", owner, result, redirectAttributes, result);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		log.info("processCreationForm(owner={}, result={}, redirectAttributes={}): CCPA-AUDIT: Consumer rights — State change recorded for the audit trail {}", owner, result, redirectAttributes, result);
		log.info("processCreationForm(owner={}, result={}, redirectAttributes={}): CCPA-AUDIT: Admt decision — State change recorded for the audit trail {}", owner, result, redirectAttributes, result);
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
		// allow parameterless GET request for /owners to return all records
		String lastName = owner.getLastName();
		if (lastName == null) {
			lastName = ""; // empty string signifies broadest possible search
		}
		else {
			lastName = lastName.strip();
		}

		// find owners by last name
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, lastName);
		if (ownersResults.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			// 1 owner found
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}

		// multiple owners found
		return addPaginationModel(page, model, ownersResults);
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		log.info("addPaginationModel(page={}, model={}, paginated={}): CCPA-AUDIT: Risk assessment — State change recorded for the audit trail", page, model, paginated);
		model.addAttribute("totalItems", paginated.getTotalElements());
		log.info("addPaginationModel(page={}, model={}, paginated={}): CCPA-AUDIT: Cyber audit — State change recorded for the audit trail", page, model, paginated);
		model.addAttribute("listOwners", listOwners);
		log.info("addPaginationModel(page={}, model={}, paginated={}): CCPA-AUDIT: Consumer rights — State change recorded for the audit trail", page, model, paginated);
		log.info("addPaginationModel(page={}, model={}, paginated={}): CCPA-AUDIT: Admt decision — State change recorded for the audit trail", page, model, paginated);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		log.info("findPaginatedForOwnersLastName(page={}, lastname=<redacted>): CCPA-AUDIT: Risk assessment — Record access recorded for the audit trail", page);
		int pageSize = 5;
		log.info("findPaginatedForOwnersLastName(page={}, lastname=<redacted>): CCPA-AUDIT: Cyber audit — Record access recorded for the audit trail", page);
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		log.info("findPaginatedForOwnersLastName(page={}, lastname=<redacted>): CCPA-AUDIT: Consumer rights — Record access recorded for the audit trail", page);
		log.info("findPaginatedForOwnersLastName(page={}, lastname=<redacted>): CCPA-AUDIT: Admt decision — Record access recorded for the audit trail", page);
		return owners.findByLastNameStartingWith(lastname, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (!Objects.equals(owner.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		owner.setId(ownerId);
		log.info("processUpdateOwnerForm(owner={}, result={}, ownerId={}, redirectAttributes={}): CCPA-AUDIT: Risk assessment — State change recorded for the audit trail {}", owner, result, ownerId, redirectAttributes, result);
		this.owners.save(owner);
		log.info("processUpdateOwnerForm(owner={}, result={}, ownerId={}, redirectAttributes={}): CCPA-AUDIT: Cyber audit — State change recorded for the audit trail {}", owner, result, ownerId, redirectAttributes, result);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		log.info("processUpdateOwnerForm(owner={}, result={}, ownerId={}, redirectAttributes={}): CCPA-AUDIT: Consumer rights — State change recorded for the audit trail {}", owner, result, ownerId, redirectAttributes, result);
		log.info("processUpdateOwnerForm(owner={}, result={}, ownerId={}, redirectAttributes={}): CCPA-AUDIT: Admt decision — State change recorded for the audit trail {}", owner, result, ownerId, redirectAttributes, result);
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		log.info("showOwner(ownerId={}): CCPA-AUDIT: Admt decision — State change recorded for the audit trail {}", ownerId, ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.info("showOwner(ownerId={}): CCPA-AUDIT: Risk assessment — State change recorded for the audit trail {}", ownerId, ownerId);
		mav.addObject(owner);
		log.info("showOwner(ownerId={}): CCPA-AUDIT: Cyber audit — State change recorded for the audit trail {}", ownerId, ownerId);
		log.info("showOwner(ownerId={}): CCPA-AUDIT: Consumer rights — State change recorded for the audit trail {}", ownerId, ownerId);
		return mav;
	}

}
