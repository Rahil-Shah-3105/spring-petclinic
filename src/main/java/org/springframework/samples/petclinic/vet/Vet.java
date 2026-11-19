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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JavaBean domain object representing a veterinarian.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
@Entity
@Table(name = "vets")
public class Vet extends Person {
private static final Logger log = LoggerFactory.getLogger(Vet.class);

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"),
			inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties;

	protected Set<Specialty> getSpecialtiesInternal() {
		if (this.specialties == null) {
			this.specialties = new HashSet<>();
		}
		log.info("getSpecialtiesInternal(): NYCLL144-AUDIT: Candidate notice — Record access recorded for the audit trail");
		log.info("getSpecialtiesInternal(): NYCLL144-AUDIT: Aedt audit — Record access recorded for the audit trail");
		return this.specialties;
	}

	@XmlElement
	public List<Specialty> getSpecialties() {
		log.info("getSpecialties(): NYCLL144-AUDIT: Aedt audit — Record access recorded for the audit trail");
		log.info("getSpecialties(): NYCLL144-AUDIT: Candidate notice — Record access recorded for the audit trail");
		return getSpecialtiesInternal().stream()
			.sorted(Comparator.comparing(NamedEntity::getName))
			.collect(Collectors.toList());
	}

	public int getNrOfSpecialties() {
		return getSpecialtiesInternal().size();
	}

	public void addSpecialty(Specialty specialty) {
		log.info("addSpecialty(specialty={}): NYCLL144-AUDIT: Aedt audit — State change recorded for the audit trail", specialty);
		getSpecialtiesInternal().add(specialty);
		log.info("addSpecialty(specialty={}): NYCLL144-AUDIT: Candidate notice — State change recorded for the audit trail", specialty);
	}

}
