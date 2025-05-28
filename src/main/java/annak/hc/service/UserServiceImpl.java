package annak.hc.service;

import annak.hc.dto.NewUserDto;
import annak.hc.dto.UserDto;
import annak.hc.entity.User;
import annak.hc.entity.embedded.Role;
import annak.hc.exception.ResourceUniqueViolationException;
import annak.hc.mapper.UserMapper;
import annak.hc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(NewUserDto newUserDto) {
        User user = userMapper.toEntity(newUserDto);

        if (userRepository.existsByUserName(user.getUsername())) {
            throw new ResourceUniqueViolationException("Користувач з ім'ям <%s> вже існує!".formatted(user.getUsername()));
        }
        user.setActive(true);
        if (userRepository.findAll().isEmpty()) {
            user.setRoles(Set.of(Role.USER, Role.ADMIN));
        } else {
            user.setRoles(Collections.singleton(Role.USER));
        }
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public String update(UserDto userDto) {
        Optional<User> userOptional = userRepository.findByUserName(userDto.getUserName());
        if (userOptional.isEmpty()) {
            return "Користувач з іменем <%s> не було знайдено!".formatted(userDto.getUserName());
        }
        User user = userMapper.toEntity(userDto);
        user.setRoles(userOptional.get().getRoles());
        userRepository.save(user);
        return "Профіль було успішно оновлено";
    }

    @Override
    public String update(User user) {
        Optional<User> userOptional = userRepository.findByUserName(user.getUsername());
        if (userOptional.isEmpty()) {
            return "Користувач з іменем <%s> не було знайдено!".formatted(user.getUsername());
        }
        userRepository.save(user);
        return "Профіль було успішно оновлено";
    }
}
