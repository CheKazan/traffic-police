package ru.testtask.trafficpolice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.testtask.trafficpolice.repository.InMemoryCarNumbersRepository;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class TrafficPoliceApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private InMemoryCarNumbersRepository repository;

    @BeforeEach
    void cleanRepository() {
        repository.cleanAllCarNumbers();
    }

    @Test
    void getFirstNumber() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/number/next")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("А001АА 116 RUS")))
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
    }

    @Test
    void getSequenceNumber() throws Exception {
        for (int i = 0; i < 2756; i++) {
            MvcResult mvcResult = mvc.perform(get("/number/next")
                    .contentType(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
        }
        MvcResult mvcResult = mvc.perform(get("/number/next")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("А759АЕ 116 RUS")))
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
    }

    @Test
    void getRandomNumbers() throws Exception {
        Set<String> actual = new HashSet<>();
        Set<String> expect = repository.getRandomNumbers();
        for (int i = 0; i < 50000; i++) {
            MvcResult mvcResult = mvc.perform(get("/number/random")
                    .contentType(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            actual.add(result);
            Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
        }
        Assertions.assertEquals(50000, actual.size());
        Assertions.assertEquals(actual, expect);
    }

    @Test
    void getLastNumber() throws Exception {
        repository.initLastNumbers();
        for (int i = 0; i < 9; i++) {
            MvcResult mvcResult = mvc.perform(get("/number/next")
                    .contentType(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
        }
        mvc.perform(get("/number/next")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("No free car numbers")))
                .andReturn();
    }

    @Test
    void getLastNumberRandom() throws Exception {
        repository.initLastNumbers();
        while (repository.getRandomNumbers().size() != 9) {
            MvcResult mvcResult = mvc.perform(get("/number/random")
                    .contentType(MediaType.TEXT_PLAIN))
                    .andExpect(status().isOk())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            Assertions.assertTrue(result.matches("^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2} \\d{2,3} RUS$"));
        }
        mvc.perform(get("/number/random")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("No free car numbers")))
                .andReturn();
    }
}
