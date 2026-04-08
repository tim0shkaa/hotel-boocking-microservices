package edu.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import edu.hotel.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_status_history")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "booking")
public class BookingStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(nullable = false)
    private String changedBy;

    private String reason;
}
