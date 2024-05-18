CREATE TABLE hotel (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    latitude NUMERIC(17, 15),
    longitude NUMERIC(17, 15)
);

CREATE TABLE room (
    hotelId INT,
    roomNumber INT,
    roomType VARCHAR(20),
    roomPrice INT,
    isAvailable VARCHAR(15),
    foreign key (hotelId) references hotel(id)
    /*PRIMARY KEY(hotelId, roomNumber) */
);

CREATE TABLE reservation (
    reservationId INT PRIMARY KEY,
    hotelId INT,
    roomNumber INT,
    bookedStartDate TIMESTAMP WITH TIME ZONE,
    bookedEndDate TIMESTAMP WITH TIME ZONE,
    bookingDate TIMESTAMP WITH TIME ZONE,
    foreign key (hotelId) references hotel(id)
    /* foreign key (roomNumber) references room(roomNumber) */
);

CREATE TABLE feedback (
    feedbackId INT PRIMARY KEY,
    hotelId INT,
    roomNumber INT,
    feedbackMessage VARCHAR(250),
    foreign key (hotelId) references hotel(id)
    /* foreign key (roomNumber) references room(roomNumber) */
)