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
package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JavaBean domain object representing an person.
 *
 * @author Ken Krebs
 */
@MappedSuperclass
public class Person extends BaseEntity {
private static final Logger log = LoggerFactory.getLogger(Person.class);

	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	private String firstName;

	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	private String lastName;

	public String getFirstName() {
		log.error(">>> Entering getFirstName()");
		log.warn("<<< Exiting getFirstName()");
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		log.error(">>> Entering setFirstName(firstName=<redacted>)");
		this.firstName = firstName;
		log.debug("setFirstName(firstName=<redacted>): this.firstName → <redacted>");
		log.warn("<<< Exiting setFirstName(firstName=<redacted>)");
	}

	public String getLastName() {
		log.error(">>> Entering getLastName()");
		log.warn("<<< Exiting getLastName()");
		return this.lastName;
	}

	public void setLastName(String lastName) {
		log.error(">>> Entering setLastName(lastName=<redacted>)");
		this.lastName = lastName;
		log.debug("setLastName(lastName=<redacted>): this.lastName → <redacted>");
		log.warn("<<< Exiting setLastName(lastName=<redacted>)");
	}

}
