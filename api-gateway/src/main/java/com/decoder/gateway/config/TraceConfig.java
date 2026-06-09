package com.decoder.gateway.config;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class TraceConfig {

    private final Tracer tracer;

    @Bean
    public GlobalFilter traceContextFilter() {
        return (exchange, chain) -> {
            var span = tracer.currentSpan();
            if (span != null) {
                var request = exchange.getRequest().mutate()
                    .header("X-Trace-Id", span.context().traceId())
                    .header("X-Span-Id", span.context().spanId())
                    .build();
                return chain.filter(exchange.mutate().request(request).build());
            }
            return chain.filter(exchange);
        };
    }
}
