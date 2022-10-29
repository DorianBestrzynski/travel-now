package com.zpi.userservice.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
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
public class AppUser {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
           )
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence", allocationSize = 10)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
    @Getter
    @Column(name = "username", nullable = false, length = 50)
    private String username;

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
    private LocalDateTime registrationDate = LocalDateTime.now();

    @JsonManagedReference
    @OneToOne(cascade=CascadeType.ALL, mappedBy = "appUser", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Password password;

    public AppUser(String username, String email, String firstName, String surname, LocalDate birthday, Password password){
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.surname = surname;
        this.birthday = birthday;
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
