package com.zpi.authorizationserver.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
//@NoArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
           )
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
    @Getter
    @Column(name = "phoneNumber", nullable = false, length = 50)
    private String phoneNumber;

    @Column(name = "email", nullable = false, unique = true ,length = 150)
    private String email;
    @Getter
    @Column(name = "first_name", length = 50)
    private String firstName;
    @Getter
    @Column(name = "surname", length = 50)
    private String surname;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @JsonManagedReference
    @OneToOne(cascade=CascadeType.ALL, mappedBy = "appUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Password password;

    public AppUser(String phoneNumber, String email, String firstName, String surname, LocalDate birthday, LocalDateTime registrationDate, Password password){
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.surname = surname;
        this.birthday = birthday;
        this.registrationDate = registrationDate;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUser appUser = (AppUser) o;
        return userId != null && Objects.equals(userId, appUser.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
