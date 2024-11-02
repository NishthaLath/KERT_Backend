package com.kert;

import com.kert.config.SecurityConfig;
import com.kert.model.History;
import com.kert.service.HistoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HistoryTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private HistoryService historyService;

    private String testRequestBody;

    private final History testHistory = new History();

    @BeforeAll
    public void setUp() {
        testHistory.setYear(2001);
        testHistory.setMonth(12);
        testHistory.setContent("test");

        testRequestBody= """
                {
                    "year" : %d,
                    "month" : %d,
                    "content" : "%s"
                }
                """.formatted(testHistory.getYear(), testHistory.getMonth(), testHistory.getContent());
    }

    @BeforeEach
    public void setMockMvc() {
        this.mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @DisplayName("get all histories")
    public void getAllHistories() throws Exception {
        mockMvc.perform(get("/histories")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("get history by id")
    public void getHistoryById() throws Exception {
        when(historyService.getHistoryById(1L)).thenReturn(testHistory);

        mockMvc.perform(get("/histories/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("create history with not_admin")
    public void createHistoryWithNot_admin() throws Exception {
        when(historyService.createHistory(testHistory)).thenReturn(testHistory);

        mockMvc.perform(post("/histories").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("create history with admin")
    @WithMockUser(roles = "ADMIN")
    public void createHistoryWithAdmin() throws Exception {
        when(historyService.createHistory(testHistory)).thenReturn(testHistory);

        mockMvc.perform(post("/histories").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("update history with not_admin")
    public void updateHistoryWithNot_admin() throws Exception {
        when(historyService.updateHistory(1L, testHistory)).thenReturn(testHistory);

        mockMvc.perform(put("/histories/1").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("update history with admin")
    @WithMockUser(roles = "ADMIN")
    public void updateHistoryWithAdmin() throws Exception {
        when(historyService.updateHistory(1L, testHistory)).thenReturn(testHistory);

        mockMvc.perform(put("/histories/1").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete history with not_admin")
    public void deleteHistoryWithNot_admin() throws Exception {
        mockMvc.perform(delete("/histories/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("delete history with admin")
    @WithMockUser(roles = "ADMIN")
    public void deleteHistoryWithAdmin() throws Exception {
        mockMvc.perform(delete("/histories/1")).andExpect(status().isNoContent());
    }
}