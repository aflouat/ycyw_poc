package com.openclassrooms.ycywapi.services.impl;

import com.openclassrooms.ycywapi.exception.BadRequestException;
import com.openclassrooms.ycywapi.mapper.UserMapper;
import com.openclassrooms.ycywapi.models.User;
import com.openclassrooms.ycywapi.models.UserPrincipal;
import com.openclassrooms.ycywapi.payload.request.LoginRequest;
import com.openclassrooms.ycywapi.payload.request.SignupRequest;
import com.openclassrooms.ycywapi.payload.request.UserCredentialUpdateRequest;
import com.openclassrooms.ycywapi.payload.response.JwtResponse;
import com.openclassrooms.ycywapi.repositories.UserRepository;

import com.openclassrooms.ycywapi.security.JwtUtils;
import com.openclassrooms.ycywapi.services.interfaces.IUserDetailsService;
import com.openclassrooms.ycywapi.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final UserMapper userMapper;
    private final IUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final com.openclassrooms.ycywapi.services.interfaces.IIntercomService intercomService;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public JwtResponse authenticate(LoginRequest loginRequest) throws AuthenticationException {
        String identifier = loginRequest.getIdentifier();
        // Vérifier si l'identifiant est un email ou un username
        User user = findUserByIdentifier(identifier);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtServiceImpl.generateToken(userPrincipal);

        String connectedUserEmail = userRepository.findByUsername(userPrincipal.getUsername()).getEmail();

        // Generate Intercom artifacts (optional)
        String intercomJwt = null;
        String intercomUserHash = null;
        try {
            intercomJwt = intercomService.generateIdentityVerificationJwt(userPrincipal);
            intercomUserHash = intercomService.generateUserHash(String.valueOf(userPrincipal.getId()));
        } catch (Exception e) {
            logger.warn("Intercom identity artifacts not generated: {}", e.getMessage());
        }

        // Defensive check: ensure app token and intercomJwt are not identical
        if (intercomJwt != null && intercomJwt.equals(jwt)) {
            logger.warn("Intercom JWT is identical to application token; dropping intercomJwt to prevent misuse. Check your configuration: intercom.identity.secret should differ from jwt.secret.");
            intercomJwt = null;
        }

        return JwtResponse.builder()
                .id(userPrincipal.getId())
                .email(connectedUserEmail)
                .username(userPrincipal.getUsername())
                .token(jwt)
                .intercomJwt(intercomJwt)
                .intercomUserHash(intercomUserHash)
                .build();
    }

    public User findUserByIdentifier(String identifier) {
        User user = findUserByEmail(identifier);

        // Si aucun utilisateur trouvé par email, rechercher par username
        if (user == null) {
            user = userRepository.findByUsername(identifier);
        }
        return user;
    }

    private User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }
    private User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public User getConnectedUser()  {
        // Vérifier si l'utilisateur est authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException();
        }

        // Récupérer l'email de l'utilisateur actuellement connecté
        String identifier = authentication.getName();

        // Trouver l'utilisateur par son email
        User user = this.findUserByIdentifier(identifier);
        if (user==null){
            throw new UsernameNotFoundException("Utilisateur non trouve");
        }
        return user;
    }

    @Override
    public void updateUser(UserCredentialUpdateRequest userCredentialUpdateRequest) {
        // Récupérer l'utilisateur connecté
        User foundUser = this.getConnectedUser();

        if (foundUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté trouvé.");
        }

        // Vérifier si l'email est déjà utilisé par un autre utilisateur
        User userByEmail = userRepository.findByEmail(userCredentialUpdateRequest.getEmail());

        if (userByEmail != null && !userByEmail.getId().equals(foundUser.getId())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur.");
        }

        User userByUsername = userRepository.findByUsername(userCredentialUpdateRequest.getUsername());
        if (userByUsername != null && !userByUsername.getId().equals(foundUser.getId())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé par un autre utilisateur.");
        }

        if(userCredentialUpdateRequest.getEmail()!= null && !userCredentialUpdateRequest.getEmail().equals(foundUser.getEmail())) {
            foundUser.setEmail(userCredentialUpdateRequest.getEmail());
        }

        if(userCredentialUpdateRequest.getUsername()!= null && !userCredentialUpdateRequest.getUsername().equals(foundUser.getUsername())) {
            foundUser.setUsername(userCredentialUpdateRequest.getUsername());
        }

        if(userCredentialUpdateRequest.getPassword()!= null && !userCredentialUpdateRequest.getPassword().equals(foundUser.getPassword())
        && !userCredentialUpdateRequest.getPassword().isEmpty()) {
            foundUser.setPassword(getEncodedPassword(userCredentialUpdateRequest.getPassword()));
        }
        userRepository.save(foundUser);
    }

    private String getEncodedPassword(String password) {
        return encoder.encode(password); // Assurez-vous que le mot de passe est hashé
    }

    @Override
    public JwtResponse getConnectedUserJwtResponse()  {
         User user = getConnectedUser();
         logger.debug("getConnectedUserJwtResponse: {}", user);
         // Populate Intercom artifacts if possible
         String intercomJwt = null;
         String intercomUserHash = null;
         try {
             UserPrincipal principal = UserPrincipal.builder()
                     .id(user.getId())
                     .email(user.getEmail())
                     .username(user.getUsername())
                     .password(user.getPassword())
                     .build();
             intercomJwt = intercomService.generateIdentityVerificationJwt(principal);
             intercomUserHash = intercomService.generateUserHash(String.valueOf(user.getId()));
         } catch (Exception e) {
             logger.warn("Intercom identity artifacts not generated for current user: {}", e.getMessage());
         }

         JwtResponse jwtResponse = JwtResponse.builder()
                 .id(user.getId())
                 .email(user.getEmail())
                 .username(user.getUsername())
                 .type("Bearer")
                 .intercomJwt(intercomJwt)
                 .intercomUserHash(intercomUserHash)
                 .build();

         return jwtResponse;
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void register(SignupRequest signUpRequest) {
        if (userDetailsService.isUserExists(signUpRequest.getEmail()) ) {
            throw new RuntimeException("On ne peut pas créer deux utilisateurs avec " +
                    "le même e-mail!");
        }

        // Create new user's account
        User user = User.builder().email(signUpRequest.getEmail()).username(signUpRequest.getUsername())
                .password(encoder.encode(signUpRequest.getPassword())).build();
        User savedUser = userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }
//TODO
    public User fetchUserByToken(String token) {
        String email = jwtServiceImpl.extractIdentifier(token);
        User user = userRepository.findByEmail(email);
        return  user;
    }
}