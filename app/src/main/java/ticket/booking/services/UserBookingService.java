// UserBookingService.java
package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles user account actions including sign-up, booking tickets,
 * fetching bookings, canceling bookings, etc.
 */
public class UserBookingService {

    private User user;
    private List<User> userList;
    private final ObjectMapper objectMapper;
    private final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    // Constructor: Loads all users from the file
    public UserBookingService() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadUsers();
    }

    // Loads users from JSON file
    private void loadUsers() throws IOException {
        userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
    }

    /**
     * Registers a new user if username is unique.
     */
    public boolean signUp(User user) throws IOException {
        try {
            Optional<User> foundUser = userList.stream()
                    .filter(u -> u.getUsername().equals(user.getUsername()))
                    .findFirst();

            if (foundUser.isPresent()) {
                System.out.println("Username already taken!");
                return false;
            }

            userList.add(user);
            saveUserListToFile();
            return true;
        } catch (Exception ex) {
            System.out.println("Saving user list to file failed: " + ex.getMessage());
            return false;
        }
    }

    // Saves the user list to the JSON file
    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    // Fetches all bookings for the logged-in user
    public void fetchBookings() {
        System.out.println("Fetching your bookings...");
        user.printTickets();
    }

    /**
     * Returns a user by username (if found).
     */
    public Optional<User> getUserByUsername(String username) {
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    // Sets the current active user
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Cancels a ticket by ID for the logged-in user.
     */
    public boolean cancelBooking(String ticketId) throws IOException {
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return false;
        }

        boolean isRemoved = user.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (isRemoved) {
            saveUserListToFile();
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return true;
        } else {
            System.out.println("No ticket found with ID " + ticketId);
            return false;
        }
    }

    /**
     * Gets a list of trains that travel from source to destination.
     */
    public List<Train> getTrains(String source, String destination) throws IOException {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (Exception ex) {
            System.out.println("There is something wrong!");
            return Collections.emptyList();
        }
    }

    // Returns the seat layout of a given train
    public List<List<Integer>> fetchSeats(Train train) {
        return train.getSeats();
    }

    /**
     * Attempts to book a seat on the specified train.
     */
    public boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    // Book seat
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);

                    // Create new ticket
                    Ticket ticket = new Ticket();
                    ticket.setSource(train.getStations().getFirst());
                    ticket.setDestination(train.getStations().getLast());
                    ticket.setTrain(train);
                    ticket.setUserId(user.getUserId());
                    ticket.setDateOfTravel("2021-09-01"); // Replace with actual input
                    ticket.setTicketId(UserServiceUtil.generateTicketId());

                    // Add to user's booking list
                    user.getTicketsBooked().add(ticket);
                    saveUserListToFile();

                    System.out.println("Seat booked successfully!");
                    System.out.println(ticket.getTicketInfo());

                    return true;
                } else {
                    return false; // Seat already booked
                }
            } else {
                return false; // Invalid seat index
            }
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Authenticates user by matching username and password.
     */
    public boolean loginUser(String username, String plainPassword) {
        Optional<User> matchedUser = userList.stream()
                .filter(u -> u.getUsername().equals(username)
                        && UserServiceUtil.checkPassword(plainPassword, u.getHashedPassword()))
                .findFirst();

        if (matchedUser.isPresent()) {
            this.user = matchedUser.get();
            return true;
        } else {
            System.out.println("Login failed! Invalid username or password.");
            return false;
        }
    }
}
