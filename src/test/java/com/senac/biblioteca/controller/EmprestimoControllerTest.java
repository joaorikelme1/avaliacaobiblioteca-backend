package com.senac.biblioteca.controller;

import com.senac.biblioteca.service.EmprestimoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmprestimoController.class)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmprestimoService emprestimoService;

    @Test
    void deveRecusarEmprestimoComDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/emprestimos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "livroId": 0,
                                  "nomeUsuario": " "
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emprestimoService);
    }
}
