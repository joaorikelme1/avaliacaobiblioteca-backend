package com.senac.biblioteca.controller;

import com.senac.biblioteca.service.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LivroService livroService;

    @Test
    void deveRecusarLivroInvalidoAoCriar() throws Exception {
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": " ",
                                  "autor": "",
                                  "quantidadeTotal": -1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(livroService);
    }

    @Test
    void deveRecusarLivroInvalidoAoAtualizar() throws Exception {
        mockMvc.perform(put("/api/livros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Livro",
                                  "autor": "Autor"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(livroService);
    }
}
