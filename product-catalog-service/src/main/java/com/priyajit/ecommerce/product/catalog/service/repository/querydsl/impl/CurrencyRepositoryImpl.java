package com.priyajit.ecommerce.product.catalog.service.repository.querydsl.impl;

import com.priyajit.ecommerce.product.catalog.service.entity.Currency;
import com.priyajit.ecommerce.product.catalog.service.repository.querydsl.CurrencyRepository;
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
import java.util.stream.Collectors;

@Component
public class CurrencyRepositoryImpl implements CurrencyRepository {

    private EntityManager entityManager;

    public CurrencyRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Currency> searchCurrency(@Nullable List<String> ids, @Nullable List<String> names) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // fetch actual data
        CriteriaQuery<Currency> dataQuery = cb.createQuery(Currency.class);
        Root<Currency> dataRoot = dataQuery.from(Currency.class);

        Predicate[] dataPredicates = createPredicates(dataRoot, cb, ids, names);

        dataQuery.select(dataRoot)
                .where(dataPredicates);

        TypedQuery<Currency> dataTypedQuery = entityManager.createQuery(dataQuery);

        return dataTypedQuery.getResultList();
    }

    private Predicate[] createPredicates(
            Root<Currency> root,
            CriteriaBuilder cb,
            @Nullable List<String> ids,
            @Nullable List<String> names
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (ids != null) {
            predicates.add(root.get("id").in(ids));
        }
        if (names != null) {
            if (names.size() > 0) {
                predicates.add(
                        cb.or(
                                names.stream()
                                        .map(name -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"))
                                        .collect(Collectors.toList())
                                        .toArray(new Predicate[]{})
                        )
                );
            }
        }

        return predicates.toArray(new Predicate[]{});
    }
}
