package com.openclassrooms.mddapi.payload.response;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
  private Long id;
  private String email;
  private String username;
  public String type;
  private String token; // Application JWT (Bearer)

  // Optional Intercom Identity Verification artifacts
  private String intercomJwt;      // short-lived JWT for Intercom Messenger secure mode
  private String intercomUserHash; // HMAC-SHA256(user_id or email)


}
