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

public class UserBookingService {

    private User user;

    private List<User> userList;

    private final ObjectMapper objectMapper;

    private final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";


    public UserBookingService() throws IOException{
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadUsers();
    }
    private void loadUsers() throws IOException{
        userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
    }

    public boolean signUp(User user) throws IOException{
        try{
            Optional<User> foundUser = userList.stream().filter(user1 -> {
                return user1.getUsername().equals(user.getUsername());
            }).findFirst();

            if (foundUser.isPresent()) {
                // If a user with the same username exists,this will print an error message
                System.out.println("Username already taken!");
                return false;
            }

            userList.add(user);
            saveUserListToFile();
        }catch (Exception ex){
            System.out.println("saving user list to file failed " + ex.getMessage());
            return false;
        }
        return true;
    }

    private void saveUserListToFile() throws IOException{
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings(){
        System.out.println("Fetching your bookings");
        user.printTickets();
    }

    public Optional<User> getUserByUsername(String username){
        return userList.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public void setUser(User user){
        this.user = user;
    }



    public boolean cancelBooking(String ticketId) throws IOException{
        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }
        boolean isRemoved =  user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId) );
        if(isRemoved) {
            saveUserListToFile();
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return true;
        }else{
            System.out.println("No ticket found with ID " + ticketId);
            return false;
        }
    }

    public List<Train> getTrains (String source, String destination) throws IOException {
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source,destination);
        }catch (Exception ex){
            System.out.println("There is something wrong!");
            // return empty list if there is an exception
            return Collections.emptyList();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);

                    train.setSeats(seats);
                    trainService.addTrain(train);

                    Ticket ticket = new Ticket();

                    ticket.setSource(train.getStations().getFirst());
                    ticket.setDestination(train.getStations().getLast());
                    ticket.setTrain(train);
                    ticket.setUserId(user.getUserId());
                    ticket.setDateOfTravel("2021-09-01");
                    ticket.setTicketId(UserServiceUtil.generateTicketId());

                    user.getTicketsBooked().add(ticket);

                    System.out.println("Seat booked successfully  !  ");

                    System.out.println(ticket.getTicketInfo());

                    saveUserListToFile();
                    return true; // Booking successful
                } else {
                    return false; // Execute when Seat is already booked
                }
            } else {
                return false; // Execute when Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }


//    Redundant method - authenticating user with username and password in the main method in App.java
//    public Boolean loginUser(){
//        Optional<User> foundUser = userList.stream()
//                .filter(user1 -> user1.getUsername().equals(user.getUsername())
//                && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())).findFirst();
//        return foundUser.isPresent();
//    }

}