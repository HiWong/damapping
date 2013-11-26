package fr.phan.damapping.test.springpackagescan.service;

/**
 * Room -
 *
 * @author Sébastien Lesaint
 */
public class Room {
    private final String roomNumber;

    public Room(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

}
