package com.example.zuum.User;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name = "`user`", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserType userType;
    
    private String cellphone;

    
    private LocalDate birthday;

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

}