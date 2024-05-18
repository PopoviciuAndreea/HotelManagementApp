package com.example.project;

import com.example.project.model.Feedback;
import com.example.project.model.Hotel;
import com.example.project.model.Reservation;
import com.example.project.model.Room;
import com.example.project.repository.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootApplication
public class ProjectApplication {
    private static double radius;
    private static float latitudeUser;
    private static float longitudeUser;
    private static HotelRepo hotelRepo;
    private static RoomRepo roomRepo;
    private static ReservationRepo reservationRepo;
    private static FeedbackRepo feedbackRepo;
    private static Scanner scanner;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProjectApplication.class, args);

        InitialDataRepo initialDataRepo = context.getBean(InitialDataRepo.class);

        scanner = new Scanner(System.in);

        hotelRepo = context.getBean(HotelRepo.class);
        roomRepo = context.getBean(RoomRepo.class);
        reservationRepo = context.getBean(ReservationRepo.class);
        feedbackRepo = context.getBean(FeedbackRepo.class);

        File file = new File("src/initialData.json");
        initialDataRepo.insertInitialData(hotelRepo, roomRepo, file);

        System.out.println("Please enter your location");
        latitudeUser = 91;
        longitudeUser = 181;
        boolean checkLocation = true;
        while (checkLocation) {
            try {
                while (latitudeUser < -90 || latitudeUser > 90) {
                    System.out.println("Enter the latitude: ");
                    latitudeUser = scanner.nextFloat();
                    if (latitudeUser < -90 || latitudeUser > 90) {
                        System.out.println("You have to enter a latitude between [-90, 90]");
                    }
                }

                while (longitudeUser < -180 || longitudeUser > 180) {
                    System.out.println("Enter the longitude: ");
                    longitudeUser = scanner.nextFloat();
                    if (longitudeUser < -180 || longitudeUser > 180) {
                        System.out.println("You have to enter a longitude between [-180, 180]");
                    }
                }
                checkLocation = false;
            } catch (InputMismatchException e) {
                System.out.println("You have entered an invalid value.");
                scanner.nextLine();
            }
        }
        
        /*
        Float latitudeUser = 46.2209344039103f;
        Float longitudeUser = 24.796173777924917f;
         */

        makeReservation();
        getOptionMenu();
    }

    public static void getOptionMenu() {
        try {
            System.out.println("\n\nChoose an option: \n 1. Make a new reservation     2. Cancel one reservation     3. Change the booked room     4. Leave Feedback     5. Close the program");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    makeReservation();
                    break;
                case 2:
                    cancelReservation();
                    break;
                case 3:
                    changeBookedRoom();
                    break;
                case 4:
                    leaveFeedback();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("You entered an invalid option");
                    System.out.println("Please enter a valid option");
                    getOptionMenu();
            }
        } catch (InputMismatchException e) {
            System.out.println("You entered an invalid option");
            scanner.nextLine();
            getOptionMenu();
        }
    }

    public static void makeReservation() {
        System.out.println("Please enter the radius: ");
        radius = scanner.nextFloat();
        List<Hotel> nearbyHotels = hotelRepo.findNearbyHotels(radius, latitudeUser, longitudeUser);

        if(nearbyHotels.get(0) == null) {
            System.out.println("\nThere is no hotel nearby within a radius of " + radius + " km");
            System.out.println("You have to introduce a new radius");
            makeReservation();
        } else {
            System.out.println("You choose a distance of " + radius + " km");
            System.out.println("These are the nearby hotels: ");
            System.out.println(nearbyHotels);
        }

        try {
            int wantedHotel = 0;
            List<Integer> availableHotels = hotelRepo.getNearbyHotelIDs(radius, latitudeUser, longitudeUser);
            while(!availableHotels.contains(wantedHotel)) {
                System.out.println("\n\nEnter the ID of an available hotel where you would like to stay: ");
                wantedHotel = scanner.nextInt();
                if(!availableHotels.contains(wantedHotel)) {
                    System.out.println("The hotel with ID " + wantedHotel + " is not available.");
                }
            }

            System.out.println("How many rooms do you want to reserve?");
            int numberOfRooms = scanner.nextInt();

            for (int i = 1; i <= numberOfRooms; i++) {

                int dateOption = 0;
                System.out.println("Do you want the reservation to start now? \n\t 1. YES    2. NO");

                while (dateOption != 1 && dateOption != 2) {
                    System.out.println("Please enter a valid option");
                    dateOption = scanner.nextInt();
                }

                LocalDateTime bookingDate = LocalDateTime.now();
                LocalDateTime bookedStartDate = chooseDate(dateOption);

                while(bookedStartDate.isBefore(bookingDate)){
                    System.out.println("\n\nThe day you wish to book is before the current date");
                    System.out.println("Please choose another date");
                    System.out.println("Do you want the reservation to start now? \n\t 1. YES    2. NO");
                    dateOption = 0;
                    while (dateOption != 1 && dateOption != 2) {
                        System.out.println("Please enter a valid option");
                        dateOption = scanner.nextInt();
                    }
                    bookedStartDate = chooseDate(dateOption);
                }

                System.out.println("\nWhat is the date on which you would like the reservation to end? ");
                LocalDateTime bookedEndDate = chooseDate(2);

                while(bookedStartDate.isAfter(bookedEndDate)) {
                    System.out.println("\n\nThe start date of the reservation is after the end date of the reservation");
                    System.out.println("Please choose another dates");
                    System.out.println("\nEnter the start date: ");
                    System.out.println("Do you want the reservation to start now? \n\t 1. YES    2. NO");

                    while (dateOption != 1 && dateOption != 2) {
                        System.out.println("Please enter a valid option");
                        dateOption = scanner.nextInt();
                    }
                    dateOption = scanner.nextInt();
                    bookedStartDate = chooseDate(dateOption);
                    System.out.println("Enter the end date: ");
                    bookedEndDate = chooseDate(2);
                }

                System.out.println("\nThese are the available rooms: ");
                List<Room> rooms = roomRepo.findHotelRooms(wantedHotel, bookedStartDate, bookedEndDate, reservationRepo);
                System.out.println(rooms);

                System.out.println("\nEnter the room number you want to reserve: ");
                int wantedRoom = scanner.nextInt();

                Reservation reservation = new Reservation(wantedHotel, wantedRoom, bookedStartDate, bookedEndDate, bookingDate);
                while (!reservationRepo.insertReservation(reservation)) {
                    System.out.println("This room is not available \n Please choose another room");
                    System.out.println("\n\nEnter the room number you want to reserve: ");
                    wantedRoom = scanner.nextInt();
                    reservation = new Reservation(wantedHotel, wantedRoom, bookedStartDate, bookedEndDate, bookingDate);
                }
                System.out.println(reservationRepo.findAll());

                getOptionMenu();
            }

        } catch (InputMismatchException e) {
            System.out.println("You enter an invalid option");
            scanner.nextLine();
            getOptionMenu();
        }
    }

    public static LocalDateTime chooseDate(int dateOption) {
        LocalDateTime dateTime;
        if(dateOption == 1) {
            dateTime = LocalDateTime.now();
        } else {
            int year = 0;
            while(year < 2024 || year > 2040) {
                System.out.print("\nEnter a valid year (e.g. 2024): ");
                year = scanner.nextInt();
            }
            int month = 0;
            while(month < 1 || month > 12) {
                System.out.print("\nEnter a valid month (e.g. 05): ");
                month = scanner.nextInt();
            }
            int day = 0;
            while(day < 1 || day > 31) {
                System.out.print("\nEnter a valid day (e.g. 17): ");
                day = scanner.nextInt();
            }
            int hour = -1;
            while(hour < 0 || hour > 23) {
                System.out.print("\nEnter a valid hour (e.g. 12): ");
                hour = scanner.nextInt();
            }
            int minute = -1;
            while(minute < 0 || minute > 59) {
                System.out.print("\nEnter a valid minute (e.g. 30): ");
                minute = scanner.nextInt();
            }
            int second = 0;
            dateTime = LocalDateTime.of(year, month, day, hour, minute, second);

        }
        return dateTime;
    }

    public static void cancelReservation() {
        try {
            System.out.println("Do you want to cancel a reservation? \n\t 1. YES    2. NO");
            int cancelOption = 0;
            while (cancelOption != 1 && cancelOption != 2) {
                System.out.println("Please enter a valid option");
                cancelOption = scanner.nextInt();
            }
            if (cancelOption == 1) {
                System.out.println("These are your reservations: ");
                System.out.println(reservationRepo.findAll());

                int reservationId = -1;
                while(!reservationRepo.getReservationIDs().contains(reservationId)) {
                    System.out.println("\nPlease enter the reservation ID you want to cancel");
                    reservationId = scanner.nextInt();
                    if(!reservationRepo.getReservationIDs().contains(reservationId)) {
                        System.out.println("Invalid ID");
                    }
                }

                reservationRepo.cancelReservation(reservationId);
                System.out.println("\nYour reservations: " + reservationRepo.findAll());
                getOptionMenu();
            } else {
                getOptionMenu();
            }
        } catch (InputMismatchException e){
            System.out.println("You entered an invalid option");
        }
    }

    public static void changeBookedRoom() {
        try {
            System.out.println("Do you want to change the room for your reservation? \n\t 1. YES    2. NO");
            int changeOption = 0;
            while (changeOption != 1 && changeOption != 2) {
                System.out.println("Please enter a valid option");
                changeOption = scanner.nextInt();
            }
            if (changeOption == 1) {
                System.out.println("These are your reservations: ");
                System.out.println(reservationRepo.findAll());

                int reservationId = -1;
                while(!reservationRepo.getReservationIDs().contains(reservationId)) {
                    System.out.println("\nPlease enter the reservation ID you want to change");
                    reservationId = scanner.nextInt();
                    if(!reservationRepo.getReservationIDs().contains(reservationId)) {
                        System.out.println("Invalid ID");
                    }
                }

                try {
                    reservationRepo.changeRoom(scanner, reservationId, roomRepo);
                    System.out.println("\n\nThese are your reservations");
                    System.out.println(reservationRepo.findAll());
                } catch (EmptyResultDataAccessException e) {
                    System.out.println("Reservation Not Found");
                }
                getOptionMenu();
            } else {
                getOptionMenu();
            }
        } catch (InputMismatchException e) {
            System.out.println("You entered an invalid option");
            scanner.nextLine();
            changeBookedRoom();
        }
    }

    public static void leaveFeedback() {
        try {
            System.out.println("Do you want to leave a feedback?  \n\t 1. YES    2. NO");
            int feedbackOption = 0;
            while (feedbackOption != 1 && feedbackOption != 2) {
                System.out.println("Please enter a valid option");
                feedbackOption = scanner.nextInt();
            }
            if (feedbackOption == 1) {
                System.out.println("\nThese are your reservation");
                System.out.println(reservationRepo.findAll());

                int reservationId = -1;
                while(!reservationRepo.getReservationIDs().contains(reservationId)) {
                    System.out.println("\nPlease enter the reservation ID for which you would like to leave feedback");
                    reservationId = scanner.nextInt();
                    if(!reservationRepo.getReservationIDs().contains(reservationId)) {
                        System.out.println("Invalid ID");
                    }
                }
                Map<String, Object> reservation = reservationRepo.getSpecificReservation(reservationId);

                System.out.println("\nPlease enter the feedback: ");
                scanner.nextLine();
                String message = scanner.nextLine();

                Feedback feedback = new Feedback(Integer.parseInt(reservation.get("hotelId").toString()), Integer.parseInt(reservation.get("roomNumber").toString()), message);
                feedbackRepo.save(feedback);

                System.out.println("\n\nThese are your previous feedback: ");
                System.out.println(feedbackRepo.findAll());
                getOptionMenu();
            }
        } catch (InputMismatchException e) {
            System.out.println("You entered an invalid option");
        }
    }
}
