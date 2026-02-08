package pro.sky.manager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
public class QueryCondition {

    @Enumerated(EnumType.STRING)
    @Column(name = "query_type", nullable = false)
    private QueryType query;

    @ElementCollection
    @CollectionTable(name = "rule_arguments",
            joinColumns = {
                    @JoinColumn(name = "rule_id"),
                    @JoinColumn(name = "condition_index")
            })
    @Column(name = "argument", nullable = false)
    @OrderColumn(name = "argument_order")
    private List<String> arguments;

    @Column(name = "negate", nullable = false)
    private boolean negate;
}