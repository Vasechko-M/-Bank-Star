package pro.sky.manager.component;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.RecommendationDto;
import pro.sky.manager.repository.RecommendationRuleSet;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class RecommendationSaving implements RecommendationRuleSet {

    private final RecommendationsRepository recommendationsRepository;

    public RecommendationSaving(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = recommendationsRepository.getProductTypes(userId).contains("DEBIT");
        double totalDepositsDebit = recommendationsRepository.getTotalDeposits(userId, "DEBIT");
        double totalDepositsSaving = recommendationsRepository.getTotalDeposits(userId, "SAVING");
        double totalSpendingDebit = recommendationsRepository.getTotalSpent(userId, "DEBIT");

        if (hasDebit &&
                (totalDepositsDebit >= 50000 || totalDepositsSaving >= 50000) &&
                totalDepositsDebit > totalSpendingDebit) {

            return Optional.of(new RecommendationDto(
                    UUID.randomUUID(),
                    "Top Saving",
                    "Откройте свою собственную «Копилку» с нашим банком!..."
            ));
        }
        return Optional.empty();
    }
}