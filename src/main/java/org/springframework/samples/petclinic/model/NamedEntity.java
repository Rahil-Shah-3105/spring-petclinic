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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>. Used as
 * a base class for objects needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Wick Dynex
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity {
private static final Logger log = LoggerFactory.getLogger(NamedEntity.class);

	@Column
	@NotBlank
	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		log.info("setName(name={}): NERCCIP-AUDIT: Change management — State change recorded for the audit trail", name);
		this.name = name;
		log.info("setName(name={}): NERCCIP-AUDIT: Electronic perimeter — State change recorded for the audit trail", name);
	}

	@Override
	public String toString() {
		String name = this.getName();
		return name != null ? name : "<null>";
	}

}
