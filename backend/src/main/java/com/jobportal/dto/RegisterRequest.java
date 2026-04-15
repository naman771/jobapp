package com.jobportal.dto;
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;
    private String securityQuestion;
    private String securityAnswer;

    public String getName(){return name;}
    public void setName(String n){this.name=n;}
    public String getEmail(){return email;}
    public void setEmail(String e){this.email=e;}
    public String getPassword(){return password;}
    public void setPassword(String p){this.password=p;}
    public String getRole(){return role;}
    public void setRole(String r){this.role=r;}
    public String getSecurityQuestion(){return securityQuestion;}
    public void setSecurityQuestion(String sq){this.securityQuestion=sq;}
    public String getSecurityAnswer(){return securityAnswer;}
    public void setSecurityAnswer(String sa){this.securityAnswer=sa;}
}
