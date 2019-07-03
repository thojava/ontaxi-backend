package vn.ontaxi.hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import vn.ontaxi.hub.api.PredicateBuilder;
import vn.ontaxi.hub.component.UserCredentialComponent;
import vn.ontaxi.common.jpa.entity.AbstractEntity;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LazyDataService {
    private final UserCredentialComponent userCredentialComponent;

    @Autowired
    public LazyDataService(UserCredentialComponent userCredentialComponent) {
        this.userCredentialComponent = userCredentialComponent;
    }

    public <T extends AbstractEntity> Page<T> getPageOfEntities(List<PredicateBuilder<T>> predicateBuilders, Map<String, String> filterValues, Pageable pageable, JpaSpecificationExecutor<T> jpaSpecificationExecutor) {
        Specification<T> filterSpecification = getFilterSpecification(predicateBuilders, filterValues);

        return jpaSpecificationExecutor.findAll(filterSpecification, pageable);
    }

    private <T> Specification<T> getFilterSpecification(List<PredicateBuilder<T>> predicateBuilders, Map<String, String> filterValues) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Optional<Predicate> predicate = filterValues.entrySet().stream()
                    .filter(v -> v.getValue() != null && v.getValue().length() > 0)
                    .map(entry -> {
                        Path<?> path = root;
                        String key = entry.getKey();
                        if (entry.getKey().contains(".")) {
                            String[] splitKey = entry.getKey().split("\\.");
                            path = root.join(splitKey[0]);
                            key = splitKey[1];
                        }
                        return builder.like(path.get(key).as(String.class), "%" + entry.getValue() + "%");
                    }).reduce(builder::and);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(predicate.orElseGet(() -> alwaysTrue(builder)));
            for (PredicateBuilder<T> predicateBuilder : predicateBuilders) {
                predicates.add(predicateBuilder.build(builder, root));
            }
            if (!userCredentialComponent.hasViewAllPermission()) {
                predicates.add(builder.equal(root.get("createdBy"), userCredentialComponent.getUserName()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate alwaysTrue(CriteriaBuilder builder) {
        return builder.isTrue(builder.literal(true));
    }

    public <T extends AbstractEntity> Long countEntites(List<PredicateBuilder<T>> predicateBuilders, Map<String, String> filterValues, JpaSpecificationExecutor<T> jpaSpecificationExecutor) {
        Specification<T> filterSpecification = getFilterSpecification(predicateBuilders, filterValues);
        return jpaSpecificationExecutor.count(filterSpecification);
    }
}

