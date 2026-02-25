package pro.sky.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.manager.model.rules.DynamicRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {

    Optional<DynamicRule> findByProductId(UUID productId);

    void deleteByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    @Query("SELECT DISTINCT r FROM DynamicRule r LEFT JOIN FETCH r.rule ORDER BY r.createdAt DESC")
    List<DynamicRule> findAllWithConditions();
}