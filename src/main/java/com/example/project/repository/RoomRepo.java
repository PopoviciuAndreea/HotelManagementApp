package com.example.project.repository;

import com.example.project.model.Reservation;
import com.example.project.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.context.ApplicationContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class RoomRepo {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
    public void save(Room room) {

        String sql = "insert into room(hotelId, roomNumber, roomType, roomPrice, isAvailable) values (?, ?, ?, ?, ?)";
        template.update(sql, room.getHotelId(), room.getRoomNumber(), room.getRoomType(), room.getRoomPrice(), room.isAvailable());

    }

    public List<Room> findAll() {
        String sql = "select * from room";

        RowMapper<Room> mapper = new RowMapper<Room>() {
            @Override
            public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
                Room room = new Room();
                room.setHotelId(rs.getInt(1));
                room.setRoomNumber(rs.getInt(2));
                room.setRoomType(rs.getInt(3));
                room.setRoomPrice(rs.getInt(4));
                room.setAvailable(Boolean.parseBoolean(rs.getString(5)));
                return room;
            }
        };

        List<Room> rooms = template.query(sql, mapper);
        return rooms;
    }

    public List<Room> findHotelRooms(int id, LocalDateTime startDate, LocalDateTime endDate, ReservationRepo reservationRepo) {
        String sql = "select * from room where hotelId = " + id;


        RowMapper<Room> mapper = new RowMapper<Room>() {
            @Override
            public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
                Room room = new Room();

                room.setHotelId(rs.getInt(1));
                room.setRoomNumber(rs.getInt(2));
                room.setRoomType(rs.getInt(3));
                room.setRoomPrice(rs.getInt(4));

                String sql;
                if(reservationRepo.isReserved(id, room.getRoomNumber(), startDate, endDate)) {
                    //System.out.println("isReserved");
                    room.setAvailable(false);
                    sql = "update room SET isAvailable = 'false' where hotelId = " + room.getHotelId() + " AND roomNumber = " + room.getRoomNumber();
                } else {
                    //System.out.println("isNotReserved");
                    room.setAvailable(true);
                    sql = "update room SET isAvailable = 'true' where hotelId = " + room.getHotelId() + " AND roomNumber = " + room.getRoomNumber();
                }
                template.update(sql);

                return room;
            }
        };

        List<Room> rooms = template.query(sql, mapper);
        return rooms;
    }
}
