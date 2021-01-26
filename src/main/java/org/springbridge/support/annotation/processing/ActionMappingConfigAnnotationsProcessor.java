 /*
 * Copyright 2020 Anoop V S.
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
package org.springbridge.support.annotation.processing;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.springbridge.action.ActionMapping;
import org.springbridge.support.annotation.ActionMappingConfig;

/**
 * Simple AnnotationsProcessor to validate {@code ActionMappingConfig}
 * annotation is only applied to ActionMapping elements .This will perform
 * compile time sanity checks on spring-bridge annotations.This will help to
 * avoid unexpected runtime exceptions.You can use it along with
 * maven-compiler-plugin to ensure you or team are using annotation correctly.
 * 
 * @author Anoop V S
 *
 */
@SupportedAnnotationTypes(value = { "org.springbridge.support.annotation.ActionMappingConfig" })
public class ActionMappingConfigAnnotationsProcessor extends AbstractProcessor {

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		boolean isFailed = false;
		log(NOTE, "ActionMappingConfig annotations process()::Entry");
		if (!roundEnv.processingOver()) {
			final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ActionMappingConfig.class);
			if (elements.isEmpty()) {
				log(NOTE, "No elements to process");
			} else {
				for (final Element annotatedElement : elements) {
					/*
					 * In Struts world we can subclass ActionMapping.But in bridge method
					 * ActionMapping type is always be ActionMapping as in Struts execute method.
					 */
					if (!annotatedElement.asType().toString().equals(ActionMapping.class.getName())) {
						log(ERROR,
								"Annotation @ActionMappingConfig can only be applied to ActionMapping method parameter.",
								annotatedElement);
						isFailed = true;
					}
				}
			}
		}
		log(NOTE, "ActionMappingConfig annotations process()::Exit");
		return isFailed;
	}

	private void log(final Kind kind, final CharSequence message) {
		processingEnv.getMessager().printMessage(kind, message);
	}

	private void log(final Kind kind, final CharSequence message, final Element element) {
		processingEnv.getMessager().printMessage(kind, message, element);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

}
