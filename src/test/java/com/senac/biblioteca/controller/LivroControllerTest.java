package com.senac.biblioteca.controller;

import com.senac.biblioteca.service.LivroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados inválidos."))
                .andExpect(jsonPath("$.path").value("/api/livros"))
                .andExpect(jsonPath("$.errors").isArray());

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

    @Test
    void devePreservarStatusEMensagemDoServico() throws Exception {
        when(livroService.buscarPorId(99L)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado")
        );

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Livro não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/livros/99"));
    }

    @Test
    void deveTratarParametroDeRotaInvalido() throws Exception {
        mockMvc.perform(get("/api/livros/invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("O parâmetro 'id' possui valor inválido."))
                .andExpect(jsonPath("$.path").value("/api/livros/invalido"));
    }
}
