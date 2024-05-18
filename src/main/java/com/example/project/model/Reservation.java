package com.example.project.model;

import java.time.LocalDateTime;

public class Reservation {
    private int reservationId;
    private int hotelId;
    private int roomNumber;
    private LocalDateTime bookedStartDate;
    private LocalDateTime bookedEndDate;
    private LocalDateTime bookingDate;

    public Reservation(int hotelId, int roomNumber, LocalDateTime bookedStartDate, LocalDateTime bookedEndDate, LocalDateTime bookingDate) {
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.bookedStartDate = bookedStartDate;
        this.bookedEndDate = bookedEndDate;
        this.bookingDate = bookingDate;
    }

    public Reservation() {

    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
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

    public LocalDateTime getBookedStartDate() {
        return bookedStartDate;
    }

    public void setBookedStartDate(LocalDateTime bookedStartDate) {
        this.bookedStartDate = bookedStartDate;
    }

    public LocalDateTime getBookedEndDate() {
        return bookedEndDate;
    }

    public void setBookedEndDate(LocalDateTime bookedEndDate) {
        this.bookedEndDate = bookedEndDate;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "\n\t ReservationId=" + reservationId +
                "\n\t hotelId=" + hotelId +
                ", roomNumber=" + roomNumber +
                "\n\t BookedStartDate=" + bookedStartDate +
                ", BookedEndDate=" + bookedEndDate +
                ", BookingDate=" + bookingDate +
                '}';
    }
}
