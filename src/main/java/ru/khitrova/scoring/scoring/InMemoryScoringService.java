package ru.khitrova.scoring.scoring;

import org.springframework.stereotype.Service;
import ru.khitrova.scoring.model.LoanRequest;
import ru.khitrova.scoring.model.LoanResponse;
import ru.khitrova.scoring.model.Sex;
import ru.khitrova.scoring.model.SourceOfIncome;

import java.math.BigDecimal;

@Service
public class InMemoryScoringService implements ScoringService {
    @Override
    public LoanResponse checkLoan(LoanRequest loanRequest) {
        return null;
    }

    /**
     * Проверка возраста
     */
    private boolean checkAge(Integer age, Sex sex) {
        if (age < 18) {
            return false;
        }
        if ((age > 60) && (sex == Sex.F)) {
            return false;
        }
        if ((age > 65) && (sex == Sex.M)) {
            return false;
        }
        return true;
    }

    /**
     * Проверка запрошенной суммы
     */
    private boolean checkRequestedAmount(
            SourceOfIncome sourceOfIncome,
            Integer creditRating,
            BigDecimal requestedAmount,
            BigDecimal lastYearIncome,
            Integer repaymentPeriod
    ) {
        BigDecimal maxAmount = maxAmount(sourceOfIncome, creditRating);
        if (requestedAmount.compareTo(maxAmount) > 0) {
            return false;
        }

        if (BigDecimal.ZERO.compareTo(maxAmount) == 0) {
            return false;
        }

        int requestedAmountRoundingMode = 1;
        BigDecimal yearAmount = requestedAmount.divide(BigDecimal.valueOf(repaymentPeriod), requestedAmountRoundingMode);
        BigDecimal thirdPartOfIncome = lastYearIncome.divide(BigDecimal.valueOf(3), requestedAmountRoundingMode);

        if (yearAmount.compareTo(thirdPartOfIncome) > 0) {
            return false;
        }

        if (requestedAmount.compareTo(BigDecimal.valueOf(10)) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Максимально возможная сумма кредита
     */
    private BigDecimal maxAmount(SourceOfIncome sourceOfIncome, Integer creditRating) {
        BigDecimal maxAmountBySource = maxRequestedAmountBySourceOfIncome(sourceOfIncome);
        BigDecimal maxAmountByRating = maxRequestedAmountByRating(creditRating);

        if (maxAmountByRating.compareTo(maxAmountBySource) > 0) {
            return maxAmountBySource;
        } else {
            return maxAmountByRating;
        }
    }

    /**
     * Максимально возможная запрашиваемая сумма по источнику доходов
     */
    private BigDecimal maxRequestedAmountBySourceOfIncome(SourceOfIncome sourceOfIncome) {
        if (sourceOfIncome == SourceOfIncome.PASSIVE) {
            return BigDecimal.valueOf(1);
        } else if (sourceOfIncome == SourceOfIncome.EMPLOYEE) {
            return BigDecimal.valueOf(5);
        } else if (sourceOfIncome == SourceOfIncome.OWN_BUSINESS) {
            return BigDecimal.valueOf(10);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Максимально возможная запрашиваемая сумма по рейтингу
     */
    private BigDecimal maxRequestedAmountByRating(Integer creditRating) {
        if (creditRating < 0) {
            return BigDecimal.valueOf(1);
        } else if (creditRating == 0) {
            return BigDecimal.valueOf(5);
        } else if ((creditRating > 0) || (creditRating == 2)) {
            return BigDecimal.valueOf(10);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Проверка кредитного рейтинга
     */
    private boolean checkLoanRating(Integer creditRating) {
        return creditRating > -2;
    }

    /**
     * Проверка источника дохода
     */
    private boolean checkSourceOfIncome(SourceOfIncome sourceOfIncome) {
        return sourceOfIncome != SourceOfIncome.UNEMPLOYED;
    }
}
