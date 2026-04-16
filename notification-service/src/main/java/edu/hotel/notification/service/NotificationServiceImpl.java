package edu.hotel.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.hotel.events.*;
import edu.hotel.notification.entity.NotificationLog;
import edu.hotel.notification.model.EventType;
import edu.hotel.notification.model.NotificationStatus;
import edu.hotel.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ObjectMapper objectMapper;

    private final NotificationLogRepository notificationLogRepository;

    @Override
    @Transactional
    public void handleBookingCreated(BookingCreatedEvent event) {

        notificationLogRepository.save(setNotificationLog(
                EventType.BOOKING_CREATED, event.getGuestId(), event.getBookingId(), event));
    }

    @Override
    @Transactional
    public void handleBookingCancelled(BookingCancelledEvent event) {

        notificationLogRepository.save(setNotificationLog(
                EventType.BOOKING_CANCELLED, event.getGuestId(), event.getBookingId(), event));
    }

    @Override
    @Transactional
    public void handleBookingCompleted(BookingCompletedEvent event) {

        notificationLogRepository.save(setNotificationLog(
                EventType.BOOKING_COMPLETED, event.getGuestId(), event.getBookingId(), event));
    }

    @Override
    @Transactional
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        notificationLogRepository.save(setNotificationLog(
                EventType.PAYMENT_CONFIRMED, event.getGuestId(), event.getBookingId(), event));
    }

    @Override
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {

        notificationLogRepository.save(setNotificationLog(
                EventType.PAYMENT_FAILED, event.getGuestId(), event.getBookingId(), event));
    }

    private NotificationLog setNotificationLog(
            EventType eventType, Long guestId, Long bookingId, Object event) {

        NotificationLog log = new NotificationLog();
        log.setEventType(eventType);
        log.setGuestId(guestId);
        log.setBookingId(bookingId);
        log.setStatus(NotificationStatus.SENT);
        log.setRetryCount(0);

        try {
            log.setPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.setPayload("serialization error");
        }
        return log;
    }
}
