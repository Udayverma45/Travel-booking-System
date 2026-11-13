package com.example.booking.vendor;

import com.example.booking.auth.CurrentUser;
import com.example.booking.common.exception.ConflictException;
import com.example.booking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final CurrentUser currentUser;

    /** Logged-in user ka vendor profile */
    public Vendor currentVendor() {
        Long userId = currentUser.get().getId();
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found"));
    }

    /** Vendor approved ho aur sahi type ka ho, tabhi inventory add kar sakta hai */
    public Vendor approvedVendor(VendorType type) {
        Vendor v = currentVendor();
        if (v.getStatus() != VendorStatus.APPROVED) {
            throw new ConflictException("Vendor is not approved yet");
        }
        if (v.getType() != type) {
            throw new ConflictException("Your vendor type is " + v.getType() + ", not " + type);
        }
        return v;
    }

    @Transactional(readOnly = true)
    public VendorResponse me() {
        return toResponse(currentVendor());
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> getByStatus(VendorStatus status) {
        return vendorRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    @Transactional
    public VendorResponse updateStatus(Long vendorId, VendorStatus status) {
        Vendor v = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + vendorId));
        v.setStatus(status);
        return toResponse(v);
    }

    private VendorResponse toResponse(Vendor v) {
        return new VendorResponse(v.getId(), v.getUser().getId(), v.getUser().getName(),
                v.getBusinessName(), v.getGstNo(), v.getType(), v.getStatus());
    }
}
