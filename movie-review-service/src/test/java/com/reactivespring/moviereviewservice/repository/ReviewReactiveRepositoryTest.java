package com.reactivespring.moviereviewservice.repository;

import com.reactivespring.moviereviewservice.domain.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "60000000")
public class ReviewReactiveRepositoryTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    public void setUp() {

        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    public void tearDown() {
        reviewReactiveRepository.deleteAll()
                .block();
    }

    @Test
    public void name() {
        webTestClient
                .get()
                .uri("/v1/helloworld")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("HelloWorld");
    }

    @Test
    public void getReviews() {
        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });
    }

    @Test
    public void getReviewsByMovieInfoId() {
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/reviews")
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });
    }

    @Test
    public void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                });
    }

    @Test
    public void updateReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        assert savedReview != null;
        webTestClient
                .put()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(savedReview.getReviewId());
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });
    }

    @Test
    public void updateReview_NotFound() {
        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        webTestClient
                .put()
                .uri("/v1/reviews/{id}", "abc")
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deleteReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        var savedReview = reviewReactiveRepository.save(review).block();
        assert savedReview != null;
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", savedReview.getReviewId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void deleteReview_notFound() {
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", "123")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void error() {
        webTestClient
                .get()
                .uri("/v1/error")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}