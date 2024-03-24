package com.priyajit.ecommerce.product.catalog.service.esdoc;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductPrice;
import lombok.*;
import org.springframework.util.comparator.Comparators;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceDoc implements Comparable<ProductPriceDoc> {

    private String id;
    private BigDecimal price;
    private String currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPriceDoc productPriceDoc = (ProductPriceDoc) o;
        return Objects.equals(id, productPriceDoc.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static ProductPriceDoc buildFrom(ProductPrice productPrice) {
        if (productPrice == null) return null;

        return ProductPriceDoc.builder()
                .id(productPrice.getId())
                .price(productPrice.getPrice())
                .currency(productPrice.getCurrency() == null ? null : productPrice.getCurrency().getName())
                .build();
    }

    @Override
    public int compareTo(ProductPriceDoc o) {
        return Comparators.comparable().compare(this.getId(), o.getId());
    }
}
