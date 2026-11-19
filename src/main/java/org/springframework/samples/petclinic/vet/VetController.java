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
		log.error(">>> Entering VetController(vetRepository={})", vetRepository);
		this.vetRepository = vetRepository;
		log.debug("VetController(vetRepository={}): this.vetRepository → {}", vetRepository, this.vetRepository);
		log.warn("<<< Exiting VetController(vetRepository={})", vetRepository);
	}

	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		log.error(">>> Entering showVetList(page={},model={})", page, model);
		Page<Vet> paginated = findPaginated(page);
		log.error("showVetList(page={},model={}): paginated → {}", page, model, paginated);
		String result = addPaginationModel(page, paginated, model);
		log.warn("<<< Exiting showVetList(page={},model={}) -> {}", page, model, result);
		return result;
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		log.error(">>> Entering addPaginationModel(page={},paginated={},model={})", page, paginated, model);
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		log.error("addPaginationModel(page={},paginated={},model={}): listVets → {}", page, paginated, model, listVets);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		log.warn("<<< Exiting addPaginationModel(page={},paginated={},model={})", page, paginated, model);
		return "vets/vetList";
	}

	private Page<Vet> findPaginated(int page) {
		log.error(">>> Entering findPaginated(page={})", page);
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		log.error("findPaginated(page)={}: find query executing", page);
		Page<Vet> result = vetRepository.findAll(pageable);
		log.warn("<<< Exiting findPaginated(page={}) -> {}", page, result);
		return result;
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		log.error(">>> Entering showResourcesVetList()");
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		long start = System.currentTimeMillis();
		log.error("showResourcesVetList(): vets → {}", vets);
		log.error("showResourcesVetList(): find query executed in {} ms", (System.currentTimeMillis() - start));
		vets.getVetList().addAll(this.vetRepository.findAll());
		log.warn("<<< Exiting showResourcesVetList()");
		return vets;
	}

}
