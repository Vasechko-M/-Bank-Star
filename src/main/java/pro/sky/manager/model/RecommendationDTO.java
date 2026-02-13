package pro.sky.manager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dynamic_rules")
@Schema(description = "Рекомендация для пользователя")
public class RecommendationDTO {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Schema(description = "ID рекомендации")
    private UUID id;

    @Column(name = "product_id", nullable = false)
    @Schema(description = "ID продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    @Schema(description = "Название продукта", example = "Простой кредит")
    private String productName;

    @Column(name = "product_text", nullable = false, length = 2000)
    @Schema(description = "Текст рекомендации", example = "Откройте мир выгодных кредитов...")
    private String productText;

    public RecommendationDTO() {
    }

    public RecommendationDTO(UUID id, UUID productId, String productName, String productText) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productText = productText;
    }

    public RecommendationDTO(UUID productId, String productName, String productText) {
        this.productId = productId;
        this.productName = productName;
        this.productText = productText;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationDTO that = (RecommendationDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(productText, that.productText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, productName, productText);
    }

    @Override
    public String toString() {
        return "RecommendationDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productText='" + productText + '\'' +
                '}';
    }
}