package ru.khitrova.scoring.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.khitrova.scoring.model.LoanRequest;
import ru.khitrova.scoring.model.LoanResponse;
import ru.khitrova.scoring.scoring.ScoringService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(
        value = "/scoring",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping(value = "/check")
    public LoanResponse checkLoan(@NotNull @Valid @RequestBody LoanRequest loanRequest) {
        return scoringService.checkLoan(loanRequest);
    }
}
