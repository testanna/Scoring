package ru.khitrova.scoring.controller;

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
import ru.khitrova.scoring.model.*;

import java.math.BigDecimal;
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

    @Test
    public void emptyAgeValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = null;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Возраст не может быть null"));
    }

    @Test
    public void lessThenMinAgeValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = -1;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Возраст не может быть меньше 0"));
    }

    @Test
    public void moreThenMaxAgeValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 201;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Возраст не может быть больше 200"));
    }

    @Test
    public void emptySexValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = null;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Пол не может быть null"));
    }

    @Test
    public void emptySourceOfIncomeValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = null;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Источник дохода не может быть null"));
    }

    @Test
    public void emptyCreditRatingValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = null;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Кредитный рейтинг не может быть null"));
    }

    @Test
    public void lessThenMinCreditRatingValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = -3;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Кредитный рейтинг не может быть меньше -2"));
    }

    @Test
    public void moreThenMaxCreditRatingValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 3;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Кредитный рейтинг не может быть больше 2"));
    }

    @Test
    public void emptyRequestedAmountValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = null;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Сумма не может быть null"));
    }

    @Test
    public void lessThenMinRequestedAmountValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.01);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Сумма не может быть меньше 0.1"));
    }

    @Test
    public void moreThenMinRequestedAmountValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(11);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Сумма не может быть больше 10"));
    }

    @Test
    public void emptyRepaymentPeriodValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = null;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Срок не может быть null"));
    }

    @Test
    public void lessThenMinRepaymentPeriodValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 0;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Срок не может быть меньше 1"));
    }

    @Test
    public void moreThenMaxRepaymentPeriodValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 21;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Срок не может быть больше 20"));
    }

    @Test
    public void emptyPurposeValidationTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = null;
        loanRequest.repaymentPeriod = 3;

        ResponseEntity<String> response = restTemplate.postForEntity(
                String.format(URL_PATTERN, port),
                loanRequest,
                String.class
        );
        String responseBody = response.getBody();
        Assert.assertTrue(responseBody.contains("Цель не может быть null"));
    }

}
