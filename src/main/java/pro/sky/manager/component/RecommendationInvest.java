package pro.sky.manager.component;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.rules.RecommendationDTO;
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

    /**
     * При совпадении условий, компонент возвращает рекомендацию открытие инвестиционного кода.
     */
    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean hasDebit = recommendationsRepository.getProductTypes(userId).contains("DEBIT");
        boolean usesInvest = recommendationsRepository.getProductTypes(userId).contains("INVEST");
        double savingDeposits = recommendationsRepository.getTotalDeposits(userId, "SAVING");

        if (hasDebit && !usesInvest && savingDeposits > 1000) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                    "Инвестиции 500",
                    """
                            Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! 
                            Воспользуйтесь налоговыми льготами и начните инвестировать с умом. 
                            Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. 
                            Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. 
                            Откройте ИИС сегодня и станьте ближе к финансовой независимости!
                            """
            ));
        }
        return Optional.empty();
    }
}