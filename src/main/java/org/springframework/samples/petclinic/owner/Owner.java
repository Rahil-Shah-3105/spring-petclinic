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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Oliver Drotbohm
 * @author Wick Dynex
 */
@Entity
@Table(name = "owners")
public class Owner extends Person {
private static final Logger log = LoggerFactory.getLogger(Owner.class);

	@Column
	@NotBlank
	private String address;

	@Column
	@NotBlank
	private String city;

	@Column
	@NotBlank
	@Pattern(regexp = "\\d{10}", message = "{telephone.invalid}")
	private String telephone;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private final List<Pet> pets = new ArrayList<>();

	public String getAddress() {
		log.debug(">>> Entering getAddress()");
		log.debug("<<< Exiting getAddress()");
		return this.address;
	}

	public void setAddress(String address) {
		log.debug(">>> Entering setAddress(address={})", address);
		this.address = address;
		log.debug("<<< Exiting setAddress(address={})", address);
	}

	public String getCity() {
		log.debug(">>> Entering getCity()");
		log.debug("<<< Exiting getCity()");
		return this.city;
	}

	public void setCity(String city) {
		log.debug(">>> Entering setCity(city={})", city);
		this.city = city;
		log.debug("<<< Exiting setCity(city={})", city);
	}

	public String getTelephone() {
		log.debug(">>> Entering getTelephone()");
		log.debug("<<< Exiting getTelephone()");
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		log.debug(">>> Entering setTelephone(telephone={})", telephone);
		this.telephone = telephone;
		log.debug("<<< Exiting setTelephone(telephone={})", telephone);
	}

	public List<Pet> getPets() {
		log.debug(">>> Entering getPets()");
		log.debug("<<< Exiting getPets()");
		return this.pets;
	}

	public void addPet(Pet pet) {
		log.debug(">>> Entering addPet(pet={})", pet);
		if (pet.isNew()) {
			getPets().add(pet);
		}
		log.debug("<<< Exiting addPet(pet={})", pet);
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @return the Pet with the given name, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(String name) {
		log.debug(">>> Entering getPet(name={})", name);
		return getPet(name, false);
	}

	/**
	 * Return the Pet with the given id, or null if none found for this Owner.
	 * @param id to test
	 * @return the Pet with the given id, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();
				if (Objects.equals(compId, id)) {
					return pet;
				}
			}
		}
		return null;
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @param ignoreNew whether to ignore new pets (pets that are not saved yet)
	 * @return the Pet with the given name, or null if no such Pet exists for this Owner
	 */
	public Pet getPet(String name, boolean ignoreNew) {
		for (Pet pet : getPets()) {
			String compName = pet.getName();
			if (compName != null && compName.equalsIgnoreCase(name)) {
				if (!ignoreNew || !pet.isNew()) {
					return pet;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		log.debug(">>> Entering toString()");
		return new ToStringCreator(this).append("id", this.getId())
			.append("new", this.isNew())
			.append("lastName", this.getLastName())
			.append("firstName", this.getFirstName())
			.append("address", this.address)
			.append("city", this.city)
			.append("telephone", this.telephone)
			.toString();
	}

	/**
	 * Adds the given {@link Visit} to the {@link Pet} with the given identifier.
	 * @param petId the identifier of the {@link Pet}, must not be {@literal null}.
	 * @param visit the visit to add, must not be {@literal null}.
	 */
	public void addVisit(Integer petId, Visit visit) {
	log.debug(">>> Entering addVisit(petId={},visit={})", petId, visit);

		Assert.notNull(petId, "Pet identifier must not be null!");
		Assert.notNull(visit, "Visit must not be null!");

		Pet pet = getPet(petId);
		log.debug("addVisit(petId={},visit={}): pet → {}", petId, visit, pet);

		Assert.notNull(pet, "Invalid Pet identifier!");

		pet.addVisit(visit);
		log.debug("<<< Exiting addVisit(petId={},visit={})", petId, visit);
	}

}
