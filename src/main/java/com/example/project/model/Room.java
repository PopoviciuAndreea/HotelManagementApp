package com.example.project.model;

public class Room {
    private int hotelId;
    private int roomNumber;
    private int roomType;
    private int roomPrice;
    private boolean isAvailable;

    public Room(int hotelId, int roomNumber, int roomType, int roomPrice, boolean isAvailable) {
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isAvailable = isAvailable;
    }

    public Room() {

    }
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public int getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(int roomPrice) {
        this.roomPrice = roomPrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "\nRoom{" +
                "\thoteId=" + hotelId +
                ", roomNumber=" + roomNumber +
                ", type=" + roomType +
                ", price=" + roomPrice +
                ", isAvailable=" + isAvailable +
                "\t }";
    }
}
