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
package fr.javatronic.damapping.test.injectable.mapping;

import fr.javatronic.damapping.annotation.Injectable;
import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.test.injectable.dto.FloorDto;
import fr.javatronic.damapping.test.injectable.service.Floor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import static fr.javatronic.damapping.util.Preconditions.checkNotNull;

/**
 * InjectableFloorToFloorDto -
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Injectable
public class InjectableFloorToFloorDto implements Function<Floor, FloorDto> {
  @Nonnull
  private final InjectableRoomToRoomDtoMapper roomToRoomDtoMapper;

  public InjectableFloorToFloorDto(@Nonnull InjectableRoomToRoomDtoMapper roomToRoomDtoMapper) {
    this.roomToRoomDtoMapper = checkNotNull(roomToRoomDtoMapper);
  }

  @Nullable
  @Override
  public FloorDto apply(@Nullable Floor floor) {
    return new FloorDto(
        FluentIterable.from(floor.getRooms()).transform(roomToRoomDtoMapper).toList()
    );
  }
}
