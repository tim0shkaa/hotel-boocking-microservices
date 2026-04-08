package edu.hotel.booking.dto.roomtype;

import edu.hotel.booking.dto.tariff.TariffResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomTypeResponse {

    private Long id;

    private String name;

    private String description;

    private Integer capacity;

    private List<String> amenities;

    private List<String> photos;

    private BigDecimal minPriceNight;

    private List<TariffResponse> tariffs;

}
