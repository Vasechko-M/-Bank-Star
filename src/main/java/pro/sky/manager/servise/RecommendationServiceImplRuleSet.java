package pro.sky.manager.servise;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import pro.sky.manager.model.RecommendationDTO;
import pro.sky.manager.repository.RecommendationRuleSet;

@Service
public class RecommendationServiceImplRuleSet implements RecommendationRuleSet {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public RecommendationServiceImplRuleSet(DataSource dataSource) {
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

//    @Override
//    public Optional<RecommendationDTO> check(UUID userId) {
//        try {
//            // Подготовленный SQL-запрос для подсчета агрегатов
//            String sqlQuery = """
//    SELECT t.USER_ID, p.TYPE AS product_type, t.TYPE AS transaction_type, SUM(t.AMOUNT) AS total_sum
//    FROM TRANSACTIONS t
//    INNER JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
//    WHERE t.USER_ID = :userId
//    GROUP BY t.USER_ID, p.TYPE, t.TYPE;
//""";
//
//            RowMapper<UserTransactionSummary> rowMapper = (rs, rowNum) ->
//                    new UserTransactionSummary(
//                            rs.getString("USER_ID"),
//                            rs.getString("PRODUCT_TYPE"),
//                            rs.getString("TYPE"),
//                            rs.getLong("TOTAL_SUM")
//                    );
//
//            var results = namedJdbcTemplate.query(sqlQuery, Map.of("userId", userId.toString()), rowMapper);
//
//            // Анализ результатов и применение условий
//            boolean hasDebit = false;
//            boolean hasInvest = false;
//            boolean hasCredit = false;
//            long sumSaving = 0L;
//            long sumDebitDeposit = 0L;
//            long sumSavingDeposit = 0L;
//            long sumDeposit = 0L;
//            long sumWithdrawal = 0L;
//            long debitDepositSum = 0L;
//            long debitWithdrawalSum = 0L;
//
//            for (var summary : results) {
//                switch (summary.productType()) {
//                    case "DEBIT":
//                        hasDebit = true;
//                        break;
//                    case "INVESTMENT":
//                        hasInvest = true;
//                        break;
//                    case "CREDIT":
//                        hasCredit = true;
//                        break;
//                }
//
//                switch (summary.transactionType()) {
//                    case "SAVING":
//                        sumSaving += summary.totalSum();
//                        break;
//                    case "DEPOSIT_DEBIT":
//                        sumDebitDeposit += summary.totalSum();
//                        debitDepositSum += summary.totalSum();
//                        break;
//                    case "DEPOSIT_SAVING":
//                        sumSavingDeposit += summary.totalSum();
//                        break;
//                    case "WITHDRAWAL":
//                        sumWithdrawal += summary.totalSum();
//                        if ("DEBIT".equals(summary.productType())) {
//                            debitWithdrawalSum += summary.totalSum();
//                        }
//                        break;
//                    default:
//                        sumDeposit += summary.totalSum();
//                }
//            }
//
//            // Правила согласно запросу
//            if ((hasDebit && !hasInvest && sumSaving > 1000)) {
//                return Optional.of(new RecommendationDTO(
//                        UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
//                        "Invest 500",
//                        "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
//                ));
//            } else if ((hasDebit &&
//                    (debitDepositSum > debitWithdrawalSum) &&
//                    (sumDebitDeposit >= 50000 || sumSavingDeposit >= 50000))) {
//                return Optional.of(new RecommendationDTO(
//                        UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
//                        "Top Saving",
//                        """
//                                Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!
//
//                                Преимущества «Копилки»:
//
//                                Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
//
//                                Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
//
//                                Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.
//
//                                Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"""
//                ));
//            } else if (!hasCredit && (sumDeposit > sumWithdrawal) && (sumWithdrawal > 100000)) {
//                return Optional.of(new RecommendationDTO(
//                        UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
//                        "Простой кредит",
//                        """
//                                Откройте мир выгодных кредитов с нами!
//
//                                Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
//
//                                Почему выбирают нас:
//
//                                Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
//
//                                Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
//
//                                Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
//
//                                Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"""
//                ));
//            }
//
//            return Optional.empty();
//        } catch (EmptyResultDataAccessException e) {
//            return Optional.empty();
//        }
//    }
//
//    // Вспомогательная сущность для промежуточных вычислений
//    record UserTransactionSummary(String userId, String productType, String transactionType, long totalSum) {}
//}
@Override
public Optional<RecommendationDTO> check(UUID userId) {
    return checkRecommendations(userId);
}

