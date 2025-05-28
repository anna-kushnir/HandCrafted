package annak.hc.mapper;

import annak.hc.dto.NewUserDto;
import annak.hc.dto.UserDto;
import annak.hc.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(NewUserDto newUserDto);
    User toEntity(UserDto userDto);
}
