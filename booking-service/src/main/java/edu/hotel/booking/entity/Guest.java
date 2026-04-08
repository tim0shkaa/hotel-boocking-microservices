package edu.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "guest")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"birthDate", "phoneNumber"})
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String surname;

    private String patronymic;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String phoneNumber;
}
