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
		log.debug(">>> Entering VetController(vetRepository={})", vetRepository);
		this.vetRepository = vetRepository;
		log.debug("<<< Exiting VetController(vetRepository={})", vetRepository);
	}

	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		log.debug(">>> Entering showVetList(model={})", model);
		Page<Vet> paginated = findPaginated(page);
		log.debug("showVetList(model={}): paginated → {}", model, paginated);
		log.debug("<<< Exiting showVetList(model={})", model);
		return addPaginationModel(page, paginated, model);
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		log.debug(">>> Entering addPaginationModel(page={},paginated={},model={})", page, paginated, model);
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		log.debug("addPaginationModel(page={},paginated={},model={}): listVets → {}", page, paginated, model, listVets);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		log.debug("<<< Exiting addPaginationModel(page={},paginated={},model={})", page, paginated, model);
		return "vets/vetList";
	}

	private Page<Vet> findPaginated(int page) {
		log.debug(">>> Entering findPaginated(page={})", page);
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		log.debug("findPaginated(page={}): pageable → {}", page, pageable);
		log.debug("<<< Exiting findPaginated(page={})", page);
		return vetRepository.findAll(pageable);
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		log.debug(">>> Entering showResourcesVetList()");
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		log.debug("showResourcesVetList(): vets → {}", vets);
		vets.getVetList().addAll(this.vetRepository.findAll());
		log.debug("<<< Exiting showResourcesVetList()");
		return vets;
	}

}
