package com.example.tfgproject.entities;

import java.io.Serializable;

public class User implements Serializable {
    private String userid;
    private String email;
    private String nif;
    private String name;
    private String dateOfBirth;
    private String phone;

    public User(){}

    public User(String userid, String email, String nif, String name, String dateOfBirth, String phone) {
        this.userid = userid;
        this.email = email;
        this.nif = nif;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}