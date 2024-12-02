package com.rin.kanban.repository.httpclient;

import com.rin.kanban.configuration.AuthenticationRequestInterceptor;
import com.rin.kanban.dto.ApiResponse;
import com.rin.kanban.dto.httpclient.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profiles}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface ProfileClient {

    @GetMapping("internal/users/{userId}")
    UserProfileResponse getProfile(
            @PathVariable("userId") String userId
    );
}
