package com.example.booking.hotel;

import com.example.booking.booking.BookingItemRepository;
import com.example.booking.booking.BookingStatus;
import com.example.booking.booking.BookingType;
import com.example.booking.common.exception.BadRequestException;
import com.example.booking.common.exception.ForbiddenException;
import com.example.booking.common.exception.ResourceNotFoundException;
import com.example.booking.vendor.Vendor;
import com.example.booking.vendor.VendorService;
import com.example.booking.vendor.VendorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final BookingItemRepository bookingItemRepository;
    private final VendorService vendorService;

    @Transactional
    public HotelResponse addHotel(HotelRequest req) {
        Vendor vendor = vendorService.approvedVendor(VendorType.HOTEL);
        Hotel h = new Hotel();
        h.setVendor(vendor);
        h.setName(req.name());
        h.setCity(req.city().trim());
        h.setAddress(req.address());
        h.setDescription(req.description());
        return toResponse(hotelRepository.save(h));
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> myHotels() {
        Long vendorId = vendorService.currentVendor().getId();
        return hotelRepository.findByVendorId(vendorId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public RoomResponse addRoom(Long hotelId, RoomRequest req) {
        Vendor vendor = vendorService.approvedVendor(VendorType.HOTEL);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
        if (!hotel.getVendor().getId().equals(vendor.getId())) {
            throw new ForbiddenException("This hotel does not belong to you");
        }
        Room r = new Room();
        r.setHotel(hotel);
        r.setRoomType(req.roomType());
        r.setPricePerNight(req.pricePerNight());
        r.setTotalRooms(req.totalRooms());
        Room saved = roomRepository.save(r);
        return new RoomResponse(saved.getId(), saved.getRoomType(), saved.getPricePerNight(),
                saved.getTotalRooms(), null);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> roomsOfHotel(Long hotelId) {
        Long vendorId = vendorService.currentVendor().getId();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
        if (!hotel.getVendor().getId().equals(vendorId)) {
            throw new ForbiddenException("This hotel does not belong to you");
        }
        return roomRepository.findByHotelId(hotelId).stream()
                .map(r -> new RoomResponse(r.getId(), r.getRoomType(), r.getPricePerNight(), r.getTotalRooms(), null))
                .toList();
    }

    /** City + dates + rooms ke hisaab se available hotels */
    @Transactional(readOnly = true)
    public List<HotelSearchResponse> search(String city, LocalDate checkIn, LocalDate checkOut, int rooms) {
        if (checkIn.isBefore(LocalDate.now()) || !checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Invalid dates");
        }
        if (rooms < 1) {
            throw new BadRequestException("rooms must be at least 1");
        }
        return hotelRepository.findByCityIgnoreCase(city.trim()).stream()
                .map(h -> {
                    List<RoomResponse> available = roomRepository.findByHotelId(h.getId()).stream()
                            .map(r -> {
                                long booked = bookingItemRepository.sumOverlap(BookingType.HOTEL, r.getId(),
                                        checkIn, checkOut, BookingStatus.ACTIVE);
                                int free = (int) Math.max(r.getTotalRooms() - booked, 0);
                                return new RoomResponse(r.getId(), r.getRoomType(), r.getPricePerNight(),
                                        r.getTotalRooms(), free);
                            })
                            .filter(rr -> rr.availableRooms() >= rooms)
                            .toList();
                    return new HotelSearchResponse(h.getId(), h.getName(), h.getCity(), h.getAddress(),
                            h.getDescription(), available);
                })
                .filter(hs -> !hs.rooms().isEmpty())
                .toList();
    }

    private HotelResponse toResponse(Hotel h) {
        return new HotelResponse(h.getId(), h.getName(), h.getCity(), h.getAddress(), h.getDescription());
    }
}
