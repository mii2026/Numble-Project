package com.example.tracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TrackingControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void beforeEach(){
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    public void addHitsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/hits?url=www.google.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value("success"));
    }

    @Test
    public void getHitsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/hits?url=www.google.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalHit").value("0"))
                .andExpect(jsonPath("todayHit").value("0"));
    }

    @Test
    public void getHistoryTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/history?url=www.google.com"))
                .andExpect(status().isOk());
    }
}
