package ru.testtask.trafficpolice.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * InMemory repository for getting car numbers
 *
 * @author Artur Nigmatullin
 * @version 0.0.1
 */
@Repository
@Slf4j
@Getter
public class InMemoryCarNumbersRepository implements CarNumbersRepository, AdminCarNumbersRepository {

    private static final String region = " 116 RUS";
    /**
     * First,second,third letters and digits of the last issued sequence car number
     */
    private int seqLetter1 = 0, seqLetter2 = 0, seqLetter3 = 0, seqDigits = 0;
    /**
     * Set of issued random car numbers
     */
    private final Set<String> randomNumbers = new HashSet<>();
    /**
     * Allowed Russian letters
     */
    private final List<Character> ruLetters = Arrays.asList('А', 'В', 'Е', 'К', 'М', 'Н', 'О', 'Р', 'С', 'Т', 'У', 'Х');

    /**
     * Generate and return the next sequence number
     *
     * @return the next sequence number or information text about the lack of free сar numbers
     */
    public String getNextSequenceNumber() {
        while (true) {
            if (isNoFreeNumbers()) {
                log.warn("No free car numbers");
                return "No free car numbers";
            }
            if (seqDigits < 999) ++seqDigits;
            else {
                seqDigits = 1;
                if (seqLetter3 < ruLetters.size() - 1) seqLetter3++;
                else {
                    seqLetter3 = 0;
                    if (seqLetter2 < ruLetters.size() - 1) seqLetter2++;
                    else {
                        seqLetter2 = 0;
                        if (seqLetter1 < ruLetters.size() - 1) seqLetter1++;
                    }
                }
            }
            String result = generateNumString(seqLetter1, seqLetter2, seqLetter3, seqDigits);
            if (randomNumbers.contains(result)) {
                log.info("Found same number in random numbers set: " + result + ". Removing it and generating next sequence number again");
                randomNumbers.remove(result);
            } else {
                log.info("Successful generated car number: " + result);
                return result;
            }
        }
    }

    /**
     * Generate and return the random car number except the issued one
     *
     * @return the random number or information text about the lack of free сar numbers
     */
    public String getRandomNumber() {
        int randomLetter1, randomLetter2, randomLetter3;
        int randomDigits;
        while (true) {
            randomLetter1 = (int) (Math.random() * (ruLetters.size() - seqLetter1) + seqLetter1);
            if (randomLetter1 > seqLetter1) {
                randomLetter2 = (int) (Math.random() * ruLetters.size());
                randomLetter3 = (int) (Math.random() * ruLetters.size());
                randomDigits = (int) (Math.random() * 999 + 1);
            } else {
                randomLetter2 = (int) (Math.random() * (ruLetters.size() - seqLetter2) + seqLetter2);
                if (randomLetter2 > seqLetter2) {
                    randomLetter3 = (int) (Math.random() * ruLetters.size());
                    randomDigits = (int) (Math.random() * 999 + 1);
                } else {
                    randomLetter3 = (int) (Math.random() * (ruLetters.size() - seqLetter3) + seqLetter3);
                    if (randomLetter3 > seqLetter3) {
                        randomDigits = (int) (Math.random() * 999 + 1);
                    } else
                        randomDigits = (seqDigits == 0) ? (int) (Math.random() * 999 + 1) : (int) (Math.random() * (1000 - seqDigits - 1) + seqDigits + 1);
                }
            }
            String result = generateNumString(randomLetter1, randomLetter2, randomLetter3, randomDigits);
            if (!randomNumbers.contains(result)) {
                log.info("Successful generated random car number: " + result);
                randomNumbers.add(result);
                return result;
            }
            if (isNoFreeNumbers()) {
                log.warn("No free car numbers");
                return "No free car numbers";
            }
        }
    }

    /**
     * Generate and return a well-formed car number string
     *
     * @return a well-formed car number string
     */
    public String generateNumString(int letter1, int letter2, int letter3, int digits) {
        if (digits < 10)
            return ruLetters.get(letter1) + "00" + digits + ruLetters.get(letter2) + ruLetters.get(letter3) + region;
        else if (digits < 100)
            return ruLetters.get(letter1) + "0" + digits + ruLetters.get(letter2) + ruLetters.get(letter3) + region;
        else
            return "" + ruLetters.get(letter1) + digits + ruLetters.get(letter2) + ruLetters.get(letter3) + region;
    }

    /**
     * Check for availability of car numbers
     *
     * @return unavailability of car numbers
     */
    public boolean isNoFreeNumbers() {
        return getCarNumbersCount() == 999 * 12 * 12 * 12;
    }

    /**
     * Get info about repository
     *
     * @return info about car numbers repository
     */
    public String getInfo() {
        log.info("Get all info about car numbers repository.");
        StringBuilder result = new StringBuilder();
        result.append("Application contains car numbers: ").append(getCarNumbersCount());
        result.append("<br/>Random car numbers: ").append(randomNumbers.size());
        result.append("<br/>Last sequence car number :").append(generateNumString(seqLetter1, seqLetter2, seqLetter3, seqDigits));
        result.append("<br/>All random car numbers:");
        randomNumbers.stream().sorted().forEach(x -> result.append("<br/>").append(x));
        return result.toString();
    }

    /**
     * Get count of all issued car numbers
     *
     * @return count of all issued car numbers
     */
    public int getCarNumbersCount() {
        return seqLetter1 * 999 * 12 * 12 + seqLetter2 * 999 * 12 + seqLetter3 * 999 + seqDigits + randomNumbers.size();
    }

    /**
     * Initialize repository for tests with last 10 free numbers
     *
     * @return info message
     */
    public String initLastNumbers() {
        log.info("Initialize data for test");
        randomNumbers.clear();
        seqLetter1 = 11;
        seqLetter2 = 11;
        seqLetter3 = 11;
        seqDigits = 990;
        return "Data for test initialized";
    }

    /**
     * Clean repository for tests
     *
     * @return info message
     */
    public String cleanAllCarNumbers() {
        log.info("Clean all data");
        randomNumbers.clear();
        seqLetter1 = 0;
        seqLetter2 = 0;
        seqLetter3 = 0;
        seqDigits = 0;
        return "All data erased";
    }

}
