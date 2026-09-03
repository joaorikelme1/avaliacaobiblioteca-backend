package com.senac.biblioteca.service;

import com.senac.biblioteca.model.Livro;
import com.senac.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LivroServiceTest {

    private final LivroRepository livroRepository = mock(LivroRepository.class);
    private final LivroService livroService = new LivroService(livroRepository);

    @Test
    void deveRecusarEmprestimoQuandoNaoHaExemplarDisponivel() {
        Livro livro = new Livro(1L, "Livro", "Autor", "123", 1, 0);
        when(livroRepository.buscarPorIdParaAtualizacao(1L)).thenReturn(Optional.of(livro));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> livroService.decrementarDisponibilidade(1L)
        );

        assertEquals(409, erro.getStatusCode().value());
        assertEquals(0, livro.getQuantidadeDisponivel());
        verify(livroRepository, never()).save(livro);
    }

    @Test
    void deveDecrementarQuandoHaExemplarDisponivel() {
        Livro livro = new Livro(1L, "Livro", "Autor", "123", 1, 1);
        when(livroRepository.buscarPorIdParaAtualizacao(1L)).thenReturn(Optional.of(livro));

        livroService.decrementarDisponibilidade(1L);

        assertEquals(0, livro.getQuantidadeDisponivel());
        verify(livroRepository).save(livro);
    }
}
