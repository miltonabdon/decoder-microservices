package com.decoder.course.adapter.out.client;

import com.decoder.course.adapter.out.client.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "auth-user-service", path = "/auth/users")
public interface AuthUserClient {
    @GetMapping("/{userId}")
    ResponseEntity<UserResponseDto> getUserById(@PathVariable("userId") UUID userId);
}
