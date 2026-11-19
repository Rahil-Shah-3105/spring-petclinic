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
		log.error(">>> Entering OwnerController(owners={})", owners);
		this.owners = owners;
		log.debug("OwnerController(owners={}): this.owners → {}", owners, this.owners);
		log.warn("<<< Exiting OwnerController(owners={})", owners);
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		log.error(">>> Entering setAllowedFields(dataBinder={})", dataBinder);
		dataBinder.setDisallowedFields("id", "*.id");
		log.warn("<<< Exiting setAllowedFields(dataBinder={})", dataBinder);
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		log.error(">>> Entering findOwner(ownerId={})", ownerId);
		Owner result = ownerId == null ? new Owner()
				: this.owners.findById(ownerId)
					.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId
							+ ". Please ensure the ID is correct " + "and the owner exists in the database."));
		log.warn("<<< Exiting findOwner(ownerId={}) -> {}", ownerId, result);
		return result;
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		log.error(">>> Entering initCreationForm()");
		log.warn("<<< Exiting initCreationForm()");
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result, RedirectAttributes redirectAttributes) {
		log.error(">>> Entering processCreationForm(owner={},result={},redirectAttributes={})", owner, result, redirectAttributes);
		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute("error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		String logriteResult = "redirect:/owners/" + owner.getId();
		log.warn("<<< Exiting processCreationForm(owner={},result={},redirectAttributes={}) -> {}", owner, result, redirectAttributes, logriteResult);
		return logriteResult;
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		log.error(">>> Entering initFindForm()");
		log.warn("<<< Exiting initFindForm()");
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, BindingResult result,
			Model model) {
			log.error(">>> Entering processFindForm(page={},owner={},result={},model={})", page, owner, result, model);
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
			log.error("processFindForm(page={},owner={},result={},model={}): owner → {}", page, owner, result, model, owner);
			return "redirect:/owners/" + owner.getId();
		}

		// multiple owners found
		String logriteResult = addPaginationModel(page, model, ownersResults);
		log.warn("<<< Exiting processFindForm(page={},owner={},result={},model={}) -> {}", page, owner, result, model, logriteResult);
		return logriteResult;
	}

	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		log.error(">>> Entering addPaginationModel(page={},model={},paginated={})", page, model, paginated);
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		log.error("addPaginationModel(page={},model={},paginated={}): listOwners → {}", page, model, paginated, listOwners);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		log.warn("<<< Exiting addPaginationModel(page={},model={},paginated={})", page, model, paginated);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname) {
		log.error(">>> Entering findPaginatedForOwnersLastName(page={},lastname=<redacted>)", page);
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		log.error("findPaginatedForOwnersLastName(page={},lastname=<redacted>): pageable → {}", page, pageable);
		Page<Owner> result = owners.findByLastNameStartingWith(lastname, pageable);
		log.warn("<<< Exiting findPaginatedForOwnersLastName(page={},lastname=<redacted>)", page);
		return result;
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		log.error(">>> Entering initUpdateOwnerForm()");
		log.warn("<<< Exiting initUpdateOwnerForm()");
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId,
			RedirectAttributes redirectAttributes) {
			log.error(">>> Entering processUpdateOwnerForm(owner={},result={},ownerId={},redirectAttributes={})", owner, result, ownerId, redirectAttributes);
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
		log.debug("processUpdateOwnerForm(owner={},result={},ownerId={},redirectAttributes={}): owner → {}", owner, result, ownerId, redirectAttributes, owner);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Owner Values Updated");
		log.warn("<<< Exiting processUpdateOwnerForm(owner={},result={},ownerId={},redirectAttributes={})", owner, result, ownerId, redirectAttributes);
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		log.error(">>> Entering showOwner(ownerId={})", ownerId);
		long start = System.currentTimeMillis();
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		log.error("showOwner(ownerId)={}: find query executed in {} ms", ownerId, (System.currentTimeMillis() - start));
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.error("showOwner(ownerId={}): mav → {}", ownerId, mav);
				log.error("showOwner(ownerId={}): optionalOwner → {}", ownerId, optionalOwner);
		mav.addObject(owner);
		log.warn("<<< Exiting showOwner(ownerId={})", ownerId);
		return mav;
	}

}
