package ru.khitrova.scoring.model;

import java.math.BigDecimal;

/**
 * Ответ сервиса - одобрен ли кредит, годовой платеж
 */
public class LoanResponse {

    /**
     * {@code true}, если кредит одобрен
     */
    public Boolean approved;

    /**
     * Годовой платёж по кредиту
     */
    public BigDecimal annualPayment;
}
