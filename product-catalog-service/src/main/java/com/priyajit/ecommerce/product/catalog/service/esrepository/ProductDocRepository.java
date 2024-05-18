package com.priyajit.ecommerce.product.catalog.service.esrepository;

import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductDocRepository {
    List<String> indexAll(List<ProductDoc> productDocs);

    Page<ProductDoc> search(String searchKeyword, int pageIndex, int pageSize);

    List<String> deleteAll(List<String> productDocIds);
}
