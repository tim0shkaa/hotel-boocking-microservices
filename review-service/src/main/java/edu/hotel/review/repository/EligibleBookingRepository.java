package edu.hotel.review.repository;

import edu.hotel.review.entity.EligibleBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EligibleBookingRepository extends JpaRepository<EligibleBooking, Long> {

    boolean existsByBookingIdAndGuestId(Long bookingId, Long guestId);
}
