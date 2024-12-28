package com.example.some.service;


import com.example.some.entity.User;
//import com.example.some.model.Role;
import com.example.some.dto.UserDto;
import com.example.some.dto.LoginRespDto;
import com.example.some.repository.UserRepository;
import com.example.some.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Value("${app.reset-password.url}")
    private String resetPasswordUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ModelMapper modelMapper;

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //user.setCreatedAt(LocalDateTime.now());
        //user.setUpdatedAt(LocalDateTime.now());
        //user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public LoginRespDto login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = tokenProvider.generateToken(user);

        LoginRespDto response = new LoginRespDto();
        response.setToken(jwt);
        response.setUser(mapToDTO(user));
        return response;
    }

    public LoginRespDto me(UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = tokenProvider.generateToken(user);

        LoginRespDto response = new LoginRespDto();
        response.setToken(jwt);
        response.setUser(mapToDTO(user));
        return response;
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(System.currentTimeMillis() + 3600000); // 1 hour
        userRepository.save(user);

        String resetLink = resetPasswordUrl + token;
        emailService.sendEmail(
                user.getEmail(),
                "Password Reset Request",
                "To reset your password, click the link: " + resetLink
        );
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid token"));

        if (user.getResetTokenExpiry() < System.currentTimeMillis()) {
            throw new IllegalStateException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalStateException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User updateProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getEmail() != null &&
                !user.getEmail().equals(updatedUser.getEmail()) &&
                userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_see roll"))
        );
    }


    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(user);
    }

    private UserDto mapToDTO(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDto save(@Valid UserDto userDto) {
        validateUserDto(userDto);
        User user = modelMapper.map(userDto, User.class);
        if (userDto.getId() == null || userDto.getId() == 0) {
            user.setId(null);
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Email already registered");
            }
            user.setPassword(passwordEncoder.encode("defaultPassword123"));
            //user.setRole(Role.USER);//Why here set an extra role?
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserDto.class);
        } else {
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));
            existingUser.setEmail(user.getEmail());
            existingUser.setFullName(user.getFullName());

            User savedUser = userRepository.save(existingUser);
            return modelMapper.map(savedUser, UserDto.class);
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(String search, Pageable pageable) {
        // Get the currently logged-in user's ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        Page<User> users;
        if (search != null && !search.trim().isEmpty()) {
            // Assuming you'll add a search method in repository
            //users = userRepository.searchByNameAndEmail(search.trim(), pageable);
            users = userRepository.findByIdNotAndSearchCriteria(currentUser.getId(), search.trim(), pageable);
        } else {
            //users = userRepository.findAll(pageable);
            users = userRepository.findByIdNot(currentUser.getId(), pageable);
        }
        return users.map(user -> modelMapper.map(user, UserDto.class));
    }

    public UserDto saveUser(UserDto userDto) {
        validateUserDto(userDto);

        User user = modelMapper.map(userDto, User.class);

        // For new users
        if (user.getId() == null) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Email already registered");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            //user.setRole(Role.USER); // Default role
        } else {
            // If updating existing user, preserve certain fields
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));

            // Preserve sensitive fields
            user.setPassword(existingUser.getPassword());
            //user.setRole(existingUser.getRole());
            user.setResetToken(existingUser.getResetToken());
            user.setResetTokenExpiry(existingUser.getResetTokenExpiry());
        }

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }


    public UserDto updateUserByAdm(Long userId, UserDto userDto) {
        // Get the currently logged-in user's ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        // Prevent self-update through admin endpoint
        if (currentUser.getId().equals(userId)) {
            throw new IllegalStateException("Cannot update your own account through admin endpoint. Please use the profile update endpoint.");
        }
        validateUserDto(userDto);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check email uniqueness if it's being changed
        if (!existingUser.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email already taken");
        }

        // Preserve sensitive data
        String currentPassword = existingUser.getPassword();
        //Role currentRole = existingUser.getRole();
        String resetToken = existingUser.getResetToken();
        Long resetTokenExpiry = existingUser.getResetTokenExpiry();

        // Map the updated data
        modelMapper.map(userDto, existingUser);

        // Restore preserved data
        existingUser.setId(userId);
        existingUser.setPassword(currentPassword);
        //existingUser.setRole(currentRole);
        existingUser.setResetToken(resetToken);
        existingUser.setResetTokenExpiry(resetTokenExpiry);

        User savedUser = userRepository.save(existingUser);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto updateUser(Long userId, @Valid UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFullName(userDto.getFullName());
        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    public void deleteUser(Long userId) {
        // Get the currently logged-in user's ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        // Prevent self-deletion
        if (currentUser.getId().equals(userId)) {
            throw new IllegalStateException("Cannot delete your own account");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Add any necessary validation before deletion
        // For example, check if user has any active operations
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_see roll"))
        );
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (userDto.getFullName() == null || userDto.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        // Add additional validation as needed
    }
}
