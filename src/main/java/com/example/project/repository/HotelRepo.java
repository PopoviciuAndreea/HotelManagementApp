package com.example.project.repository;

import com.example.project.model.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HotelRepo {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
    public void save(Hotel hotel) {

        String sql = "insert into hotel(id, name, latitude, longitude) values (?, ?, ?, ?)";
        template.update(sql, hotel.getId(), hotel.getName(), hotel.getLatitude(), hotel.getLongitude());

    }

    public List<Hotel> findAll() {
        String sql = "select * from hotel";

        RowMapper<Hotel> mapper = new RowMapper<Hotel>() {
            @Override
            public Hotel mapRow(ResultSet rs, int rowNum) throws SQLException {
                Hotel hotel = new Hotel();
                hotel.setId(rs.getInt(1));
                hotel.setName(rs.getString(2));
                hotel.setLatitude(rs.getDouble(3));
                hotel.setLongitude(rs.getDouble(4));
                return hotel;
            }
        };

        List<Hotel> hotels = template.query(sql, mapper);
        return hotels;
    }

    public List<Integer> getNearbyHotelIDs(double radius, Float latitudeUser, Float longitudeUser) {
        List<Hotel> hotels = new ArrayList<Hotel>();
        hotels = findNearbyHotels(radius, latitudeUser, longitudeUser);
        List<Integer> hotelIDs = new ArrayList<Integer>();

        for(int i = 0; i < hotels.size(); i++) {
            if (!(hotels.get(i) == null)) {
                    hotelIDs.add(hotels.get(i).getId());
            }
        }

        return hotelIDs;
    }


    public List<Hotel> findNearbyHotels(double radius, Float latitudeUser, Float longitudeUser) {
        String sql = "select * from hotel";

        RowMapper<Hotel> mapper = new RowMapper<Hotel>() {
            @Override
            public Hotel mapRow(ResultSet rs, int rowNum) throws SQLException {
                Hotel hotel = new Hotel();
                
                Float latitudeHotel = rs.getFloat("latitude");
                Float longitudeHotel = rs.getFloat("longitude");

                Double distance = calculateDistance(latitudeUser, longitudeUser, latitudeHotel, longitudeHotel);

                if(distance <= radius) {
                    hotel.setId(rs.getInt(1));
                    hotel.setName(rs.getString(2));
                    hotel.setLatitude(rs.getDouble(3));
                    hotel.setLongitude(rs.getDouble(4));
                    hotel.setDistance(distance);
                    return hotel;
                }
                return null;
            }
        };

        List<Hotel> hotels = template.query(sql, mapper);
        for(int i = 0; i < hotels.size(); i++) {
            if (hotels.get(i) == null) {
                hotels.remove(i);
            }
        }
        return hotels;
    }

    private static Double calculateDistance(Float latUser, Float longUser, Float latHotel, Float longHotel) {

        double latIntoMeter = convertLatitudeIntoMeter(latUser);
        double longIntoMeter = convertLongitudeIntoMeter(longUser);

        double latHotelIntoMeter = convertLatitudeIntoMeter(latHotel);
        double longHotelIntoMeter = convertLongitudeIntoMeter(longHotel);

        double distance = Math.sqrt(Math.pow((latHotelIntoMeter - latIntoMeter), 2) + Math.pow((longHotelIntoMeter - longIntoMeter), 2));
        double distanceNauticalMile = distance / 1000;
        double distanceKM = Double.parseDouble(String.format("%.2f",distanceNauticalMile * 1.852));

        return distanceKM;
    }

    private static Double convertLatitudeIntoMeter(Float latitude) {
        return 111132.92 - 559.82 * Math.cos(2 * latitude) + 1.175 * Math.cos(4 * latitude) - 0.0023 * Math.cos(6 * latitude);
    }

    private static Double convertLongitudeIntoMeter(Float longitude) {
        return 111412.84 * Math.cos(longitude) - 93.5 * Math.cos(3 * longitude) + 0.118 * Math.cos(5 * longitude);
    }
}
