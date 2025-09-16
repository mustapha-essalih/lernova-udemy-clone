package dev.api.admin;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.admin.requests.ManagerRequest;
import dev.api.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
@RestController
public class AdminControler {
    
    private AdminService adminService;

    // --- Manager Management ---
    @GetMapping("/managers")
    public ResponseEntity<?> getAllManagers() {
        return ResponseEntity.ok(adminService.getAllManagers());
    }

    @PostMapping("/managers")
    public ResponseEntity<ApiResponse<String>> createManager(@Valid @RequestBody ManagerRequest request) {
        adminService.createManager(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(new ApiResponse<>(true, null, null));
    }

    @PutMapping("/managers/{managerId}")
    public ResponseEntity<ApiResponse<String>> updateManager( @PathVariable Integer managerId, @Valid  @RequestBody ManagerRequest request) {
        adminService.updateManager(managerId, request);
        return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(new ApiResponse<>(true, null, null));
    }

    @DeleteMapping("/managers/{managerId}")
    public ResponseEntity<ApiResponse<String>> deleteManager(@PathVariable Integer managerId) {
        adminService.deleteManager(managerId);
        return ResponseEntity.status(HttpStatus.SC_NO_CONTENT).body(new ApiResponse<String>(true, null, null));
    }

    
}
