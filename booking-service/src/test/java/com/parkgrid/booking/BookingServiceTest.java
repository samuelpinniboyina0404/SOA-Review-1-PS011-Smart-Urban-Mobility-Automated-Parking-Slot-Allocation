package com.parkgrid.booking;

import com.parkgrid.booking.dto.BookingRequest;
import com.parkgrid.booking.entity.Booking;
import com.parkgrid.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "eureka.client.enabled=false")
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Test
    public void testBookingCreationAndDuplicatePrevention() {
        BookingRequest req1 = new BookingRequest(2L, "driver@parkgrid.com", 101L, "KA-01-AB-1234", "CAR", 2.0);
        Booking b1 = bookingService.createBooking(req1);

        assertNotNull(b1);
        assertNotNull(b1.getBookingReference());
        assertEquals("CONFIRMED", b1.getStatus());

        // Attempt duplicate reservation on same slot 101L must fail with IllegalStateException (Zero Double Booking)
        BookingRequest req2 = new BookingRequest(3L, "driver2@parkgrid.com", 101L, "KA-02-CD-5678", "CAR", 1.0);
        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(req2));
    }

    @Test
    public void testCheckInAndCancelFlow() {
        BookingRequest req = new BookingRequest(2L, "driver@parkgrid.com", 102L, "KA-03-EF-9999", "EV", 3.0);
        Booking booking = bookingService.createBooking(req);

        Booking active = bookingService.checkIn(booking.getId());
        assertEquals("ACTIVE", active.getStatus());

        Map<String, Object> outResult = bookingService.checkOut(booking.getId());
        assertNotNull(outResult);
        Booking completed = (Booking) outResult.get("booking");
        assertEquals("COMPLETED", completed.getStatus());
    }

    @Test
    public void testBookingTrends() {
        Map<String, Object> trends = bookingService.getBookingTrends();
        assertNotNull(trends);
        assertTrue(trends.containsKey("totalBookings"));
    }
}
