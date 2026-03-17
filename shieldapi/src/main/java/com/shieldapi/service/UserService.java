package com.shieldapi.service;

import com.shieldapi.dto.UserRequestDTO;
import com.shieldapi.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO request);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UserRequestDTO request);
    void deleteUser(Long id);
}
