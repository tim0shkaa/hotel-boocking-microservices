package edu.hotel.review.kafka;

import edu.hotel.common.model.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BookingConsumerConfig {


    @Transactional
    @KafkaListener(topics = KafkaTopics.BOOKING_COMPLETED)
}
