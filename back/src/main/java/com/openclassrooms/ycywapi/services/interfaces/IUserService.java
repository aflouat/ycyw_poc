package com.openclassrooms.ycywapi.services.interfaces;

import com.openclassrooms.ycywapi.models.User;
import com.openclassrooms.ycywapi.payload.request.LoginRequest;
import com.openclassrooms.ycywapi.payload.request.SignupRequest;
import com.openclassrooms.ycywapi.payload.request.UserCredentialUpdateRequest;
import com.openclassrooms.ycywapi.payload.response.JwtResponse;

public interface IUserService  {
    public void register(SignupRequest signupRequest);
    public JwtResponse authenticate(LoginRequest loginRequest);
    public JwtResponse getConnectedUserJwtResponse();
    public User findUserByIdentifier(String identifier);
    public User getConnectedUser();
    public void updateUser(UserCredentialUpdateRequest userCredentialUpdateRequest);


}
