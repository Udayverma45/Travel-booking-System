package com.example.booking.rental;

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
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BookingItemRepository bookingItemRepository;
    private final VendorService vendorService;

    @Transactional
    public VehicleResponse add(VehicleRequest req) {
        Vendor vendor = vendorService.approvedVendor(VendorType.RENTAL);
        Vehicle v = new Vehicle();
        v.setVendor(vendor);
        v.setType(req.type());
        v.setModel(req.model());
        v.setCity(req.city().trim());
        v.setPricePerDay(req.pricePerDay());
        v.setStatus(VehicleStatus.ACTIVE);
        return toResponse(vehicleRepository.save(v));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> mine() {
        Long vendorId = vendorService.currentVendor().getId();
        return vehicleRepository.findByVendorId(vendorId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public VehicleResponse setStatus(Long vehicleId, VehicleStatus status) {
        Long vendorId = vendorService.currentVendor().getId();
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));
        if (!v.getVendor().getId().equals(vendorId)) {
            throw new ForbiddenException("This vehicle does not belong to you");
        }
        v.setStatus(status);
        return toResponse(v);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> search(String city, VehicleType type, LocalDate start, LocalDate end) {
        if (start.isBefore(LocalDate.now()) || !end.isAfter(start)) {
            throw new BadRequestException("Invalid dates");
        }
        return vehicleRepository.findByCityIgnoreCaseAndStatus(city.trim(), VehicleStatus.ACTIVE).stream()
                .filter(v -> type == null || v.getType() == type)
                .filter(v -> bookingItemRepository.sumOverlap(BookingType.RENTAL, v.getId(),
                        start, end, BookingStatus.ACTIVE) == 0)
                .map(this::toResponse)
                .toList();
    }

    private VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(v.getId(), v.getType(), v.getModel(), v.getCity(),
                v.getPricePerDay(), v.getStatus());
    }
}
