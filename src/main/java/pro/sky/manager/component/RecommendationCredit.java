    package pro.sky.manager.component;

    import org.springframework.stereotype.Component;
    import pro.sky.manager.model.DynamicRule;
    import pro.sky.manager.model.RecommendationDTO;
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

        /**
         * При совпадении условий, компонент возвращает рекомендацию на получение простого кредита.
         */
        @Override
        public Optional<RecommendationDTO> check(UUID userId) {
            boolean hasCredit = recommendationsRepository.getProductTypes(userId).contains("CREDIT");
            double totalDepositsDebit = recommendationsRepository.getTotalDeposits(userId, "DEBIT");
            double totalSpendingDebit = recommendationsRepository.getTotalSpent(userId, "DEBIT");

            if (!hasCredit && totalDepositsDebit > totalSpendingDebit && totalSpendingDebit > 100000) {
                return Optional.of(new RecommendationDTO(
                        UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                        "Простой кредит",
                        """
                                Откройте мир выгодных кредитов с нами!
                                Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
                                Почему выбирают нас:
                                Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
                                Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
                                Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
                                Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!
                                """
                ));
            }
            return Optional.empty();
        }

        @Override
        public void onRuleFired(DynamicRule rule) {

        }
    }