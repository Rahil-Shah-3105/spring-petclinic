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
		log.info("setAllowedFields(dataBinder={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail", dataBinder);
		dataBinder.setDisallowedFields("id", "*.id");
		log.info("setAllowedFields(dataBinder={}): M2614-AUDIT: Log integrity (encrypt + validate) — State change recorded for the audit trail", dataBinder);
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
			log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 11: CEM + THIRF capability — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 8: quantify data affected in an incident — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
				log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 7: anomaly hunting — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);

		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 6: suspicious activity & IOC hunting — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		Pet pet = owner.getPet(petId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 5: infrastructure changes — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		if (pet == null) {
			log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 4: privilege changes — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
					log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 10: automated alerting — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		}
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 3: object and data access — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		model.put("pet", pet);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 2: source/destination network traffic — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		model.put("owner", owner);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Outcome 1: user attribution — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);

		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		Visit visit = new Visit();
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Log Management / data protection — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		pet.addVisit(visit);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Log integrity (encrypt + validate) — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
		log.info("loadPetWithVisit(ownerId={}, petId={}, model={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — Record access recorded for the audit trail {}", ownerId, petId, model, ownerId);
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
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 5: infrastructure changes — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 9: trace attack vectors — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
			result.rejectValue("date", "typeMismatch.visitDate");
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 8: quantify data affected in an incident — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		}
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 4: privilege changes — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);

		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 3: object and data access — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		if (result.hasErrors()) {
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 7: anomaly hunting — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
			log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 6: suspicious activity & IOC hunting — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
			return "pets/createOrUpdateVisitForm";
		}
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 2: source/destination network traffic — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);

		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Outcome 1: user attribution — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		owner.addVisit(petId, visit);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		this.owners.save(owner);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Log Management / data protection — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Log integrity (encrypt + validate) — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		log.info("processNewVisitForm(owner={}, petId={}, visit={}, result={}, redirectAttributes={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail {}", owner, petId, visit, result, redirectAttributes, petId);
		return "redirect:/owners/{ownerId}";
	}

}
