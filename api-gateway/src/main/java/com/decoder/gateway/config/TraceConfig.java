package com.decoder.gateway.config;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceConfig {

    @Autowired(required = false)
    private Tracer tracer;

    @Bean
    public GlobalFilter traceContextFilter() {
        return (exchange, chain) -> {
            if (tracer != null) {
                var span = tracer.currentSpan();
                if (span != null) {
                    var request = exchange.getRequest().mutate()
                        .header("X-Trace-Id", span.context().traceId())
                        .header("X-Span-Id", span.context().spanId())
                        .build();
                    return chain.filter(exchange.mutate().request(request).build());
                }
            }
            return chain.filter(exchange);
        };
    }
}
