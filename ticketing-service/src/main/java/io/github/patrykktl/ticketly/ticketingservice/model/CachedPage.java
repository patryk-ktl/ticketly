package io.github.patrykktl.ticketly.ticketingservice.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record CachedPage<T>(List<T> content, long totalElements, int pageNumber, int pageSize) {

    public static <T> CachedPage<T> from(Page<T> page) {
        return new CachedPage<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    public Page<T> toPage(Pageable pageable) {
        return new PageImpl<>(content, pageable, totalElements);
    }
}
