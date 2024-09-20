package com.lyncas.financeiro.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class SortingHelper {

    public static List<Sort.Order> createSortOrder(String sortBy, String sortDirection) {
        Sort.Direction direction = (sortDirection != null) ?
                Sort.Direction.fromString(sortDirection) : Sort.Direction.DESC;
        return List.of(new Sort.Order(direction, sortBy));
    }

    public static Pageable createPageableWithSort(int page, int size, String sortBy, String sortDirection) {
        return PageRequest.of(page, size, Sort.by(createSortOrder(sortBy, sortDirection)));
    }

}
