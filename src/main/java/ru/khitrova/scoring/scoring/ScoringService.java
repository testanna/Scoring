package ru.khitrova.scoring.scoring;

import ru.khitrova.scoring.model.LoanRequest;
import ru.khitrova.scoring.model.LoanResponse;

/**
 *
 */
public interface ScoringService {

    /**
     * Интерфейс проверки возможности выдачи кредита и расчета годового платежа
     * @param loanRequest Запрос на возможность выдачи кредита
     * @return LoanResponse - ответ сервиса - возможность выдачи кредита и расчет годового платежа
     */
    LoanResponse checkLoan(LoanRequest loanRequest);
}
