package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

public class CreditRatingTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final int incorrectRating = -2;
    private final String paymentError = "Некорректная сумма годового платежа";

    @Test
    public void incorrectRatingTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = incorrectRating;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = SourceOfIncome.EMPLOYEE;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если кредитный рейтинг = -2", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }
}
