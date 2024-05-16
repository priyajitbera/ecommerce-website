package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.component.EmailSender;
import com.priyajit.ecommerce.user.management.component.UserAuthTokenProvider;
import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.dto.RequestEmailVerificationSecretDto;
import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.dto.VerifyEmailDto;
import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.Role;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.UserSecret;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import com.priyajit.ecommerce.user.management.entity.enums.LoginAttemptStatus;
import com.priyajit.ecommerce.user.management.entity.enums.RequestEmailVerifcationSecretStatus;
import com.priyajit.ecommerce.user.management.exception.NullArgumentException;
import com.priyajit.ecommerce.user.management.exception.UserAlreadyExistsException;
import com.priyajit.ecommerce.user.management.model.*;
import com.priyajit.ecommerce.user.management.repository.RoleRepository;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImplV1 implements AuthService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserAuthTokenProvider userAuthTokenProvider;
    private EmailSender emailSender;

    public AuthServiceImplV1(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserAuthTokenProvider userAuthTokenProvider, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthTokenProvider = userAuthTokenProvider;
        this.emailSender = emailSender;
    }

    @Override
    public LoginModel login(LoginDto dto) {
        // null validations
        if (dto == null) throw new IllegalArgumentException("Unexpected null value found for argument dto:LoginDto");
        if (dto.getUserId() == null || dto.getPassword() == null)
            throw new IllegalArgumentException("Unexpected null value found for argument userId:Long, password:String");

        // find user
        User user = userRepository.findById(dto.getUserId())
                .orElse(null);

        // if not user found with id
        if (user == null) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("user-id or password is incorrect")
                    .build();
        }

        // validate email verification status
        if (EmailVerificationStatus.NOT_VERIFIED.equals(user.getEmailVerificationStatus())) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("Email verification is not complete")
                    .build();
        }

        // if UserSecret entity is not created or correctly mapped for the user
        if (user.getUserSecret() == null || user.getUserSecret().getPassword() == null) {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("User onboarding is not complete or unsuccessful, contact System Administrator")
                    .build();
        }

        // match password
        if (passwordEncoder.matches(dto.getPassword(), user.getUserSecret().getPassword())) {
            // fetch user's roles
            var roles = user.getRoles() == null ? List.<String>of() :
                    user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList());

            // generate toke with userId and roles
            String token = userAuthTokenProvider.generateToken(user.getId().toString(), roles);

            // build the response model and return
            return LoginModel.builder()
                    .status(LoginAttemptStatus.SUCCESS)
                    .userId(user.getId())
                    .name(user.getName())
                    .emailId(user.getEmailId())
                    .roles(roles)
                    .token(token)
                    .build();
        } else {
            return LoginModel.builder()
                    .status(LoginAttemptStatus.FAILED)
                    .message("UserId or Password is incorrect")
                    .build();
        }
    }

    @Override
    @Transactional
    public SignupModel signup(SignupDto dto) {
        if (dto == null) throw new NullArgumentException("dto", SignupDto.class);

        var userBuilder = User.builder();

        if (dto.getEmailId() == null) throw new NullArgumentException("emailId", String.class);

        // validate if user exists with User
        var existingUserWithEmailId = userRepository.findByEmailIdAndEmailVerificationStatus(
                dto.getEmailId(), EmailVerificationStatus.VERIFIED
        );
        if (existingUserWithEmailId.isPresent()) {
            throw new UserAlreadyExistsException(dto.getEmailId(), 0);
        }
        userBuilder.emailId(dto.getEmailId());

        // validation
        if (dto.getName() == null)
            throw new NullArgumentException("name", String.class);
        userBuilder.name(dto.getName());

        // validation
        if (dto.getRoles() == null || dto.getRoles().size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atleast one role selection is required");

        var roles = roleRepository.findAllByNameIn(dto.getRoles());
        userBuilder.roles(roles);

        // validation
        if (dto.getPassword() == null)
            throw new NullArgumentException("password", String.class);
        var userSecret = UserSecret.builder()
                .password(passwordEncoder.encode(dto.getPassword()))
                .passwordSetOn(ZonedDateTime.now())
                .build();

        var user = userBuilder.build();

        user.setUserSecret(userSecret);
        userSecret.setUser(user);

        user.setEmailVerificationStatus(EmailVerificationStatus.NOT_VERIFIED);

        userRepository.saveAndFlush(user);

        return SignupModel.builder()
                .userId(user.getId())
                .emailId(user.getEmailId())
                .name(user.getName())
                .build();
    }

    @Override
    public RequestEmailVerificationSecretModel requestEmailVerificationSecret(RequestEmailVerificationSecretDto dto) {


        var user = findUserById(dto.getUserId());

        RequestEmailVerificationSecretModel responseModel = RequestEmailVerificationSecretModel.builder()
                .userId(user.getId()).build();

        // verify if user is already verified
        if (EmailVerificationStatus.VERIFIED == user.getEmailVerificationStatus()) {

            responseModel.setStatus(RequestEmailVerifcationSecretStatus.FAILURE);
            responseModel.setMessage("EmailId is already verified");
            return responseModel;
        }

        // verify the email id is provided
        if (user.getEmailId() == null) {

            responseModel.setStatus(RequestEmailVerifcationSecretStatus.FAILURE);
            responseModel.setMessage(String.format("No EmailId is saved for userId: %s",
                    user.getId()));
            return responseModel;
        }

        // generate a new email verification secret
        user.setEmailVerificationSecret(UUID.randomUUID().toString());
        user.setEmailVerificationSecretGeneratedOn(ZonedDateTime.now());
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
    }

    @Override
    @Transactional
    public VerifyEmailModel verifyUserEmail(VerifyEmailDto verifyEmailDto) {

        // fetch the user
        var user = findUserById(verifyEmailDto.getUserId());

        // if the provided secret matches with secret stored in DB, then mark as verified
        if (Objects.equals(user.getEmailVerificationSecret(), verifyEmailDto.getEmailVerificationSecret())) {
            user.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
            user.setEmailVerifiedOn(ZonedDateTime.now());
            userRepository.saveAndFlush(user);
        }

        // send a confirmation email
        try {
            sendSuccessfulEmailValidationMail(
                    user.getEmailId(),
                    user.getEmailVerifiedOn()
            );
        } catch (Throwable t) {
            log.error("Error while sending EmailId verification success mail for userId:{} emailId:{}",
                    user.getId(), user.getEmailId());
            t.printStackTrace();
        }

        // create response model and return
        return VerifyEmailModel.builder()
                .userId(user.getId())
                .emailId(user.getEmailId())
                .verificationStatus(user.getEmailVerificationStatus())
                .verifiedOn(user.getEmailVerifiedOn())
                .build();
    }

    @Override
    public CheckEmailIdAvailableModel checkEmailIdAvailable(String emailId) {

        // fetch the verified user with this email if present, there must be exactly one such user
        var userOpt = userRepository.findByEmailIdAndEmailVerificationStatus(
                emailId,
                EmailVerificationStatus.VERIFIED
        );

        return CheckEmailIdAvailableModel.builder()
                .emailId(emailId)
                .available(userOpt.isEmpty())
                .build();
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
}