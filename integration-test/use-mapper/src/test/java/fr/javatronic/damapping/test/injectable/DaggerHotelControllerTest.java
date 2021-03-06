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
package fr.javatronic.damapping.test.injectable;

import fr.javatronic.damapping.test.injectable.dto.HotelDto;

import dagger.ObjectGraph;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DaggerHotelControllerTest -
 *
 * @author Sébastien Lesaint
 */
public class DaggerHotelControllerTest {

  private HotelController hotelController;

  @BeforeClass
  public void setup() {
    ObjectGraph objectGraph = ObjectGraph.create(new DaggerHotelControllerModule());
    this.hotelController = objectGraph.get(HotelController.class);
  }

  @Test
  public void testGetHotel() throws Exception {
    HotelDto hotel = hotelController.getHotel();
    assertThat(hotel.getFloors()).hasSize(2);
    assertThat(hotel.getFloors().get(0).getRooms()).extracting("number").containsExactly("1", "2");
    assertThat(hotel.getFloors().get(1).getRooms()).extracting("number").containsExactly("11", "12");
  }
}
