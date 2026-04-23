package edu.hotel.review.service;

import edu.hotel.common.exception.AlreadyExistsException;
import edu.hotel.review.dto.reply.ReviewReplyRequest;
import edu.hotel.review.dto.reply.ReviewReplyResponse;
import edu.hotel.review.entity.ReviewReply;
import edu.hotel.review.mapper.ReviewReplyMapper;
import edu.hotel.review.repository.ReviewReplyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewReplyServiceImplTest {

    @Mock private ReviewReplyRepository reviewReplyRepository;
    @Mock private ReviewReplyMapper reviewReplyMapper;

    @InjectMocks
    private ReviewReplyServiceImpl reviewReplyService;

    @Test
    void createReply_shouldCreateReplyWithCorrectFields() {
        Long reviewId = 1L;
        Long userId = 10L;

        ReviewReplyRequest request = new ReviewReplyRequest();
        request.setBody("Спасибо за отзыв");

        ReviewReply savedReply = new ReviewReply();
        ReviewReplyResponse response = new ReviewReplyResponse();

        when(reviewReplyRepository.existsByReviewId(reviewId)).thenReturn(false);
        when(reviewReplyRepository.save(any())).thenReturn(savedReply);
        when(reviewReplyMapper.toResponse(savedReply)).thenReturn(response);

        ReviewReplyResponse result = reviewReplyService.createReply(request, reviewId, userId);

        assertThat(result).isEqualTo(response);

        ArgumentCaptor<ReviewReply> captor = ArgumentCaptor.forClass(ReviewReply.class);
        verify(reviewReplyRepository).save(captor.capture());
        assertThat(captor.getValue().getAuthorId()).isEqualTo(userId);
        assertThat(captor.getValue().getReviewId()).isEqualTo(reviewId);
        assertThat(captor.getValue().getBody()).isEqualTo("Спасибо за отзыв");
    }

    @Test
    void createReply_shouldThrowAlreadyExistsException_whenReplyAlreadyExists() {
        when(reviewReplyRepository.existsByReviewId(anyLong())).thenReturn(true);

        assertThatThrownBy(() -> reviewReplyService.createReply(new ReviewReplyRequest(), 1L, 10L))
                .isInstanceOf(AlreadyExistsException.class);

        verify(reviewReplyRepository, never()).save(any());
    }
}