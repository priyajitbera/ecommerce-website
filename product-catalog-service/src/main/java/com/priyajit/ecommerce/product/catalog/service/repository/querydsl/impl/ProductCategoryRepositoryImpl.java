package com.priyajit.ecommerce.product.catalog.service.repository.querydsl.impl;

import com.priyajit.ecommerce.product.catalog.service.entity.ProductCategory;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductCategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {

    private EntityManager entityManager;

    public ProductCategoryRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ProductCategory> findProductCategories(List<String> ids, List<String> names) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // fetch actual data
        CriteriaQuery<ProductCategory> dataQuery = cb.createQuery(ProductCategory.class);
        Root<ProductCategory> dataRoot = dataQuery.from(ProductCategory.class);

        Predicate[] dataPredicates = createPredicates(dataRoot, cb, ids, names);

        dataQuery.select(dataRoot)
                .where(dataPredicates);

        TypedQuery<ProductCategory> dataTypedQuery = entityManager.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    private Predicate[] createPredicates(
            Root<ProductCategory> root,
            CriteriaBuilder criteriaBuilder,
            @Nullable List<String> ids,
            @Nullable List<String> names
    ) {

        List<Predicate> predicates = new ArrayList<>();
        if (ids != null) {
            predicates.add(root.get("id").in(ids));
        }
        if (names != null) {
            predicates.add(root.get("name").in(names));
        }
        return predicates.toArray(new Predicate[]{});
    }
}
