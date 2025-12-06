package com.jobportal.dto;
public class JobDto {
    private String title;
    private String company;
    private String description;
    private String location;
    private String salaryRange;
    private String jobType;
    private String skills;
    // getters/setters
    public String getTitle(){return title;}
    public void setTitle(String t){this.title=t;}
    public String getCompany(){return company;}
    public void setCompany(String c){this.company=c;}
    public String getDescription(){return description;}
    public void setDescription(String d){this.description=d;}
    public String getLocation(){return location;}
    public void setLocation(String l){this.location=l;}
    public String getSalaryRange(){return salaryRange;}
    public void setSalaryRange(String s){this.salaryRange=s;}
    public String getJobType(){return jobType;}
    public void setJobType(String jt){this.jobType=jt;}
    public String getSkills(){return skills;}
    public void setSkills(String s){this.skills=s;}
}
