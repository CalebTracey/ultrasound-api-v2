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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AppUserRepo userRepository;
    @Autowired
    RoleRepo roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl detailsService;

    @GetMapping("/user/{username}")
    public ResponseEntity<?> userData(@PathVariable String username) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/{username}").toUriString());
        return ResponseEntity.created(uri).body(detailsService.loadUserByUsername(username));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody @NotNull LoginRequest loginRequest) {
        return getAuthenticatedResponse(loginRequest.getUsername(),loginRequest.getPassword());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody @NotNull RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
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
        //return getAuthenticatedResponse(registerRequest.getUsername(),registerRequest.getPassword());
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/sign-up").toUriString());
        return ResponseEntity.created(uri).body(new MessageResponse("Account created. You'll get an email when your account is approved and activated."));
    }

    protected ResponseEntity<?> getAuthenticatedResponse(String userName, String password) {
        // first check if the account has been approved
        Optional<AppUser> user = userRepository.findByUsername(userName);
        if (user.isPresent() && user.get().getApproved() != null && user.get().getApproved() == false) {
            return ResponseEntity.badRequest().body(new MessageResponse("Account pending approval"));
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}
