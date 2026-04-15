package com.jobportal.dto;
public class AuthResponse {
    private String token;
    public AuthResponse(String t){this.token=t;}
    public String getToken(){return token;}
    public void setToken(String t){this.token=t;}
}
