/**
 * Copyright (C) 2013 Sébastien Lesaint (http://www.javatronic.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.javatronic.damapping.processor.validator;

import fr.javatronic.damapping.processor.model.DASourceClass;
import fr.javatronic.damapping.processor.model.InstantiationType;

import javax.annotation.Nonnull;

/**
 * ConstructorValidationStep - Makes sure that a DASourceClass of instantiationType {@code CONSTRUCTOR} has at least
 * one accessible constructor.
 *
 * @author Sébastien Lesaint
 */
public class ConstructorValidationStep implements ValidationStep {
  @Override
  public void validate(@Nonnull DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getInstantiationType() != InstantiationType.CONSTRUCTOR) {
      return;
    }

    validateHasAccessibleConstructor(sourceClass);

    validateHasOnlyOneCosntructor(sourceClass);
  }

  private void validateHasAccessibleConstructor(DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getAccessibleConstructors().isEmpty()) {
      throw new ValidationError("Class does not expose an accessible default constructor", sourceClass, null, null);
    }
  }

  private void validateHasOnlyOneCosntructor(DASourceClass sourceClass) throws ValidationError {
    if (sourceClass.getAccessibleConstructors().size() > 1) {
      throw new ValidationError(
          "Mapper can not define more than one constructor",
          sourceClass,
          sourceClass.getAccessibleConstructors().get(1),
          null
      );
    }
  }

}
