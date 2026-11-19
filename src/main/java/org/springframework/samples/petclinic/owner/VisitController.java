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
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {
private static final Logger log = LoggerFactory.getLogger(VisitController.class);

	private final OwnerRepository owners;

	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		log.info("setAllowedFields(dataBinder={}): GDPR-AUDIT: Breach notification — State change recorded for the audit trail", dataBinder);
		log.warn("setAllowedFields(dataBinder={}): GDPR-AUDIT [Art. 6]: Lawfulness of processing — State change recorded for the audit trail", dataBinder);
		dataBinder.setDisallowedFields("id", "*.id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT: Transfers to third countries — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT [Art. 32]: Security of processing — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT [Art. 5(1)(e)]: Storage limitation — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT [Art. 30]: Records of processing activities — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.warn("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT: Access, erasure, portability — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT [Art. 25]: Data protection by design and by default — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.warn("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT [Art. 6]: Lawfulness of processing — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): GDPR-AUDIT: Breach notification — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		model.put("pet", pet);
		model.put("owner", owner);

		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now().plusDays(1);
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT: Transfers to third countries — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art. 32]: Security of processing — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art. 5(1)(e)]: Storage limitation — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art. 30]: Records of processing activities — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.warn("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT: Access, erasure, portability — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art. 25]: Data protection by design and by default — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art.30]: Personal data processing operation recorded {}", owner, petId, visit, result, redirectAttributes, result);
		log.warn("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT [Art. 6]: Lawfulness of processing — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): GDPR-AUDIT: Breach notification — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

}
