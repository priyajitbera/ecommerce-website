package com.priyajit.ecommerce.user.management.service.service.impl;

import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.entity.User;
import com.priyajit.ecommerce.user.management.entity.UserSecret;
import com.priyajit.ecommerce.user.management.entity.enums.EmailVerificationStatus;
import com.priyajit.ecommerce.user.management.exception.NullArgumentException;
import com.priyajit.ecommerce.user.management.exception.UserAlreadyExistsException;
import com.priyajit.ecommerce.user.management.model.SignupModel;
import com.priyajit.ecommerce.user.management.repository.RoleRepository;
import com.priyajit.ecommerce.user.management.repository.UserRepository;
import com.priyajit.ecommerce.user.management.service.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

@Service
public class SignupServiceImpl implements SignupService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public SignupServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public SignupModel signup(SignupDto dto) {
        if (dto == null) throw new NullArgumentException("dto", SignupDto.class);

        var userBuilder = User.builder();

        if (dto.getEmailId() == null) throw new NullArgumentException("emailId", String.class);

        // validate if user exists with User
        var existingUserWithEmailId = userRepository.findByEmailId(dto.getEmailId());
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
}
