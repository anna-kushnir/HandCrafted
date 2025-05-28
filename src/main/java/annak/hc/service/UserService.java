package annak.hc.service;

import annak.hc.dto.NewUserDto;
import annak.hc.dto.UserDto;
import annak.hc.entity.User;

public interface UserService {
    UserDto save(NewUserDto newUserDto);
    String update(UserDto userDto);
    String update(User user);
}
