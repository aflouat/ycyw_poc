package com.openclassrooms.mddapi.services.interfaces;

import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.payload.request.LoginRequest;
import com.openclassrooms.mddapi.payload.request.SignupRequest;
import com.openclassrooms.mddapi.payload.request.UserCredentialUpdateRequest;
import com.openclassrooms.mddapi.payload.response.JwtResponse;

public interface IUserService  {
    public void register(SignupRequest signupRequest);
    public JwtResponse authenticate(LoginRequest loginRequest);
    public JwtResponse getConnectedUserJwtResponse();
    public User findUserByIdentifier(String identifier);
    public User getConnectedUser();
    public void updateUser(UserCredentialUpdateRequest userCredentialUpdateRequest);


}
