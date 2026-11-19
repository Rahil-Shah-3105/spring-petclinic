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

package org.springframework.samples.petclinic;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.vet.Vet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetClinicRuntimeHints implements RuntimeHintsRegistrar {
private static final Logger log = LoggerFactory.getLogger(PetClinicRuntimeHints.class);

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Outcome 4: privilege changes — State change recorded for the audit trail", hints, classLoader);
		hints.resources().registerPattern("db/*"); // https://github.com/spring-projects/spring-boot/issues/32654
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Outcome 3: object and data access — State change recorded for the audit trail", hints, classLoader);
		hints.resources().registerPattern("db/*/*"); // nested db/{h2,mysql,postgres}
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Outcome 2: source/destination network traffic — State change recorded for the audit trail", hints, classLoader);
		hints.resources().registerPattern("messages/*");
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Outcome 1: user attribution — State change recorded for the audit trail", hints, classLoader);
		hints.resources().registerPattern("mysql-default-conf");
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Continuous Event Monitoring / Network Visibility — State change recorded for the audit trail", hints, classLoader);
		hints.reflection().registerType(BaseEntity.class, typeHint -> typeHint.withJavaSerialization(true));
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Log Management / data protection — State change recorded for the audit trail", hints, classLoader);
		hints.reflection().registerType(Person.class, typeHint -> typeHint.withJavaSerialization(true));
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Log integrity (encrypt + validate) — State change recorded for the audit trail", hints, classLoader);
		hints.reflection().registerType(Vet.class, typeHint -> typeHint.withJavaSerialization(true));
		log.info("registerHints(hints={}, classLoader={}): M2614-AUDIT: Activity-Based Outcomes 1-11 — State change recorded for the audit trail", hints, classLoader);
	}

}
