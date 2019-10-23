package ru.khitrova.scoring.scoring;

import ru.khitrova.scoring.model.LoanRequest;
import ru.khitrova.scoring.model.LoanResponse;

/**
 *
 */
public interface ScoringService {

    /**
     *
     * @param loanRequest
     * @return
     */
    LoanResponse checkLoan(LoanRequest loanRequest);
}
