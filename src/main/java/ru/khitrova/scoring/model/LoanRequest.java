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
    @NotNull(message = "Возраст не может быть null")
    @Min(value = 0, message = "Возраст не может быть меньше 0")
    @Max(value = 200, message = "Возраст не может быть больше 200")
    public Integer age;

    /**
     * Пол
     */
    @NotNull(message = "Пол не может быть null")
    public Sex sex;

    /**
     * Источник дохода
     */
    @NotNull(message = "Источник дохода не может быть null")
    public SourceOfIncome sourceOfIncome;

    /**
     * Доход за последний год (млн.)
     */
    public BigDecimal lastYearIncome;

    /**
     * Кредитный рейтинг
     */
    @NotNull(message = "Кредитный рейтинг не может быть null")
    @Min(value = -2, message = "Кредитный рейтинг не может быть меньше -2")
    @Max(value = 2, message = "Кредитный рейтинг не может быть больше 2")
    public Integer creditRating;

    /**
     * Запрошенная сумма (млн.)
     */
    @NotNull(message = "Сумма не может быть null")
    @DecimalMin(value = "0.1", message = "Сумма не может быть меньше 0.1")
    @DecimalMax(value = "10.0", message = "Сумма не может быть больше 10")
    @Digits(integer = 2, fraction = 1)
    public BigDecimal requestedAmount;

    /**
     * Срок погашения
     */
    @NotNull(message = "Срок не может быть null")
    @Min(value = 1, message = "Срок не может быть меньше 1")
    @Max(value = 20, message = "Срок не может быть больше 20")
    public Integer repaymentPeriod;

    /**
     * Цель кредита
     */
    @NotNull(message = "Цель не может быть null")
    public LoanPurpose loanPurpose;
}
