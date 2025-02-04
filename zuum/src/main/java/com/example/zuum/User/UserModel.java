package com.example.zuum.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.User.CustomValidation.MaxAge;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "`user`", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class UserModel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank
    @NotNull
    private String name;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserType userType;

    @Pattern(regexp="^[0-9]+$", message = "Invalid cellphone number")
    private String cellphone;
    
    @MaxAge(value = 100, message = "Maximum age allowed is 100")
    private LocalDate birthday;

    @OneToMany(mappedBy = "id")
    private List<RideModel> rides = new ArrayList<>();
    
    public UserModel() {}

    public UserModel(Integer id, String name, String email, UserType userType, String cellphone, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.cellphone = cellphone;
        this.birthday = birthday;
    }

    public UserModel(String name, String email, String password, String cellphone, LocalDate birthday) {
        this.id = null;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = UserType.PASSENGER;
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

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<RideModel> getRides() {
        return rides;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}