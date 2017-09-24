package nz.ac.auckland.concert.service.services;

import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.service.domain.User;

public class ObjectMapper {

    public static User userToDomainModel(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String firstname = userDTO.getFirstname();
        String lastname = userDTO.getLastname();
        User user = new User(username, password, firstname, lastname);
        return user;
    }

    public static UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getFirstname(),
                user.getLastname()
        );
        return userDTO;
    }

}
