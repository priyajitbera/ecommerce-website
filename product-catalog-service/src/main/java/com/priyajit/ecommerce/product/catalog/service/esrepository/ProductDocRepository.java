package com.priyajit.ecommerce.product.catalog.service.esrepository;

import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ProductDocRepository {
    List<String> indexAll(List<ProductDoc> productDocs);

    Page<ProductDoc> search(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable String productDescriptionPart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex, int pageSize
    );

    List<String> deleteAll(List<String> productDocIds);
}
