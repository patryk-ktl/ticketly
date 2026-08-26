package observability;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest httpRequest)
                || !(servletResponse instanceof HttpServletResponse httpResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            log.info("{} {} -> {} ({} ms)",
                    httpRequest.getMethod(), httpRequest.getRequestURI(), httpResponse.getStatus(), durationMs);
        }
    }
}
