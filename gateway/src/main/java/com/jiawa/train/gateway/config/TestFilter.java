package com.jiawa.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ClassName: TestFilter
 * Package: com.jiawa.train.gateway.config
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/27 21:38
 * @Version 1.0
 */
@Component
public class TestFilter implements GlobalFilter, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(TestFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        LOG.info("test filter");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
