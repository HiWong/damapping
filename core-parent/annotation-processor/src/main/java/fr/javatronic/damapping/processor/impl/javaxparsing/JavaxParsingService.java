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
package fr.javatronic.damapping.processor.impl.javaxparsing;

import fr.javatronic.damapping.processor.model.DAType;

import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;

/**
 * JavaxParsingService -
 *
 * @author Sébastien Lesaint
 */
public interface JavaxParsingService {
  @Nonnull
  ParsingResult parse(@Nonnull TypeElement classElement, @Nullable Collection<DAType> generatedTypes)
      throws IOException;

}
