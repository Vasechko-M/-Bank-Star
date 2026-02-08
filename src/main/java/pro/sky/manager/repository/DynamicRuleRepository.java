package pro.sky.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.manager.model.DynamicRule;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, Long> {

    Optional<DynamicRule> findByProductId(UUID productId);

    void deleteByProductId(UUID productId);

    boolean existsByProductId(UUID productId);
}