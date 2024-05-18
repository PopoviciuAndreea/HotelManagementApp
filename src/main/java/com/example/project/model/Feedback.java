package com.example.project.model;

public class Feedback {
    private int feedbackId;
    private int hotelId;
    private int roomNumber;
    private String feedbackMessage;

    public Feedback(int hotelId, int roomNumber, String feedbackMessage) {
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.feedbackMessage = feedbackMessage;
    }



    public Feedback() {

    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
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

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "\n\tfeedbackId=" + feedbackId +
                ", hotelId=" + hotelId +
                ", roomNumber=" + roomNumber +
                ", feedbackMessage='" + feedbackMessage + '\'' +
                "\t }";
    }
}
