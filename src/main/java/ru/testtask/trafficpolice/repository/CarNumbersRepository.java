package ru.testtask.trafficpolice.repository;

public interface CarNumbersRepository {
    String getNextSequenceNumber();

    String getRandomNumber();
}
