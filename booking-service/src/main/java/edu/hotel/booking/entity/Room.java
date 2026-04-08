package edu.hotel.booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import edu.hotel.booking.model.RoomStatus;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"roomType"})
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private Integer roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;
}
