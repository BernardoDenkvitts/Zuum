package com.example.zuum.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.zuum.Trip.TripModel;
import com.example.zuum.User.CustomValidation.MaxAge;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "`user`", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;

    @Email(message = "Invalid email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserType userType;

    @Pattern(regexp="^[0-9]+$", message = "Invalid cellphone number")
    private String cellphone;
    
    @MaxAge(value = 100, message = "Maximum age allowed is 100")
    private LocalDate birthday;

    @OneToMany(mappedBy = "id")
    private List<TripModel> trips = new ArrayList<>();
    
    public UserModel() {}

    public UserModel(Integer id, String name, String email, UserType userType, String cellphone, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.cellphone = cellphone;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public UserType getUserType() {
        return userType;
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

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public List<TripModel> getTrips() {
        return trips;
    }

}