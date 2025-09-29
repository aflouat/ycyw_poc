package com.openclassrooms.ycywapi.payload.request;


import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserCredentialUpdateRequest {
  @Email
  private String email;

  private String username;

  private String password;
}
