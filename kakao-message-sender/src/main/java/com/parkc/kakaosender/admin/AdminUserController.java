package com.parkc.kakaosender.admin;

import com.parkc.kakaosender.admin.dto.*;
import com.parkc.kakaosender.admin.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService service;

    public AdminUserController(AdminUserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> list() {
        return ResponseEntity.ok(service.listUsers());
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> create(@Valid @RequestBody AdminUserCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminUserResponse> update(
            @PathVariable Long id,
            @RequestBody AdminUserUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserPasswordRequest req
    ) {
        service.resetPassword(id, req);
        return ResponseEntity.ok().build();
    }
}