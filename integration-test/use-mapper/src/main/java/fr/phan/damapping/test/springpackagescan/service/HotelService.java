package fr.phan.damapping.test.springpackagescan.service;

import java.util.List;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.springframework.stereotype.Component;

/**
 * HotelService -
 *
 * @author Sébastien Lesaint
 */
@Component
public class HotelService {
    private Hotel hotel = buildHotel();

    private Hotel buildHotel() {
        Hotel res = new Hotel();
        res.setName("Hotel California");
        res.setFloors(
                ImmutableList.of(
                        new Floor(1, ImmutableList.of(new Room("1"), new Room("2"))),
                        new Floor(2, ImmutableList.of(new Room("11"), new Room("12")))
                )
        );
        return res;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public List<Room> getRooms() {
        return ImmutableList.copyOf(
                Iterables.concat(
                        hotel.getFloors().get(0).getRooms(),
                        hotel.getFloors().get(1).getRooms()
                )
        );
    }

}