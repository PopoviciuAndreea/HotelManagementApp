package com.example.project.repository;

import com.example.project.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Repository
public class ReservationRepo {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
    public boolean insertReservation(Reservation reservation) {

        List<Integer> roomNumbers = getRoomNumbers(reservation.getHotelId());

        if (isReserved(reservation.getHotelId(), reservation.getRoomNumber(), reservation.getBookedStartDate(), reservation.getBookedEndDate())) {
            return false;
        }
        if (!roomNumbers.contains(reservation.getRoomNumber())) {
            return false;
        } else {
            String sql = "insert into reservation(reservationId, hotelId, roomNumber, bookedStartDate, bookedEndDate, bookingDate) values (?, ?, ?, ?, ?, ?)";

            List<Reservation> reservations = findAll();
            int reservationId;
            if (reservations.isEmpty()) {
                reservationId = 0;
            } else {
                Reservation res = reservations.get(reservations.size() - 1);
                reservationId = res.getReservationId() + 1;
            }

            template.update(sql, reservationId, reservation.getHotelId(), reservation.getRoomNumber(), reservation.getBookedStartDate(), reservation.getBookedEndDate(), reservation.getBookingDate());
            return true;
        }
    }

    public List<Integer> getRoomNumbers(int hotelId) {
        String sql = "select roomNumber from room where hotelId = " + hotelId;
        RowMapper<Integer> mapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        };

        List<Integer> roomNumbers = template.query(sql, mapper);
        return roomNumbers;
    }

    public List<Integer> getReservationIDs() {
        List<Reservation> reservations = findAll();
        List<Integer> reservationIDs = new ArrayList<Integer>();
        for(Reservation reservation : reservations) {
            reservationIDs.add(reservation.getReservationId());
        }
        return reservationIDs;
    }

    public List<Reservation> findAll() {
        String sql = "select * from reservation";

        RowMapper<Reservation> mapper = new RowMapper<Reservation>() {
            @Override
            public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
                Calendar cal = Calendar.getInstance();
                Timestamp timestamp;
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));

                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt(1));
                reservation.setHotelId(rs.getInt(2));
                reservation.setRoomNumber(rs.getInt(3));

                timestamp = rs.getTimestamp(4, cal);
                reservation.setBookedStartDate(timestamp.toLocalDateTime());

                timestamp = rs.getTimestamp(5, cal);
                reservation.setBookedEndDate(timestamp.toLocalDateTime());

                timestamp = rs.getTimestamp(6, cal);
                reservation.setBookingDate(timestamp.toLocalDateTime());
                return reservation;
            }
        };

        List<Reservation> reservations = template.query(sql, mapper);
        return reservations;
    }

    public List<Reservation> getReservationsForRoom(int hotelId, int roomNumber) {
        String sql = "select * from reservation where hotelId = " + hotelId + " AND roomNumber = " + roomNumber;

        RowMapper<Reservation> mapper = new RowMapper<Reservation>() {
            @Override
            public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
                Calendar cal = Calendar.getInstance();
                Timestamp timestamp;
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));

                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt(1));
                reservation.setHotelId(rs.getInt(2));
                reservation.setRoomNumber(rs.getInt(3));

                timestamp = rs.getTimestamp(4, cal);
                reservation.setBookedStartDate(timestamp.toLocalDateTime());

                timestamp = rs.getTimestamp(5, cal);
                reservation.setBookedEndDate(timestamp.toLocalDateTime());

                timestamp = rs.getTimestamp(6, cal);
                reservation.setBookingDate(timestamp.toLocalDateTime());
                return reservation;
            }
        };

        List<Reservation> reservations = template.query(sql, mapper);
        return reservations;
    }

    public Map<String, Object> getSpecificReservation(int reservationId) {
        String sql = "select * from reservation where reservationId = " + reservationId;
        try {
            //Reservation reservation = template.queryForObject(sql, Reservation.class);
            return template.queryForMap(sql);
            //return reservation;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Reservation Not Found");
        }
        return null;
    }

    public boolean isReserved(int hotelId, int roomNumber, LocalDateTime startDate, LocalDateTime endDate) {
        List<Reservation> reservations = getReservationsForRoom(hotelId, roomNumber);

        for(Reservation reservation : reservations) {

            if((startDate.isAfter(reservation.getBookedStartDate())
                    && startDate.isBefore(reservation.getBookedEndDate().plusDays(1)))
                    || endDate.isBefore(reservation.getBookedEndDate().plusDays(1)))
            {
                return true;
            }
        }
        return false;
    }

    public void cancelReservation(int reservationId) {
        if(checkBookingDate(reservationId)) {
            String sql = "delete from reservation where reservationId = " + reservationId;
            template.update(sql);
            System.out.println("Reservation successfully deleted");
        } else {
            System.out.println("You are less than 2 hours before check-in");
            System.out.println("The reservation can no longer be cancelled\n");
        }
    }

    public void changeRoom(Scanner scanner, int reservationId, RoomRepo roomRepo) {
        String sqlStartDate = "select bookedStartDate from reservation where reservationId = " + reservationId;
        String sqlEndDate = "select bookedEndDate from reservation where reservationId = " + reservationId;
        String sqlHotelId = "select hotelId from reservation where reservationId = " + reservationId;

        LocalDateTime startDate = template.queryForObject(sqlStartDate, LocalDateTime.class);
        LocalDateTime endDate = template.queryForObject(sqlEndDate, LocalDateTime.class);
        Integer hotelId = template.queryForObject(sqlHotelId, Integer.class);

        System.out.println(roomRepo.findHotelRooms(hotelId, startDate, endDate, this));
        System.out.println("Please enter the room you want to book: ");
        int roomNumber = scanner.nextInt();

        if(checkBookingDate(reservationId)) {
            String sql = "update reservation SET roomNumber = " + roomNumber + " where reservationId = " + reservationId;
            try {
                template.update(sql);
                System.out.println("Reservation successfully updated");
            } catch (EmptyResultDataAccessException e) {
                System.out.println("Reservation Not Found");
            }
        } else {
            System.out.println("You are less than 2 hours before check-in");
            System.out.println("Your room can no longer be changed\n");
        }
    }

    // Check if the booked date is made 2 hours before the current date
    public boolean checkBookingDate(int reservationId) {
        String sqlStartDate = "select bookedStartDate from reservation where reservationId = " + reservationId;
        LocalDateTime resStartDate = template.queryForObject(sqlStartDate, LocalDateTime.class);

        LocalDateTime currentDate = LocalDateTime.now().plusHours(2);

        System.out.println("Current date: " + currentDate + "\t/  check-in Date: " + resStartDate);

        if(currentDate.isBefore(resStartDate)) {
            return true;
        }
        return false;
    }
}
