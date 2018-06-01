package com.bkic.lymenglong.audiobookbkic.account.utils;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String username;
    private String phonenumber;
    private String identitynumber;
    private String address;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String getCity;

    public User( String firstName, String lastName, String username, String email,  String phonenumber, String address) {
        this.email = email;
        this.username = username;
        this.phonenumber = phonenumber;
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    private String birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getIdentitynumber() {
        return identitynumber;
    }

    public void setIdentitynumber(String identitynumber) {
        this.identitynumber = identitynumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGetCity(String getCity) {
        this.getCity = getCity;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getGetCity() {
        return getCity;
    }
}
