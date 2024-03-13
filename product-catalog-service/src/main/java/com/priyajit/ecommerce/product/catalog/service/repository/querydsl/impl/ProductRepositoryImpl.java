package com.priyajit.ecommerce.product.catalog.service.repository.querydsl.impl;

import com.priyajit.ecommerce.product.catalog.service.entity.Product;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductRepositoryImpl implements ProductRepository {

    private EntityManager entityManager;

    public ProductRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<Product> findProducts(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex,
            int pageSize
    ) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // fetch total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);

        Predicate[] countPredicates = createPredicates(countRoot, cb, productIds, productNamePart, productCategoryIds, productCategoryNames);

        countQuery.select(cb.count(countRoot))
                .where(countPredicates);

        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countQuery);

        long total = countTypedQuery.getSingleResult();

        // fetch actual data
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> dataRoot = dataQuery.from(Product.class);

        Predicate[] dataPredicates = createPredicates(dataRoot, cb, productIds, productNamePart, productCategoryIds, productCategoryNames);

        dataQuery.select(dataRoot)
                .where(dataPredicates);

        TypedQuery<Product> dataTypedQuery = entityManager.createQuery(dataQuery);

        List<Product> products = dataTypedQuery.getResultList();

        return new PageImpl<>(
                products,
                Pageable.ofSize(pageSize),
                total
        );

    }

    private Predicate[] createPredicates(
            Root<Product> root,
            CriteriaBuilder criteriaBuilder,
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames) {

        List<Predicate> predicates = new ArrayList<>();
        if (productIds != null) {
            predicates.add(root.get("id").in(productIds));
        }
        if (productNamePart != null) {
            predicates.add(
                    criteriaBuilder.like(criteriaBuilder.lower(
                                    root.get("title")),
                            "%" + productNamePart.toLowerCase() + "%"
                    )
            );
        }
        if (productCategoryIds != null) {
            predicates.add(root.join("taggedCategories").get("id").in(productCategoryIds));
        }
        if (productCategoryNames != null) {
            predicates.add(root.join("taggedCategories").get("name").in(productCategoryNames));
        }
        return predicates.toArray(new Predicate[]{});
    }
}
