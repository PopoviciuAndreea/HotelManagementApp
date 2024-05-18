package com.example.project.repository;

import com.example.project.model.Hotel;
import com.example.project.model.Room;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class InitialDataRepo {

    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
    public void insertInitialData(HotelRepo hotelRepo, RoomRepo roomRepo, File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(file);
            for(int i = 0; i < jsonNode.size(); i++) {
                // Insert Hotels from the JSON file
                Hotel hotel;

                int id = jsonNode.get(i).get("id").asInt();
                String name = jsonNode.get(i).get("name").asText();
                Double latitude = jsonNode.get(i).get("latitude").asDouble();
                Double longitude = jsonNode.get(i).get("longitude").asDouble();

                hotel = new Hotel(id, name, latitude, longitude);
                hotelRepo.save(hotel);

                // Insert Rooms from the JSON file
                for(int j = 0; j < jsonNode.get(i).get("rooms").size(); j++) {
                    Room room;

                    JsonNode roomNode = jsonNode.get(i).get("rooms").get(j);

                    int hotelId = jsonNode.get(i).get("id").asInt();
                    int roomNumber = roomNode.get("roomNumber").asInt();
                    int roomType = roomNode.get("type").asInt();
                    int roomPrice = roomNode.get("price").asInt();
                    boolean isAvailable = Boolean.parseBoolean(roomNode.get("isAvailable").asText());

                    room = new Room(hotelId, roomNumber, roomType, roomPrice, isAvailable);
                    roomRepo.save(room);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
