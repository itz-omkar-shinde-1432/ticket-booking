// User.java
package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Collections;
import java.util.List;

/**
 * Represents a User in the booking system,
 * with credentials and booked ticket history.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String username;
    private String userId;
    private String password;              // Plain password (used temporarily before hashing)
    private String hashedPassword;
    private List<Ticket> ticketsBooked;

    // Default constructor required for Jackson
    public User() {}

    // Full constructor
    public User(String username, String password, String hashedPassword, List<Ticket> ticketsBooked, String userId) {
        this.username = username;
        this.userId = userId;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketsBooked = ticketsBooked != null ? ticketsBooked : Collections.emptyList(); // Prevent null list issues
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }

    // Prints all booked tickets for the user
    public void printTickets() {
        if (ticketsBooked.isEmpty()) {
            System.out.println("No tickets booked yet!");
        } else {
            for (Ticket ticket : ticketsBooked) {
                System.out.println(ticket.getTicketInfo());
            }
        }
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setTicketsBooked(List<Ticket> ticketsBooked) {
        this.ticketsBooked = ticketsBooked;
    }
}
