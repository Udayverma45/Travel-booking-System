package com.example.booking.bus;

import com.example.booking.common.exception.ConflictException;
import com.example.booking.vendor.Vendor;
import com.example.booking.vendor.VendorService;
import com.example.booking.vendor.VendorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final VendorService vendorService;

    @Transactional
    public BusResponse addBus(BusRequest request) {
        Vendor vendor = vendorService.approvedVendor(VendorType.BUS);

        if (busRepository.existsByBusNumber(request.busNumber())) {
            throw new ConflictException("Bus number already exists: " + request.busNumber());
        }

        Bus bus = new Bus();
        bus.setBusNumber(request.busNumber());
        bus.setBusType(request.busType());
        bus.setTotalSeats(request.totalSeats());
        bus.setVendor(vendor);
        return toResponse(busRepository.save(bus));
    }

    @Transactional(readOnly = true)
    public List<BusResponse> myBuses() {
        Long vendorId = vendorService.currentVendor().getId();
        return busRepository.findByVendorId(vendorId).stream().map(this::toResponse).toList();
    }

    private BusResponse toResponse(Bus b) {
        return new BusResponse(b.getId(), b.getBusNumber(), b.getBusType(), b.getTotalSeats());
    }
}
