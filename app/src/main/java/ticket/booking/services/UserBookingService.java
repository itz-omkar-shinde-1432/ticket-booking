package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class UserBookingService {
    private User user;
    private List<User>  userList;

     private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_Path ="../localDb/users.json";
    public UserBookingService(User user1) throws IOException
    {
        this.user = user1;
        File users = new File(USERS_Path);
        userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }



}
