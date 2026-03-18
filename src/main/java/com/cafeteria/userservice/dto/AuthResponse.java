package com.cafeteria.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
  private String token;

  @Default
  private String type = "Bearer";

  private Long id;
  private String name;
  private String email;
  private String role;

  public AuthResponse(String token, Long id, String name, String email, String role) {
    this.token = token;
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
    this.type = "Bearer";
  }
}
