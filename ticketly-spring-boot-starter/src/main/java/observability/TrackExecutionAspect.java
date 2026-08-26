package observability;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@RequiredArgsConstructor
public class TrackExecutionAspect {

    private static final Logger log = LoggerFactory.getLogger(TrackExecutionAspect.class);

    private final long slowCallThresholdMs;

    @Around("@annotation(TrackExecution)")
    public Object trackExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            String signature = joinPoint.getSignature().toShortString();
            if (durationMs > slowCallThresholdMs) {
                log.warn("{} took {} ms (exceeds {} ms threshold)", signature, durationMs, slowCallThresholdMs);
            } else {
                log.info("{} took {} ms", signature, durationMs);
            }
        }
    }
}
