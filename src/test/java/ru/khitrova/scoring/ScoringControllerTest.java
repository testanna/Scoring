package ru.khitrova.scoring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import ru.khitrova.scoring.model.LoanRequest;
import ru.khitrova.scoring.model.LoanResponse;
import ru.khitrova.scoring.web.ScoringController;

import javax.validation.ConstraintViolationException;
import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScoringControllerTest {

    private static final String URL_PATTERN = "http://localhost:%s/scoring/check";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void nullRequestValidationTest() {
        RequestEntity<?> request = RequestEntity
                .post(URI.create(String.format(URL_PATTERN, port)))
                .contentType(MediaType.APPLICATION_JSON)
                .body("");
        ResponseEntity<String> response = restTemplate.exchange(
                request,
                String.class
        );
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assert.assertTrue(StringUtils.hasText(responseBody));
        Assert.assertTrue(responseBody.contains("Required request body is missing"));
    }

    @Test
    public void emptyRequestValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String responseBody = response.getBody();
        Assert.assertTrue(StringUtils.hasText(responseBody));

        //TODO contains
        System.out.println(responseBody);
    }

}
