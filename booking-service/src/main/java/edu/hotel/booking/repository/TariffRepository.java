package edu.hotel.booking.repository;

import edu.hotel.booking.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {

    @Query("""
            SELECT t FROM Tariff t
            WHERE t.roomType.id = :roomTypeId
            AND t.validFrom <= :date
            AND t.validTo >= :date
            """)
    List<Tariff> findActualByRoomTypeId(
            @Param("roomTypeId") Long roomTypeId,
            @Param("date") LocalDate date
    );

    Optional<Tariff> findByIdAndRoomTypeId(Long id, Long roomTypeId);
}