package org.laundry.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.laundry.entity.Laundry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LaundryCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Laundry> findLaundriesByCriteria(String name, String address) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Laundry> query = cb.createQuery(Laundry.class);
        Root<Laundry> laundry = query.from(Laundry.class);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(laundry.get("name"), "%" + name + "%"));
        }
        if (address != null && !address.isEmpty()) {
            predicates.add(cb.like(laundry.get("address"), "%" + address + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }
}
