package pro.sky.manager.model.rules;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule_conditions")
@Data
@NoArgsConstructor
public class QueryCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false)
    private QueryType query;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "rule_arguments",
            joinColumns = @JoinColumn(name = "condition_id")
    )
    @Column(name = "argument", nullable = false)
    @OrderColumn(name = "argument_order")
    private List<String> arguments;

    @Column(name = "negate", nullable = false)
    private boolean negate;

    @Column(name = "condition_order")
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    private DynamicRule rule;
}