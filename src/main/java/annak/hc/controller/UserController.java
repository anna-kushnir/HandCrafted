package annak.hc.controller;

import annak.hc.dto.NewUserDto;
import annak.hc.dto.UserDto;
import annak.hc.dto.UserPasswordDto;
import annak.hc.entity.User;
import annak.hc.entity.embedded.Role;
import annak.hc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @GetMapping("/merge-cart-check")
    public String mergeCartCheckView() {
        return "user/merge_cart";
    }

    @GetMapping
    public String startPage() {
        return "redirect:/products";
    }

    @GetMapping("/admin")
    public String startAdminPage() {
        return "redirect:/admin/menu";
    }

    @GetMapping("/register")
    public String registerView(@ModelAttribute("user") NewUserDto newUserDto, Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        model.addAttribute("user", (newUserDto == null) ? new NewUserDto() : newUserDto);
        return "registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") NewUserDto newUserDto, Model model) {
        try {
            if (!newUserDto.getPassword().equals(newUserDto.getSubmitPassword())) {
                throw new Exception("Паролі не співпадають");
            }
            newUserDto.setPassword(passwordEncoder.encode(newUserDto.getPassword()));
            newUserDto.setSubmitPassword(passwordEncoder.encode(newUserDto.getSubmitPassword()));
            userService.save(newUserDto);
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            newUserDto.setPassword(null);
            newUserDto.setSubmitPassword(null);
            model.addAttribute("user", newUserDto);
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginView(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        return "authorization";
    }

    @GetMapping("/menu")
    public String menuView(Principal principal, Model model) {
        if (principal != null) {
            if (userDetailsService.loadUserByUsername(principal.getName()).getAuthorities().contains(Role.ADMIN)) {
                model.addAttribute("isAdmin", true);
            }
            return "user/main_menu";
        }
        return "guest/main_menu";
    }

    @GetMapping("/admin/menu")
    public String adminMenuView() {
        return "admin/main_menu";
    }

    @GetMapping("/editProfile")
    public String editProfileView(Principal principal, Model model) {
        model.addAttribute("oldUser", userDetailsService.loadUserByUsername(principal.getName()));
        model.addAttribute("newUser", new UserDto());
        return "user/profile";
    }

    @PostMapping("/editProfile")
    public String editProfile(Principal principal,
                              @ModelAttribute("newUser") UserDto userDto,
                              RedirectAttributes redirectAttributes) {
        var user = (User)userDetailsService.loadUserByUsername(principal.getName());
        userDto.setId(user.getId());
        userDto.setUserName(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setActive(true);
        var result = userService.update(userDto);
        redirectAttributes.addFlashAttribute("message", result);
        return "redirect:/editProfile";
    }

    @GetMapping("/changePassword")
    public String changePasswordView(Model model) {
        model.addAttribute("newUserPassword", new UserPasswordDto());
        return "user/change_password";
    }

    @PostMapping("/changePassword")
    public String changePassword(Principal principal,
                                 @ModelAttribute("newUserPassword") UserPasswordDto userPasswordDto,
                                 RedirectAttributes redirectAttributes) {
        var user = (User)userDetailsService.loadUserByUsername(principal.getName());
        if (passwordEncoder.matches(userPasswordDto.getOldPassword(), user.getPassword())) {
            if (userPasswordDto.getNewPassword().equals(userPasswordDto.getSubmitNewPassword())) {
                user.setPassword(passwordEncoder.encode(userPasswordDto.getNewPassword()));
                var result = userService.update(user);
                redirectAttributes.addFlashAttribute("message", result);
                return "redirect:/changePassword";
            }
            redirectAttributes.addFlashAttribute("message", "Некоректне повторне введення нового паролю!");
            return "redirect:/changePassword";
        }
        redirectAttributes.addFlashAttribute("message", "Поточний пароль не співпадає з вашим дійсним паролем!");
        return "redirect:/changePassword";
    }
}
