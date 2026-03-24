package edu.hotel.booking.repository;

import edu.hotel.booking.entity.RoomType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    List<RoomType> findByHotelId(Long hotelId);

    @EntityGraph(attributePaths = {"tariffs"})
    List<RoomType> findWithTariffsByHotelId(Long hotelId);
}
