package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

public class RequiredAmountTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final String paymentError = "Некорректная сумма годового платежа";

    @Test
    public void incorrectRequiredAmountWithIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(4.2);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(6);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если (запрашиваемая сумма / срок) > (доход / 3) ",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(4.1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(6);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если (запрашиваемая сумма / срок) < = (доход / 3) ",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(2.4)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithPassiveIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(1.1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(6);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если доход пассивный и сумма > 1",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithPassiveIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(6);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если доход пассивный и сумма = 1",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithEmployeeIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(5.1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если наемный работник и сумма > 5",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountByEmployeeIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(5);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если наемный работник и сумма = 5",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(3)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithBusinessIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(10.1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если собственный бизнес и сумма > 10",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithBusinessIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если собственный бизнес и сумма = 10",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(6)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(1.1);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если кредитный рейтинг = -1 и сумма > 1",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(1);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит одобрен, если кредитный рейтинг = -1 и сумма = 1",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.6)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(5.1);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если кредитный рейтинг = 0 и сумма > 5",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(5);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если кредитный рейтинг = 0 и сумма = 5",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(3)) == 0);
    }

    @Test
    public void incorrectRequiredAmountWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(10.1);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если кредитный рейтинг = 1 и сумма > 10",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    public void correctRequiredAmountWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(10);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если кредитный рейтинг = 1 и сумма = 10",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(6)) == 0);
    }


}
