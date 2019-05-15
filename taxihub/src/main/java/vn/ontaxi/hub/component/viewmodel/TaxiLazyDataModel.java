package vn.ontaxi.hub.component.viewmodel;

import vn.ontaxi.common.jpa.entity.AbstractEntity;
import vn.ontaxi.hub.service.LazyDataService;
import vn.ontaxi.hub.api.PredicateBuilder;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.common.constant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Tran Xuan Quang on 12/11/2017.
 */
public class TaxiLazyDataModel<T extends AbstractEntity> extends LazyDataModel<T> {
    private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.ASCENDING;
    private static final String DEFAULT_SORT_FIELD = "id";
    private final LazyDataService lazyDataService;
    private final Sort.Order defaultOrder;
    private final JpaSpecificationExecutor<T> jpaSpecificationExecutor;
    private List<PredicateBuilder<T>> predicateBuilders = new ArrayList<>();

    public TaxiLazyDataModel(LazyDataService lazyDataService, JpaSpecificationExecutor<T> jpaSpecificationExecutor, BookingOrder bookingOrder) {
        this.lazyDataService = lazyDataService;
        this.jpaSpecificationExecutor = jpaSpecificationExecutor;
        if (bookingOrder == BookingOrder.DEPARTURE_TIME_ASC) {
            defaultOrder = new Sort.Order(Sort.Direction.ASC, "departureTime");
        } else if (bookingOrder == BookingOrder.ARRIVAL_TIME_DESC) {
            defaultOrder = new Sort.Order(Sort.Direction.DESC, "arrivalTime");
        } else if (bookingOrder == BookingOrder.ARRIVAL_TIME_ASC) {
            defaultOrder = new Sort.Order(Sort.Direction.ASC, "arrivalTime");
        } else if (bookingOrder == BookingOrder.ID_DESC) {
            defaultOrder = new Sort.Order(Sort.Direction.DESC, "id");
        } else {
            defaultOrder = new Sort.Order(Sort.Direction.ASC, DEFAULT_SORT_FIELD);
        }
    }


    @Override
    public Object getRowKey(T object) {
        return object.getKey();
    }

    @Override
    public T getRowData(String rowKey) {
        @SuppressWarnings("unchecked")
        List<T> bookings = (List<T>) super.getWrappedData();

        return bookings.stream().filter(booking -> rowKey.equals(booking.getKey())).findAny().orElse(null);
    }

    @Override
    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        Sort sort = new Sort(getDirection(DEFAULT_SORT_ORDER), DEFAULT_SORT_FIELD);

        if (multiSortMeta != null) {
            List<Sort.Order> orders = multiSortMeta.stream()
                    .map(m -> new Sort.Order(getDirection(m.getSortOrder() != null ? m.getSortOrder() : DEFAULT_SORT_ORDER),
                            m.getSortField()))
                    .collect(Collectors.toList());
            sort = new Sort(orders);
        }

        return filterAndSort(first, pageSize, filters, sort);
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Sort sort;
        if (sortField != null) {
            sort = new Sort(defaultOrder, new Sort.Order(getDirection(sortOrder != null ? sortOrder : DEFAULT_SORT_ORDER), sortField));
        } else {
            sort = new Sort(defaultOrder, new Sort.Order(getDirection(sortOrder != null ? sortOrder : DEFAULT_SORT_ORDER), DEFAULT_SORT_FIELD));
        }

        return filterAndSort(first, pageSize, filters, sort);
    }

    private List<T> filterAndSort(int first, int pageSize, Map<String, Object> filters, Sort sort) {
        Map<String, String> filtersMap = filters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));

        Page<T> page = lazyDataService.getPageOfEntities(predicateBuilders, filtersMap, new PageRequest(first / pageSize, pageSize, sort), jpaSpecificationExecutor);

        this.setRowCount(((Number) page.getTotalElements()).intValue());
        this.setWrappedData(page.getContent());
        return page.getContent();
    }

    private static Sort.Direction getDirection(SortOrder order) {
        switch (order) {
            case ASCENDING:
                return Sort.Direction.ASC;
            case DESCENDING:
                return Sort.Direction.DESC;
            case UNSORTED:
            default:
                return null;
        }
    }

    public Long getTotalSize() {
        return lazyDataService.countEntites(predicateBuilders, new HashMap<>(), jpaSpecificationExecutor);
    }

    public void addPredicate(PredicateBuilder<T> predicateBuilder) {
        predicateBuilders.add(predicateBuilder);
    }
}
