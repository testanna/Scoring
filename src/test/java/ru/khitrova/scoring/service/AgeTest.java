package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

public class AgeTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final int minAge = 18;
    private final int maxFAge = 60;
    private final int maxMAge = 65;
    private final String paymentError = "Некорректная сумма годового платежа";

    /**
     * TODO сделать отдельный класс для формирования сообщений об ошибках в assert.
     */

    @Test
    public void lessThenMinTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = minAge - 1;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если возраст < 18", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void zeroAgeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 0;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если возраст = 0", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void moreThenMinTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = minAge + 1;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст > 18", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void minTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = minAge;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст = 18", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void lessThenMaxFTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxFAge - 1;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст < 60 и пол женский", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void moreThenMaxFTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxFAge + 1;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если возраст > 60 и пол женский", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void maxFTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxFAge;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст = 60 и пол женский", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void lessThenMaxMTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxMAge - 1;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст < 65 и пол мужской", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void moreThenMaxMTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxMAge + 1;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если возраст > 65 и пол мужской", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }

    @Test
    public void maxMTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = maxMAge;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если возраст = 65 и пол мужской", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }
}
