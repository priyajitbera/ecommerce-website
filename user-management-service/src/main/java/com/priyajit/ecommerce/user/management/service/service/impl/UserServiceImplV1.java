package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.component.EmailSender;
import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.UserSecret;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import com.priyajit.ecommerce.user.management.exception.NullArgumentException;
import com.priyajit.ecommerce.user.management.exception.UserNotFoundException;
import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class UserServiceImplV1 implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private EmailSender emailSender;

    public UserServiceImplV1(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailSender emailSender
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }


    @Override
    public FindUserModel findUser(BigInteger userId) {
        if (userId == null) throw new NullArgumentException("userId", BigInteger.class);

        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException.supplier(userId));

        return FindUserModel.buildFrom(user);
    }

    /**
     * Method to find user using ids
     *
     * @param userIds
     * @return
     */
    @Override
    public List<FindUserModel> findUsers(List<BigInteger> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(FindUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    @Override
    public List<FindUserModel> findUsersByEmailIds(List<String> emailIdList) {

        var users = userRepository.findAllByEmailIdInAndEmailVerificationStatus(
                emailIdList,
                EmailVerificationStatus.VERIFIED
        );

        return users.stream()
                .map(FindUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    private EmailClientStatus sendSuccessfulEmailValidationMail(String emailId, ZonedDateTime verifiedOn) {

        SendEmailDto emailDto = SendEmailDto.builder()
                .to(List.of(emailId))
                .subject("Email verification success!!")
                .body(String.format("Your email id %s is verified successfully on %s", emailId, verifiedOn.toString())) // todo : create email templates
                .correlationIds(Map.of("user-email-id", emailId, "source-service", "user-management-service"))
                .build();

        return emailSender.send(emailDto);
    }

    /**
     * Helper method to send email verification secret
     *
     * @param email                   the email to which secret to be send
     * @param emailVerificationSecret the secret to send
     * @return
     */
    private EmailClientStatus sendEmailVerificationSecret(String email, String emailVerificationSecret) {

        List<String> to = List.of(email);
        String emailBody = String.format("Your email verification secret is: %s", emailVerificationSecret);

        SendEmailDto emailDto = SendEmailDto.builder()
                .to(List.of(email))
                .subject("Verify your email")
                .body(emailBody)
                .correlationIds(Map.of("user-email-id", email, "source-service", "user-managerment-service"))
                .build();

        return emailSender.send(emailDto);
    }

    /**
     * Helper method to find a user using by userId
     *
     * @param userId key find user
     * @return the User object
     * @throws ResponseStatusException with 404-NOT_FOUND when no user found with given userId
     */
    private User findUserById(BigInteger userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Helper method create User object from CreateUserDto
     *
     * @param dto DTO containing new user data
     * @return
     */
    private User createUserFromDto(CreateUserDto dto) {

        String encodedPassword =
                dto.getPassword() == null ?
                        null :
                        passwordEncoder.encode(dto.getPassword());

        ZonedDateTime passwordSetOn = encodedPassword == null ? null : ZonedDateTime.now();

        User user = User.builder()
                .emailId(dto.getEmail())
                .name(dto.getName())
                .build();
        UserSecret userSecret = UserSecret.builder()
                .password(encodedPassword)
                .passwordSetOn(passwordSetOn)
                .build();

        user.setUserSecret(userSecret);
        userSecret.setUser(user);

        return user;
    }
}
