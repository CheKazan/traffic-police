package ru.testtask.trafficpolice.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.trafficpolice.repository.AdminCarNumbersRepository;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

    private final AdminCarNumbersRepository repository;

    @GetMapping("/info")
    private String getRepositoryInfo() {
        return repository.getInfo();
    }

    @GetMapping("/init")
    private String initLastNumbers() {
        return repository.initLastNumbers();
    }

    @GetMapping("/clean")
    private String cleanAllCarNumbers() {
        return repository.cleanAllCarNumbers();
    }
}

