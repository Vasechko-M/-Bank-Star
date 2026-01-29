package pro.sky.manager.component;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.RecommendationDto;
import pro.sky.manager.repository.RecommendationRuleSet;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class RecommendationCredit implements RecommendationRuleSet {

    private final RecommendationsRepository recommendationsRepository;

    public RecommendationCredit(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasCredit = recommendationsRepository.getProductTypes(userId).contains("CREDIT");
        double totalDepositsDebit = recommendationsRepository.getTotalDeposits(userId, "DEBIT");
        double totalSpendingDebit = recommendationsRepository.getTotalSpent(userId, "DEBIT");

        if (!hasCredit && totalDepositsDebit > totalSpendingDebit && totalSpendingDebit > 100000) {
            return Optional.of(new RecommendationDto(
                    UUID.randomUUID(),
                    "Простой кредит",
                    "Откройте мир выгодных кредитов с нами!"
            ));
        }
        return Optional.empty();
    }
}