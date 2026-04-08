package edu.hotel.booking.repository;

import edu.hotel.booking.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByUserId(Long userId);
}
