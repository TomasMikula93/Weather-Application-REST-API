package com.example.weatherapplicationrestapi.Services;

import com.example.weatherapplicationrestapi.Models.Enums.Roles;
import com.example.weatherapplicationrestapi.Models.UserList;
import com.example.weatherapplicationrestapi.Models.WAUser;
import com.example.weatherapplicationrestapi.Registration.ConfirmationToken;
import com.example.weatherapplicationrestapi.Registration.ConfirmationTokenService;
import com.example.weatherapplicationrestapi.Registration.EmailService;
import com.example.weatherapplicationrestapi.Registration.EmailValidation;
import com.example.weatherapplicationrestapi.Repositories.UserListRepository;
import com.example.weatherapplicationrestapi.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final EmailValidation emailValidation;
    private final UserRepository userRepository;
    private final UserListRepository userListRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    @Override
    public boolean checkIfUsernameExists(String username) {
        return userRepository.findOptionalByUsername(username).isPresent();
    }

    @Override
    public boolean emailIsValidate(String email) {
        return emailValidation.isValidEmail(email);
    }

    @Override
    public void saveNewUser(WAUser user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        WAUser waUser = new WAUser(user.getUsername(),
                encodedPassword,
                user.getEmail(),
                Roles.USER.getRole());
        userRepository.save(waUser);

        UserList userList = new UserList(waUser, new ArrayList<>());
        userListRepository.save(userList);

        waUser.setUserList(userList);
        userRepository.save(waUser);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                waUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/api/registration/confirm?token=" + token;
        emailService.send(user.getEmail(), buildEmail(user.getUsername(), link));
    }

    @Override
    public void enableAppUser(String username) {
        WAUser user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }


    @Override
    public WAUser findByUser(WAUser user) {
        return userRepository.findByUsername(user.getUsername());
    }

    @Override
    public boolean userOwnsList(String username, long id) {
        return userListRepository.findByWaUser_UsernameAndId(username, id).isPresent();
    }

    @Override
    public void generateNewToken(String username, String email) {
        WAUser waUser = userRepository.findByUsername(username);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                waUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/api/registration/confirm?token=" + token;
        emailService.send(waUser.getEmail(), buildEmail(waUser.getUsername(), link));
    }

    @Override
    public boolean userAccountIsEnabled(String username) {
        return userRepository.findByUsername(username).isEnabled();
    }

    @Override
    public boolean emailMatches(String email, String username) {
        WAUser waUser = userRepository.findByUsername(username);
        return Objects.equals(waUser.getEmail(), email);
    }

    @Override
    public boolean checkIfEmailExists(String email) {
        return userRepository.findOptionalByEmail(email).isPresent();
    }

    @Override
    public boolean checkIfTokenExpired(WAUser wauser) {
        WAUser user = userRepository.findByUsername(wauser.getUsername());
        for (int i = 0; i < user.getListOfConfirmationTokens().size(); i++) {
            if(user.getListOfConfirmationTokens().get(i).getConfirmedAt() == null){
                return true;
            }
        }
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WAUser waUser = userRepository.findByUsername(username);
        if (waUser == null) {
            log.error("Player not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("Player found in the database: {}", username);
        }

        return new User(waUser.getUsername(), waUser.getPassword(), waUser.getAuthorities());
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
