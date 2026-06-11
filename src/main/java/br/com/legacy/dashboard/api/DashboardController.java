package br.com.legacy.dashboard.api;

import br.com.legacy.dashboard.application.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse buscaDashboard(Authentication authentication) {
        return dashboardService.buscaDashboard(authentication.getName());
    }
}
