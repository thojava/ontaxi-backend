package vn.ontaxi.hub.api;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface PredicateBuilder<E> {
    Predicate build(CriteriaBuilder criteriaBuilder, Root<E> root);
}
