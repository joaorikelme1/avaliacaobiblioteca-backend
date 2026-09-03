package com.senac.biblioteca.service;

import com.senac.biblioteca.model.Emprestimo;
import com.senac.biblioteca.model.StatusEmprestimo;
import com.senac.biblioteca.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroService livroService;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroService livroService) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroService = livroService;
    }

    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAll();
    }

    public Emprestimo emprestar(Emprestimo emprestimo) {
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(7));
        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        livroService.decrementarDisponibilidade(emprestimo.getLivroId());
        return emprestimoRepository.save(emprestimo);
    }

    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElseThrow();
        emprestimo.setDataDevolucaoReal(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        livroService.incrementarDisponibilidade(emprestimo.getLivroId());
        return emprestimoRepository.save(emprestimo);
    }

    public List<Emprestimo> listarAtrasados() {
        // BUG: condicao invertida. Deveria ser isBefore(LocalDate.now()) para achar
        // emprestimos cuja devolucao prevista ja passou. Do jeito que esta,
        // devolve os emprestimos que AINDA NAO venceram, e nao os atrasados.
        return emprestimoRepository.findAll().stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO)
                .filter(e -> e.getDataDevolucaoPrevista().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
