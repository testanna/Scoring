package ru.khitrova.scoring.model;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Клиент
 */
public class LoanRequest {
    /**
     * Возраст
     */
    @NotNull
    @Min(0)
    @Max(200)
    public Integer age;

    /**
     * Пол
     */
    @NotNull
    public Sex sex;

    /**
     * Источник дохода
     */
    @NotNull
    public SourceOfIncome sourceOfIncome;

    /**
     * Доход за последний год (млн.)
     */
    public BigDecimal lastYearIncome;

    /**
     * Кредитный рейтинг
     */
    @NotNull
    @Min(-2)
    @Max(2)
    public Integer creditRating;

    /**
     * Запрошенная сумма (млн.)
     */
    @NotNull
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "1.0")
    @Digits(integer = 2, fraction = 1)
    public BigDecimal requestedAmount;

    /**
     * Срок погашения
     */
    @NotNull
    @Min(1)
    @Max(20)
    public Integer repaymentPeriod;

    /**
     * Цель кредита
     */
    @NotNull
    public LoanPurpose loanPurpose;
}
