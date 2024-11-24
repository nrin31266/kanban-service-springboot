package com.kanban.payment.repository;

import com.kanban.payment.configuration.AuthenticationRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "kanban-service", url = "http://localhost:3002/kanban",
        configuration = {AuthenticationRequestInterceptor.class})
public interface KanbanServiceClient {
}
