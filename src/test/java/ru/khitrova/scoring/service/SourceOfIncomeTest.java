package ru.khitrova.scoring.service;

import org.junit.Assert;
import org.junit.Test;
import ru.khitrova.scoring.model.*;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

import java.math.BigDecimal;

/**
 * Тесты возможности получения кредита в зависимости от источника дохода.
 */
public class SourceOfIncomeTest {
    private final ScoringService scoringService = new InMemoryScoringService();
    private final SourceOfIncome incorrectSource = SourceOfIncome.UNEMPLOYED;
    private final String paymentError = "Некорректная сумма годового платежа";

    @Test
    public void incorrectSourceOfIncomeTest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.age = 30;
        loanRequest.sex = Sex.M;
        loanRequest.requestedAmount = BigDecimal.ONE;
        loanRequest.creditRating = 2;
        loanRequest.lastYearIncome = BigDecimal.TEN;
        loanRequest.sourceOfIncome = incorrectSource;
        loanRequest.loanPurpose = LoanPurpose.CAR;
        loanRequest.repaymentPeriod = 2;

        LoanResponse loanResponse = scoringService.checkLoan(loanRequest);

        Assert.assertFalse("Кредит одобрен, если заниматель безработный", loanResponse.approved);
        Assert.assertEquals(paymentError, loanResponse.annualPayment.compareTo(BigDecimal.ZERO), 0);
    }
}
