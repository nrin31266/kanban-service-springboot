package com.rin.kanban.repository.httpclient;


import com.rin.kanban.dto.request.ProfileCreationRequest;
import com.rin.kanban.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "profile-client",
    url = "${app.service.profile}"
)
public interface ProfileClient {
    @PostMapping(value = "/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
    UserProfileResponse createUserProfile(@RequestBody ProfileCreationRequest request);
    @GetMapping("/internal/users/{userId}")
    UserProfileResponse getUserProfile(@PathVariable("userId") String userId);

}
