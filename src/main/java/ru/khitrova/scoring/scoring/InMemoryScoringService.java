package ru.khitrova.scoring.scoring;

import org.springframework.stereotype.Service;
import ru.khitrova.scoring.model.*;

import java.math.BigDecimal;

@Service
public class InMemoryScoringService implements ScoringService {
    /**
     * Допущение по округлению, так как в требованиях это не описано.
     */
    private int roundingMode = 1;
    private int scale = 1;

    @Override
    public LoanResponse checkLoan(LoanRequest loanRequest) {
        LoanResponse loanResponse = new LoanResponse();

        BigDecimal interestRate = сommonInterestRate(loanRequest.loanPurpose, loanRequest.creditRating,
                loanRequest.requestedAmount, loanRequest.sourceOfIncome);
        BigDecimal annualPayment = annualPayment(loanRequest.requestedAmount, loanRequest.repaymentPeriod, interestRate);

        loanResponse.approved = checkLoanRequest(loanRequest, annualPayment);

        if (Boolean.TRUE.equals(loanResponse.approved)) {
            loanResponse.annualPayment = annualPayment;
        } else {
            loanResponse.annualPayment = BigDecimal.ZERO;
        }

        return loanResponse;
    }


    /**
     * Проверка запроса на кредит.
     */
    private boolean checkLoanRequest(LoanRequest loanRequest, BigDecimal annualPayment) {
        if (!((checkAge(loanRequest.age, loanRequest.sex))
                && (checkLoanRating(loanRequest.creditRating))
                && (checkSourceOfIncome(loanRequest.sourceOfIncome))
                && (checkRequestedAmount(loanRequest.sourceOfIncome, loanRequest.creditRating,
                loanRequest.requestedAmount, loanRequest.lastYearIncome, loanRequest.repaymentPeriod))
        )) {
            return false;
        }

        return checkAnnualPayment(annualPayment, loanRequest.lastYearIncome);
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
        BigDecimal yearAmount = requestedAmount.divide(BigDecimal.valueOf(repaymentPeriod), scale, roundingMode);
        BigDecimal thirdPartOfIncome = lastYearIncome.divide(BigDecimal.valueOf(3), scale, roundingMode);

        if (yearAmount.compareTo(thirdPartOfIncome) > 0) {
            return false;
        }

        BigDecimal maxAmount = maxAmount(sourceOfIncome, creditRating);
        if (requestedAmount.compareTo(maxAmount) > 0) {
            return false;
        }

        if (BigDecimal.ZERO.compareTo(maxAmount) == 0) {
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
        } else if (creditRating > 0) {
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

    /**
     * Проверка годового платежа
     */

    private boolean checkAnnualPayment(BigDecimal annualPayment,
                                       BigDecimal lastYearIncome) {

        if (annualPayment.compareTo(lastYearIncome.divide(BigDecimal.valueOf(2), roundingMode)) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Расчет годового платежа
     */
    private BigDecimal annualPayment(BigDecimal requestedAmount, int repaymentPeriod, BigDecimal interestRate) {
        BigDecimal partOfSum = BigDecimal.valueOf(repaymentPeriod).
                multiply(interestRate)
                .divide(BigDecimal.valueOf(100), scale, roundingMode);
        BigDecimal sum = BigDecimal.ONE.add(partOfSum);
        BigDecimal dividend = requestedAmount.multiply(sum);
        BigDecimal divider = BigDecimal.valueOf(repaymentPeriod);

        return dividend.divide(divider, scale, roundingMode);
    }

    /**
     * Общая процентраная ставка
     */
    private BigDecimal сommonInterestRate(LoanPurpose loanPurpose, int creditRating, BigDecimal requestedAmount,
                                          SourceOfIncome sourceOfIncome) {
        BigDecimal interestRate = BigDecimal.TEN;
        return interestRate.add(modifierByPurpose(loanPurpose))
                .add(modifierByRating(creditRating))
                .add(modifierBySourceOfIncome(sourceOfIncome))
                .add(modifierByRequestedAmount(requestedAmount));
    }

    /**
     * Модификатор процентной ставки в зависимости от цели кредита
     */
    private BigDecimal modifierByPurpose(LoanPurpose loanPurpose) {
        if (loanPurpose == LoanPurpose.MORTGAGE) {
            return BigDecimal.valueOf(-2);
        }
        if (loanPurpose == LoanPurpose.BUSINESS) {
            return BigDecimal.valueOf(-0.5);
        }
        if (loanPurpose == LoanPurpose.CONSUMER) {
            return BigDecimal.valueOf(1.5);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Модификатор процентной ставки в зависимости от кредитного рейтинга
     */
    private BigDecimal modifierByRating(int creditRating) {
        if (creditRating == -1) {
            return BigDecimal.valueOf(1.5);
        }
        if (creditRating == 1) {
            return BigDecimal.valueOf(-0.25);
        }
        if (creditRating == 2) {
            return BigDecimal.valueOf(-0.75);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Модификатор процентной ставки в зависимости от запрашиваемой суммы
     */
    private BigDecimal modifierByRequestedAmount(BigDecimal requestedAmount) {
        double amount = requestedAmount.doubleValue();
        return BigDecimal.valueOf(Math.log(amount));
    }

    /**
     * Модификатор процентной ставки в зависимости от источника дохода
     */
    private BigDecimal modifierBySourceOfIncome(SourceOfIncome sourceOfIncome) {
        if (sourceOfIncome == SourceOfIncome.PASSIVE) {
            return BigDecimal.valueOf(0.5);
        }
        if (sourceOfIncome == SourceOfIncome.EMPLOYEE) {
            return BigDecimal.valueOf(-0.25);
        }
        if (sourceOfIncome == SourceOfIncome.OWN_BUSINESS) {
            return BigDecimal.valueOf(0.25);
        }
        return BigDecimal.ZERO;
    }

}
