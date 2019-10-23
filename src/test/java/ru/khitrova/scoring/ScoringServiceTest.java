package ru.khitrova.scoring;

import org.junit.Test;
import ru.khitrova.scoring.scoring.InMemoryScoringService;
import ru.khitrova.scoring.scoring.ScoringService;

public class ScoringServiceTest {
    private final ScoringService scoringService = new InMemoryScoringService();

    @Test
    public void test() {

    }
}
