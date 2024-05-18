package com.example.project.repository;

import com.example.project.model.Feedback;
import com.example.project.model.Hotel;
import com.example.project.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FeedbackRepo {
    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    @Autowired
    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
    public void save(Feedback feedback) {
        List<Feedback> feedbacks = findAll();
        int feedbackId;
        if (feedbacks.isEmpty()) {
            feedbackId = 0;
        } else {
            Feedback feed = feedbacks.get(feedbacks.size() - 1);
            feedbackId = feed.getFeedbackId() + 1;
        }

        String sql = "insert into feedback(feedbackId, hotelId, roomNumber, feedbackMessage) values (?, ?, ?, ?)";
        template.update(sql, feedbackId, feedback.getHotelId(), feedback.getRoomNumber(), feedback.getFeedbackMessage());
        System.out.println("Successfully submitted feedback");
    }

    public List<Feedback> findAll() {
        String sql = "select * from feedback";

        RowMapper<Feedback> mapper = new RowMapper<Feedback>() {
            @Override
            public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt(1));
                feedback.setHotelId(rs.getInt(2));
                feedback.setRoomNumber(rs.getInt(3));
                feedback.setFeedbackMessage(rs.getString(4));
                return feedback;
            }
        };

        List<Feedback> feedbacks = template.query(sql, mapper);
        return feedbacks;
    }

    public List<Integer> getFeedbackIDs() {
        List<Feedback> feedbacks = findAll();
        List<Integer> feedbackIDs = new ArrayList<Integer>();
        for(Feedback feedback : feedbacks) {
            feedbackIDs.add(feedback.getFeedbackId());
        }
        return feedbackIDs;
    }
}
