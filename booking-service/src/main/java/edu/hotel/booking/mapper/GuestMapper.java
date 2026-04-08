package edu.hotel.booking.mapper;

import edu.hotel.booking.dto.guest.GuestRequest;
import edu.hotel.booking.dto.guest.GuestResponse;
import edu.hotel.booking.entity.Guest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface GuestMapper {

    GuestResponse toResponse(Guest guest);

    Guest toEntity(GuestRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFrom(GuestRequest request, @MappingTarget Guest guest);
}
