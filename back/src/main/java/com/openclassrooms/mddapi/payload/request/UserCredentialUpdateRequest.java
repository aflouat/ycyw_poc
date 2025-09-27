package com.openclassrooms.mddapi.payload.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCredentialUpdateRequest {
  @Email
  private String email;

  private String username;

  private String password;
}
