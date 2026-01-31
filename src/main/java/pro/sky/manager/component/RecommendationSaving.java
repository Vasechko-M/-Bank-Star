package pro.sky.manager.component;

import org.springframework.stereotype.Component;
import pro.sky.manager.model.RecommendationDTO;
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

    /**
     * При совпадении условий, компонент возвращает реккомендацию на открытие накопительного счета.
     */
    @Override
    public Optional<RecommendationDTO> check(UUID userId) {
        boolean hasDebit = recommendationsRepository.getProductTypes(userId).contains("DEBIT");
        double totalDepositsDebit = recommendationsRepository.getTotalDeposits(userId, "DEBIT");
        double totalDepositsSaving = recommendationsRepository.getTotalDeposits(userId, "SAVING");
        double totalSpendingDebit = recommendationsRepository.getTotalSpent(userId, "DEBIT");

        if (hasDebit &&
                (totalDepositsDebit >= 50000 || totalDepositsSaving >= 50000) &&
                totalDepositsDebit > totalSpendingDebit) {

            return Optional.of(new RecommendationDTO(
                    UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                    "Top Saving",
                    """
                            Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!
                            Преимущества «Копилки»:
                            Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
                            Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
                            Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.
                            Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!
                            """
            ));
        }
        return Optional.empty();
    }
}