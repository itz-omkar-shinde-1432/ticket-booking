// UserServiceUtil.java
package ticket.booking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for user-related operations such as password hashing and ticket ID generation.
 */
public class UserServiceUtil {

    /**
     * Hashes a plain-text password using BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Checks if the plain-text password matches the hashed password.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Generates a unique ticket ID using the current timestamp.
     */
    public static String generateTicketId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
