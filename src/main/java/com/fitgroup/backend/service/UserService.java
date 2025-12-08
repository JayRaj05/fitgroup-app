package com.fitgroup.backend.service;


import com.fitgroup.backend.dto.RegisterRequest;
import com.fitgroup.backend.entity.User;
import com.fitgroup.backend.repository.UserRepository;
import com.fitgroup.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;   //

    public String registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "EMAIL_ALREADY_EXISTS";
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .age(request.getAge())
                .weightKg(request.getWeightKg())
                .build();

        userRepository.save(user);

        return "USER_REGISTERED_SUCCESSFULLY";
    }

    //  FINAL JWT LOGIN METHOD
    public String loginUserAndGenerateToken(String email, String rawPassword) {

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "USER_NOT_FOUND";
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return "INVALID_PASSWORD";
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
