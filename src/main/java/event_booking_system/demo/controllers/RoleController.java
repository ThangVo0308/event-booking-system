package event_booking_system.demo.controllers;

import event_booking_system.demo.components.Translator;
import event_booking_system.demo.dtos.responses.paging.Pagination;
import event_booking_system.demo.dtos.responses.paging.PaginationResponse;
import event_booking_system.demo.dtos.responses.role.RoleResponse;
import event_booking_system.demo.entities.Role;
import event_booking_system.demo.mappers.RoleMapper;
import event_booking_system.demo.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Role APIs")
public class RoleController {

    RoleService roleService;
    Translator translator;
    RoleMapper roleMapper = RoleMapper.INSTANCE;

    @Operation(summary = "Get all roles", description = "Retrieve all roles with pagination")
    @GetMapping
    public ResponseEntity<PaginationResponse<RoleResponse>> findAll(
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Role> roles = roleService.findAllRoles();
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(roleMapper::toRoleResponse)
                .toList();

        PaginationResponse<RoleResponse> response = PaginationResponse.<RoleResponse>builder()
                .items(roleResponses)
                .pagination(new Pagination(page, size, rolePage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Search roles by name", description = "Search for roles by their name")
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<RoleResponse>> search(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") String offset,
            @RequestParam(defaultValue = "10") String limit) {
        int page = Integer.parseInt(offset);
        int size = Integer.parseInt(limit);
        Pageable pageable = PageRequest.of(page, size);
        List<Role> roles = name.isEmpty() ? roleService.findAllRoles() : List.of(roleService.findByName(name));
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(roleMapper::toRoleResponse)
                .toList();

        PaginationResponse<RoleResponse> response = PaginationResponse.<RoleResponse>builder()
                .items(roleResponses)
                .pagination(new Pagination(page, size, rolePage.getTotalElements()))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}