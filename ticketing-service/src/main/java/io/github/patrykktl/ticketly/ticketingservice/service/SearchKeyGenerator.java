package io.github.patrykktl.ticketly.ticketingservice.service;

import org.jspecify.annotations.Nullable;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("searchKeyGenerator")
public class SearchKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, @Nullable Object... params) {
        StringBuilder key = new StringBuilder();

        for (Object param : params) {
            if (param == null) {
                key.append("null;");
            } else if (param instanceof Pageable pageable) {
                key.append("p=").append(pageable.getPageNumber())
                        .append(",s=").append(pageable.getPageSize())
                        .append(",sort=");

                if (pageable.getSort().isSorted()) {
                    for (Sort.Order order : pageable.getSort()) {
                        key.append(order.getProperty())
                                .append(":")
                                .append(order.getDirection())
                                .append(",");
                    }
                } else {
                    key.append("UNSORTED");
                }
                key.append(";");
            } else {
                key.append(param).append(";");
            }
        }

        return key.toString();
    }
}
