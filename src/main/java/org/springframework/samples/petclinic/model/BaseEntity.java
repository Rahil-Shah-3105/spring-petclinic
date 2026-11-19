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

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JavaBean domain object with an id property. Used as a base class for objects
 * needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
private static final Logger log = LoggerFactory.getLogger(BaseEntity.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	public Integer getId() {
		log.error(">>> Entering getId()");
		log.warn("<<< Exiting getId()");
		return id;
	}

	public void setId(Integer id) {
		log.error(">>> Entering setId(id={})", id);
		this.id = id;
		log.debug("setId(id={}): this.id → {}", id, this.id);
		log.warn("<<< Exiting setId(id={})", id);
	}

	public boolean isNew() {
		log.error(">>> Entering isNew()");
		log.warn("<<< Exiting isNew()");
		return this.id == null;
	}

}
