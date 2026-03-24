package edu.hotel.booking.repository;

import edu.hotel.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByRoomTypeId(Long roomTypeId);

    @Query(value = """
            SELECT r.* FROM room r
            WHERE r.room_type_id = :roomTypeId
            AND r.status = 'AVAILABLE'
            AND r.id NOT IN (
                SELECT b.room_id FROM booking b
                WHERE b.status NOT IN ('CANCELLED')
                AND b.check_in < :checkOut
                AND b.check_out > :checkIn
            )
            LIMIT 1
            """, nativeQuery = true)
    Optional<Room> findFirstAvailableRoom(
            @Param("roomTypeId") Long roomTypeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}