    public Optional<RecommendationDTO> checkRecommendations(UUID userId) {
        String[] productTypes = {"DEBIT", "CREDIT", "SAVING", "INVEST"};
        String transactionType = "DEPOSIT"; //под вопросом

        for (String productType : productTypes) {
            String sqlQuery = """
                SELECT t.USER_ID, p.TYPE AS product_type, t.TYPE AS transaction_type, SUM(t.AMOUNT) AS total_sum
                FROM TRANSACTIONS t
                INNER JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                WHERE t.USER_ID = :userId AND p.TYPE = :productType AND t.TYPE = :transactionType
                GROUP BY t.USER_ID, p.TYPE, t.TYPE;
                """;

            RowMapper<UserTransactionSummary> rowMapper = (rs, rowNum) ->
                    new UserTransactionSummary(
                            rs.getString("USER_ID"),
                            rs.getString("product_type"),
                            rs.getString("transaction_type"),
                            rs.getLong("total_sum")
                    );

            var results = namedJdbcTemplate.query(sqlQuery, Map.of(
                    "userId", userId.toString(),
                    "productType", productType,
                    "transactionType", transactionType
            ), rowMapper);

            Optional<RecommendationDTO> recommendation = analyzeResults(results, userId, productType);
            if (recommendation.isPresent()) {
                return recommendation;
            }
        }
        return Optional.empty();
    }

    private Optional<RecommendationDTO> analyzeResults(java.util.List<UserTransactionSummary> results, UUID userId, String productType) {
        boolean hasDebit = false;
        boolean hasInvest = false;
        boolean hasCredit = false;
        long sumSaving = 0L;
        long sumDebitDeposit = 0L;
        long sumSavingDeposit = 0L;
        long sumDeposit = 0L;
        long sumWithdrawal = 0L;
        long debitDepositSum = 0L;
        long debitWithdrawalSum = 0L;

        for (var summary : results) {
            switch (summary.productType()) {
                case "DEBIT":
                    hasDebit = true;
                    break;
                case "INVESTMENT":
                    hasInvest = true;
                    break;
                case "CREDIT":
                    hasCredit = true;
                    break;
            }

            switch (summary.transactionType()) {
                case "SAVING":
                    sumSaving += summary.totalSum();
                    break;
                case "DEPOSIT_DEBIT":
                    sumDebitDeposit += summary.totalSum();
                    debitDepositSum += summary.totalSum();
                    break;
                case "DEPOSIT_SAVING":
                    sumSavingDeposit += summary.totalSum();
                    break;
                case "WITHDRAWAL":
                    sumWithdrawal += summary.totalSum();
                    if ("DEBIT".equals(summary.productType())) {
                        debitWithdrawalSum += summary.totalSum();
                    }
                    break;
                default:
                    sumDeposit += summary.totalSum();
            }
        }

        if ((hasDebit && !hasInvest && sumSaving > 1000)) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                    "Invest 500",
                    "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
            ));
        } else if ((hasDebit &&
                (debitDepositSum > debitWithdrawalSum) &&
                (sumDebitDeposit >= 50000 || sumSavingDeposit >= 50000))) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                    "Top Saving",
                    "Текст  для Top Saving скопировать надо..."
            ));
        } else if (!hasCredit && (sumDeposit > sumWithdrawal) && (sumWithdrawal > 100000)) {
            return Optional.of(new RecommendationDTO(
                    UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                    "Простой кредит",
                    "Текст для кредита скопировать не забыть..."
            ));
        }
        return Optional.empty();
    }

    // Вспомогательная сущность для промежуточных вычислений
    record UserTransactionSummary(String userId, String productType, String transactionType, long totalSum) {}
}
