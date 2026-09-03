package com.senac.biblioteca.service;

import com.senac.biblioteca.model.Emprestimo;
import com.senac.biblioteca.model.StatusEmprestimo;
import com.senac.biblioteca.repository.EmprestimoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmprestimoServiceTest {

    private final EmprestimoRepository emprestimoRepository = mock(EmprestimoRepository.class);
    private final LivroService livroService = mock(LivroService.class);
    private final EmprestimoService emprestimoService =
            new EmprestimoService(emprestimoRepository, livroService);

    @Test
    void deveRecusarEmprestimoQueJaFoiDevolvido() {
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.DEVOLVIDO);
        when(emprestimoRepository.buscarPorIdParaAtualizacao(1L))
                .thenReturn(Optional.of(emprestimo));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> emprestimoService.devolver(1L)
        );

        assertEquals(409, erro.getStatusCode().value());
        verify(livroService, never()).incrementarDisponibilidade(emprestimo.getLivroId());
        verify(emprestimoRepository, never()).save(emprestimo);
    }

    @Test
    void deveDevolverEmprestimoAtivoUmaUnicaVez() {
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO);
        when(emprestimoRepository.buscarPorIdParaAtualizacao(1L))
                .thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);

        Emprestimo devolvido = emprestimoService.devolver(1L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, devolvido.getStatus());
        assertEquals(LocalDate.now(), devolvido.getDataDevolucaoReal());
        verify(livroService).incrementarDisponibilidade(2L);
        verify(emprestimoRepository).save(emprestimo);
    }

    private Emprestimo criarEmprestimo(StatusEmprestimo status) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(1L);
        emprestimo.setLivroId(2L);
        emprestimo.setStatus(status);
        return emprestimo;
    }
}
