package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

/**
 * Тесты возможности получения кредита в зависимости от суммы годового платежа.
 * Тесты расчета суммы годового платежа в зависимости от значений входных параметров.
 */
public class AnnualPaymentTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final String approveError = "Кредит не одобрен, а должен был.";
    private final String paymentError = "Некорректная сумма годового платежа.";
    private final String annualError = paymentError + " OP: %s ПР: %s";

    /**
     * TODO подобрать значения для срока, суммы и дохода для тестирования.
     * Сложность в том, что большинство значений попадает под проверку:
     * 3. Если результат деления запрошенной суммы на срок погашения в годах более трети годового дохода.
     */
    @Test
    public void incorrectAnnualPaymentWithIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(6.8);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(10.1);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если годовой платеж больше половины дохода", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }

    /**
     * Далее тесты проверяют ежемясячный платеж с учетом комбинаций цели, рейтинга и дохода методом pairwise.
     * Запрашиваемая сумма меняется условно рандомно.
     * Тестов 15, а не 16, потому что один из вариантов проверен в тесте на другие условия.
     */

    @Test
    public void annualPaymentForBusinessWithBusinessIncomeWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(1);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.BUSINESS;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertTrue(annualError, loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.6)) == 0);
        Assert.assertEquals(String.format("%s OP: %s ПР: %s", paymentError, 0.6, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.6)), 0);
    }

    @Test
    public void annualPaymentForBusinessWithEmployeeIncomeWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(1);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.BUSINESS;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.5, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void annualPaymentForBusinessWithPassiveIncomeWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.9);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.BUSINESS;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.4, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.4)), 0);
    }

    @Test
    public void annualPaymentForMortgageWithPassiveIncomeWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(1);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.MORTGAGE;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.5, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void annualPaymentForMortgageWithPassiveIncomeWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.8);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.MORTGAGE;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.4, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.4)), 0);
    }

    @Test
    public void annualPaymentForMortgageWithBusinessIncomeWithTwoRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(5);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.MORTGAGE;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 2.7, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(2.7)), 0);
    }

    @Test
    public void annualPaymentForMortgageWithEmployeeIncomeWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.7);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.MORTGAGE;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, paymentError, 0.3, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.3)), 0);
    }

    @Test
    public void annualPaymentForConsumerWithBusinessIncomeWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.7);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CONSUMER;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.4, loanResponse.annualPayment), loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.4)), 0);
    }

    @Test
    public void annualPaymentForConsumerWithEmployeeIncomeWithTwoRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.6);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CONSUMER;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.3, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.3)), 0);
    }

    @Test
    public void annualPaymentForConsumerWithPassiveIncomeWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.9);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.CONSUMER;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, paymentError, 0.5, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.5)), 0);
    }

    @Test
    public void annualPaymentForConsumerWithBusinessIncomeWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(2);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CONSUMER;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 1.2, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(1.2)), 0);
    }

    @Test
    public void annualPaymentForCarWithPassiveIncomeWithTwoRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 23;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.5);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.PASSIVE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.2, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.2)), 0);
    }

    @Test
    public void annualPaymentForCarWithEmployeeIncomeWithMinRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 23;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.5);
        loanRequest.creditRating = -1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.3, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.3)), 0);
    }

    @Test
    public void annualPaymentForCarWithBusinessIncomeWithZeroRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 23;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.5);
        loanRequest.creditRating = 0;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.2, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.2)), 0);
    }

    @Test
    public void annualPaymentForCarWithEmployeeIncomeWithOneRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 23;
        loanRequest.sex = Sex.F;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.7);
        loanRequest.creditRating = 1;
        loanRequest.lastYearIncome = BigDecimal.valueOf(15);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue(approveError, loanResponse.approved);
        Assert.assertEquals(String.format(annualError, 0.3, loanResponse.annualPayment),
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.3)), 0);
    }


}
