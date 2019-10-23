package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

public class RepaymentPeriodTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final String paymentError = "Некорректная сумма годового платежа";

    @Test
    public void minRequiredAmountWithMinRepaymentPeriodTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(0.1);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(6);
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 1;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если запрашиваемая сумма = 0.1",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(0.1)) == 0);
    }

    @Test
    public void maxRepaymentPeriodWithBigIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.valueOf(9);
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.valueOf(100000000);
        loanRequest.sourceOfIncome = SourceOfIncome.OWN_BUSINESS;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 20;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertTrue("Кредит НЕ одобрен, если запрашиваемая сумма = 100000000 и срок = 20",
                loanResponse.approved);
        Assert.assertTrue(paymentError,
                loanResponse.annualPayment.compareTo(BigDecimal.valueOf(1.4)) == 0);
    }
}
