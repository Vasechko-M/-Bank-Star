package pro.sky.manager.component;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.RecommendationDto;
import pro.sky.manager.repository.RecommendationRuleSet;
import pro.sky.manager.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class RecommendationInvest implements RecommendationRuleSet {

    private final RecommendationsRepository recommendationsRepository;

    public RecommendationInvest(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public Optional<RecommendationDto> check(UUID userId) {
        boolean hasDebit = recommendationsRepository.getProductTypes(userId).contains("DEBIT");
        boolean usesInvest = recommendationsRepository.getProductTypes(userId).contains("INVEST");
        double savingDeposits = recommendationsRepository.getTotalDeposits(userId, "SAVING");
        if (hasDebit && !usesInvest && savingDeposits > 1000) {
            return Optional.of(new RecommendationDto(
                    UUID.randomUUID(),
                    "Инвестиции 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом..."
            ));
        }
        return Optional.empty();
    }
}