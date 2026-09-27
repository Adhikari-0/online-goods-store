package com.store.user;

import com.store.common.dto.PageResponse;
import com.store.user.dto.CreateUserRequest;
import com.store.user.dto.UpdateUserRequest;
import com.store.user.dto.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse create(CreateUserRequest request);

    UserResponse getById(Long id);

    UserResponse getByEmail(String email);

    PageResponse<UserResponse> list(Pageable pageable);

    UserResponse update(Long id, UpdateUserRequest request);

    void changeStatus(Long id, UserStatusEnum status);

    void softDelete(Long id);

    void recordLogin(Long id);

    User getEntityById(Long id);
}