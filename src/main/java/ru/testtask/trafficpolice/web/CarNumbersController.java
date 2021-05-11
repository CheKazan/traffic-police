package ru.testtask.trafficpolice.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.trafficpolice.repository.CarNumbersRepository;

@RestController
@RequestMapping("/number")
@AllArgsConstructor
@Slf4j
public class CarNumbersController {

    private final CarNumbersRepository repository;

    @GetMapping("/next")
    private String getSequenceNumber() {
        return repository.getNextSequenceNumber();
    }

    @GetMapping("/random")
    private String getRandomNumber() {
        return repository.getRandomNumber();
    }


}
