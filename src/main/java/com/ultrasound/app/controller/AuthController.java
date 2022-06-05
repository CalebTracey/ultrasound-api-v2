package com.ultrasound.app.controller;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import com.ultrasound.app.security.jwt.JwtUtils;
import com.ultrasound.app.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ultrasound.app.model.user.ERole;
import com.ultrasound.app.model.user.Role;
import com.ultrasound.app.model.user.AppUser;
import com.ultrasound.app.payload.request.LoginRequest;
import com.ultrasound.app.payload.request.RegisterRequest;
import com.ultrasound.app.payload.response.JwtResponse;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.repo.RoleRepo;
import com.ultrasound.app.repo.AppUserRepo;
import com.ultrasound.app.security.service.UserDetailsImpl;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepo userRepository;
    private final RoleRepo roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final  UserDetailsServiceImpl detailsService;

    @GetMapping("/user/{username}")
    public ResponseEntity<?> userData(@PathVariable String username) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/{username}").toUriString());
        return ResponseEntity.created(uri).body(detailsService.loadUserByUsername(username));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody @NotNull LoginRequest loginRequest) throws IllegalAccessException {
        return getAuthenticatedResponse(loginRequest.getUsername(),loginRequest.getPassword());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> registerUser(@Valid @RequestBody @NotNull RegisterRequest registerRequest) {
        MessageResponse messageResponse = new MessageResponse();
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            messageResponse.setMessage("Error: Username is already taken!");
            return ResponseEntity
                    .badRequest()
                    .body(messageResponse.getMessage());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            messageResponse.setMessage("Error: Email is already in use!");
            return ResponseEntity
                    .badRequest()
                    .body(messageResponse.getMessage());
        }

        // Create new user's account
        AppUser user = new AppUser(registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("admin".equals(role)) {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);

        //talk to Christie to get activated
        user.setApproved(false);

        userRepository.save(user);

        // now log them in
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/sign-up").toUriString());
        messageResponse.setMessage("Account created. You'll get an email when your account is approved and activated.");

        return ResponseEntity.created(uri).body(messageResponse.getMessage());
    }

    protected ResponseEntity<?> getAuthenticatedResponse(String userName, String password) throws IllegalAccessException {
        MessageResponse messageResponse = new MessageResponse();
        JwtResponse jwtResponse = new JwtResponse();
        // first check if the account has been approved
        Optional<AppUser> user = userRepository.findByUsername(userName);
        if (user.isPresent() && user.get().getApproved() != null && !user.get().getApproved()) {
            messageResponse.setMessage("Account pending approval");
            return ResponseEntity.badRequest().body(messageResponse.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        try {
            jwtResponse.setToken(jwt);
            jwtResponse.setId(userDetails.getId());
            jwtResponse.setUsername(userDetails.getUsername());
            jwtResponse.setEmail(userDetails.getEmail());
            jwtResponse.setRoles(roles);
        } catch (IncorrectResultSizeDataAccessException ex){
            throw new IllegalAccessException(ex.getLocalizedMessage());
        }

        return ResponseEntity.ok().body(jwtResponse);
    }
}
