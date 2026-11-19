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
package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {
private static final Logger log = LoggerFactory.getLogger(VetController.class);

	private final VetRepository vetRepository;

	public VetController(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<Vet> paginated = findPaginated(page);
		return addPaginationModel(page, paginated, model);
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		log.info("addPaginationModel(page={}, paginated={}, model={}): FERPA-AUDIT: Inspect amend — State change recorded for the audit trail", page, paginated, model);
		model.addAttribute("totalItems", paginated.getTotalElements());
		log.info("addPaginationModel(page={}, paginated={}, model={}): FERPA-AUDIT [34 CFR 99.32]: Disclosure log — State change recorded for the audit trail", page, paginated, model);
		model.addAttribute("listVets", listVets);
		log.info("addPaginationModel(page={}, paginated={}, model={}): FERPA-AUDIT [34 CFR 99.37]: Directory information — State change recorded for the audit trail", page, paginated, model);
		log.info("addPaginationModel(page={}, paginated={}, model={}): FERPA-AUDIT [34 CFR 99.30]: Consent — State change recorded for the audit trail", page, paginated, model);
		return "vets/vetList";
	}

	private Page<Vet> findPaginated(int page) {
		log.info("findPaginated(page={}): FERPA-AUDIT: Inspect amend — Record access recorded for the audit trail", page);
		int pageSize = 5;
		log.info("findPaginated(page={}): FERPA-AUDIT [34 CFR 99.32]: Disclosure log — Record access recorded for the audit trail", page);
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		log.info("findPaginated(page={}): FERPA-AUDIT [34 CFR 99.37]: Directory information — Record access recorded for the audit trail", page);
		log.info("findPaginated(page={}): FERPA-AUDIT [34 CFR 99.30]: Consent — Record access recorded for the audit trail", page);
		return vetRepository.findAll(pageable);
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		log.info("showResourcesVetList(): FERPA-AUDIT: Inspect amend — State change recorded for the audit trail");
		log.info("showResourcesVetList(): FERPA-AUDIT [34 CFR 99.32]: Disclosure log — State change recorded for the audit trail");
		log.info("showResourcesVetList(): FERPA-AUDIT [34 CFR 99.37]: Directory information — State change recorded for the audit trail");
		log.info("showResourcesVetList(): FERPA-AUDIT [34 CFR 99.30]: Consent — State change recorded for the audit trail");
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}

}
