package ru.testtask.trafficpolice.repository;

public interface AdminCarNumbersRepository {
    String getInfo();

    String initLastNumbers();

    String cleanAllCarNumbers();
}
