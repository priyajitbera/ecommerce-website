package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.component.EmailSender;
import com.priyajit.ecommerce.user.management.dto.CreateUserDto;
import com.priyajit.ecommerce.user.management.dto.RequestEmailVerificationSecretDto;
import com.priyajit.ecommerce.user.management.dto.VerifyEmailDto;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.UserSecret;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import com.priyajit.ecommerce.user.management.entity.enums.RequestEmailVerifcationSecretStatus;
import com.priyajit.ecommerce.user.management.exception.NullArgumentException;
import com.priyajit.ecommerce.user.management.exception.UserNotFoundException;
import com.priyajit.ecommerce.user.management.model.CreateUserModel;
import com.priyajit.ecommerce.user.management.model.FindUserModel;
import com.priyajit.ecommerce.user.management.model.RequestEmailVerificationSecretModel;
import com.priyajit.ecommerce.user.management.model.VerifyEmailModel;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * Method to create users
     *
     * @param dtoList
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CreateUserModel> createUsers(List<CreateUserDto> dtoList) {

        // create User objects from request dtos
        List<User> userList = dtoList.stream()
                .map(this::createUserFromDto)
                .collect(Collectors.toList());

        // for new users emails are not verified
        userList.forEach(user -> user.setEmailVerificationStatus(EmailVerificationStatus.NOT_VERIFIED));

        // save users
        List<User> savedUsers = userRepository.saveAllAndFlush(userList);

        // create response model and return
        return savedUsers.stream()
                .map(CreateUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    /**
     * Method to request email verification secrets
     *
     * @param dtoList
     * @return
     */
    @Override
    public List<RequestEmailVerificationSecretModel> requestEmailVerificationSecret(List<RequestEmailVerificationSecretDto> dtoList) {

        // fetch users for whom email verification secret is requested
        List<User> userList = dtoList.stream()
                .map(RequestEmailVerificationSecretDto::getUserId)
                .map(this::findUserById)
                .collect(Collectors.toList());

        List<RequestEmailVerificationSecretModel> responseModelList = userList.stream().map(user -> {
                    RequestEmailVerificationSecretModel responseModel = RequestEmailVerificationSecretModel.builder()
                            .userId(user.getId()).build();

                    // verify if user is already verified
                    if (EmailVerificationStatus.VERIFIED == user.getEmailVerificationStatus()) {

                        responseModel.setStatus(RequestEmailVerifcationSecretStatus.FAILURE);
                        responseModel.setMessage("Email id verification already complete for this user");
                        return responseModel;
                    }

                    // verify the email id is provided
                    if (user.getEmailId() == null) {

                        responseModel.setStatus(RequestEmailVerifcationSecretStatus.FAILURE);
                        responseModel.setMessage("Email id is not saved for this user");
                        return responseModel;
                    }

                    // generate a new email verification secret
                    user.setEmailVerificationSecret(UUID.randomUUID().toString());
                    user.setEmailVerificationSecretGeneratedOn(ZonedDateTime.now()); // audit
                    userRepository.saveAndFlush(user);

                    // send the secret via email to the email-id to be verified
                    EmailClientStatus emailClientStatus = sendEmailVerificationSecret(
                            user.getEmailId(), user.getEmailVerificationSecret()
                    );

                    // email send SUCCESS
                    if (EmailClientStatus.SUCCESS == emailClientStatus) {

                        responseModel.setStatus(RequestEmailVerifcationSecretStatus.SUCCESS);
                        responseModel.setMessage(String.format("Email verification secret sent to email id: %s", user.getEmailId()));
                        return responseModel;
                    }
                    // email send FAILURE
                    else if (EmailClientStatus.FAILURE == emailClientStatus) {

                        responseModel.setStatus(RequestEmailVerifcationSecretStatus.FAILURE);
                        responseModel.setMessage(String.format("Failed to send verification secret to email id: %s", user.getEmailId()));
                        return responseModel;
                    }
                    // ideally program control never reaches here as EmailClientStatus is either SUCCESS or FAILURE
                    return responseModel;
                })
                .collect(Collectors.toList());
        return responseModelList;
    }

    /**
     * Method to verify user email-ids
     *
     * @param
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<VerifyEmailModel> verifyUserEmails(List<VerifyEmailDto> dtoList) {

        return dtoList.stream().map(this::verifyUserEmail).collect(Collectors.toList());
    }

    /**
     * Method to find users by emailIds
     *
     * @param emailIdList
     * @return
     */
    @Override
    public List<FindUserModel> findUsersByEmailIds(List<String> emailIdList) {

        // find users by given email
        List<User> userList = userRepository.findAllByEmailIdIn(emailIdList);

        // create response model and return
        return userList.stream()
                .map(FindUserModel::buildFrom)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to verify single user email
     *
     * @param verifyEmailDto
     * @return
     */
    private VerifyEmailModel verifyUserEmail(VerifyEmailDto verifyEmailDto) {

        // fetch the user
        User user = findUserById(verifyEmailDto.getUserId());

        // if the provided secret matches with secret stored in DB, then mark as verified
        if (Objects.equals(user.getEmailVerificationSecret(), verifyEmailDto.getEmailVerificationSecret())) {
            user.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
            user.setEmailVerifiedOn(ZonedDateTime.now());
            userRepository.saveAndFlush(user);
        }

        sendSuccessfulEmailValidationMail(
                user.getEmailId(),
                user.getEmailVerifiedOn()
        );

        // create response model and return
        return VerifyEmailModel.builder()
                .userId(user.getId())
                .verificationStatus(user.getEmailVerificationStatus())
                .verifiedOn(user.getEmailVerifiedOn())
                .build();
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
