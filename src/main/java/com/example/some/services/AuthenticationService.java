package com.example.some.services;


import com.example.some.entities.User;
//import com.example.some.model.Role;
import com.example.some.dto.auth.UserDTO;
import com.example.some.dto.auth.UserLoginResponseDTO;
import com.example.some.repositories.UserRepository;
import com.example.some.security.JwtTokenProvider;
import com.example.some.util.constants.MessageConstants;
import com.example.some.util.constants.ValidationConstants;
import com.example.some.util.formatters.StringFormatter;
import com.example.some.util.security.PasswordUtils;
import com.example.some.util.security.TokenUtils;
import com.example.some.util.validation.EmailValidator;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @Value("${app.backend.url}")
    private String backEndUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ModelMapper modelMapper;

    public User createUser(User user)  {
        if (!EmailValidator.isValid(user.getEmail())) {
            throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Invalid email format");
        }
        if (!EmailValidator.isValidLength(user.getEmail())) {
            throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Email too long");
        }
        if (user.getUsername().length() < ValidationConstants.USERNAME_MIN_LENGTH ||
                user.getUsername().length() > ValidationConstants.USERNAME_MAX_LENGTH) {
            throw new RuntimeException(MessageConstants.VALIDATION_ERROR +
                    ": Username must be between " + ValidationConstants.USERNAME_MIN_LENGTH +
                    " and " + ValidationConstants.USERNAME_MAX_LENGTH + " characters");
        }

        user.setEmail(StringFormatter.sanitize(user.getEmail()));
        user.setUsername(StringFormatter.sanitize(user.getUsername()));
        user.setFullName(StringFormatter.sanitize(user.getFullName()));
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Email already registered");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Username already taken");
        }

        user.setActive(false);

        String confirmationToken = TokenUtils.generateToken();
        user.setResetToken(confirmationToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusDays(1));


        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(PasswordUtils.hashPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        String confirmationLink = backEndUrl + "/api/auth/confirm-email?token=" + confirmationToken;
        try {
            emailService.sendEmailConfirm(user.getEmail(), "Confirm Your Email", confirmationLink);
        } catch(MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }
        //user.setCreatedAt(LocalDateTime.now());
        //user.setUpdatedAt(LocalDateTime.now());
        //user.setRole(Role.USER);
        return savedUser;
    }

    public UserLoginResponseDTO login(String usernameOrEmail, String password) {

        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException(MessageConstants.USER_NOT_FOUND)
        );
        if (!user.isActive()) {
            throw new BadCredentialsException("Please confirm your email address before logging in");
        }
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password);
            Authentication authentication = authenticationManager.authenticate(authToken);
            String jwt = tokenProvider.generateToken(user);
            UserLoginResponseDTO response = new UserLoginResponseDTO();
            response.setToken(jwt);
            response.setUser(mapToDTO(user));
            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(MessageConstants.INVALID_CREDENTIALS);
        }
    }

    public UserLoginResponseDTO me(UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = tokenProvider.generateToken(user);

        UserLoginResponseDTO response = new UserLoginResponseDTO();
        response.setToken(jwt);
        response.setUser(mapToDTO(user));
        return response;
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //String token = UUID.randomUUID().toString();
        String token = TokenUtils.generateResetToken();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 1 hour
        userRepository.save(user);

        String resetLink = frontEndUrl + "/reset-password?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "To reset your password, click the link: " + resetLink
        );
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid password reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Password reset link has expired. Please request a new one");
        }

        //user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(PasswordUtils.hashPassword(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        //if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            throw new IllegalStateException("Invalid old password");
        }
        //user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(PasswordUtils.hashPassword(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void confirmEmail(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid confirmation token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Confirmation token has expired");
        }

        user.setActive(true);
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public User updateProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(MessageConstants.USER_NOT_FOUND));

        // Email validation
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            String sanitizedEmail = StringFormatter.sanitize(updatedUser.getEmail());
            if (!EmailValidator.isValid(sanitizedEmail)) {
                throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Invalid email format");
            }
            if (!user.getEmail().equals(sanitizedEmail) &&
                    userRepository.existsByEmail(sanitizedEmail)) {
                throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Email already taken");
            }
            user.setEmail(sanitizedEmail);
        }

        // Username validation
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
            String sanitizedUsername = StringFormatter.sanitize(updatedUser.getUsername());
            if (!user.getUsername().equals(sanitizedUsername) &&
                    userRepository.existsByUsername(sanitizedUsername)) {
                throw new RuntimeException(MessageConstants.VALIDATION_ERROR + ": Username already taken");
            }
            user.setUsername(sanitizedUsername);
        }

        // Update other fields
        if (updatedUser.getFullName() != null && !updatedUser.getFullName().isEmpty()) {
            user.setFullName(StringFormatter.sanitize(updatedUser.getFullName()));
        }

        if (updatedUser.getBorn() != null) {
            user.setBorn(updatedUser.getBorn());
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.isActive()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            if (user.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    private UserDTO mapToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO save(@Valid UserDTO userDto) {
        validateUserDto(userDto);
        User user = modelMapper.map(userDto, User.class);
        if (userDto.getId() == null || userDto.getId() == 0) {
            user.setId(null);
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Email already registered");
            }
            String password = PasswordUtils.generateRandomPassword(20);
            String hashedPassword = PasswordUtils.hashPassword(password);
            user.setPassword(hashedPassword);
            //user.setRole(Role.USER);//Why here set an extra role?
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserDTO.class);
        } else {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));
            existingUser.setEmail(user.getEmail());
            existingUser.setFullName(user.getFullName());

            User savedUser = userRepository.save(existingUser);
            return modelMapper.map(savedUser, UserDTO.class);
        }
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if(user.isActive()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            if (user.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
                //Collections.singletonList(new SimpleGrantedAuthority("ROLE_see roll"))
        );
    }

    private void validateUserDto(UserDTO userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (userDto.getFullName() == null || userDto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        // Add additional validation as needed
    }
}
