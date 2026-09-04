package com.senac.biblioteca.service;

import com.senac.biblioteca.model.Livro;
import com.senac.biblioteca.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public Livro buscarPorId(Long id) {
        // BUG: nao trata o caso de id inexistente, lanca NoSuchElementException
        // sem tratamento, o Spring devolve 500 em vez de 404
        return livroRepository.findById(id).get();
    }

    public Livro salvar(Livro livro) {
        validarQuantidadeTotal(livro.getQuantidadeTotal());
        livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());
        return livroRepository.save(livro);
    }

    @Transactional
    public Livro atualizar(Long id, Livro dadosAtualizados) {
        Livro livro = livroRepository.buscarPorIdParaAtualizacao(id)
                .orElseThrow();
        validarQuantidadeTotal(dadosAtualizados.getQuantidadeTotal());

        int quantidadeEmprestada = livro.getQuantidadeTotal() - livro.getQuantidadeDisponivel();
        if (dadosAtualizados.getQuantidadeTotal() < quantidadeEmprestada) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A quantidade total não pode ser menor que a quantidade de exemplares emprestados"
            );
        }

        livro.setTitulo(dadosAtualizados.getTitulo());
        livro.setAutor(dadosAtualizados.getAutor());
        livro.setIsbn(dadosAtualizados.getIsbn());
        livro.setQuantidadeTotal(dadosAtualizados.getQuantidadeTotal());
        livro.setQuantidadeDisponivel(dadosAtualizados.getQuantidadeTotal() - quantidadeEmprestada);
        return livroRepository.save(livro);
    }

    public void excluir(Long id) {
        livroRepository.deleteById(id);
    }

    @Transactional
    public void decrementarDisponibilidade(Long livroId) {
        Livro livro = livroRepository.buscarPorIdParaAtualizacao(livroId)
                .orElseThrow();

        if (livro.getQuantidadeDisponivel() == null || livro.getQuantidadeDisponivel() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não há exemplares disponíveis para empréstimo"
            );
        }

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);
    }

    @Transactional
    public void incrementarDisponibilidade(Long livroId) {
        Livro livro = livroRepository.buscarPorIdParaAtualizacao(livroId)
                .orElseThrow();

        if (livro.getQuantidadeDisponivel() == null
                || livro.getQuantidadeTotal() == null
                || livro.getQuantidadeDisponivel() >= livro.getQuantidadeTotal()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A quantidade disponível não pode ultrapassar a quantidade total"
            );
        }

        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livroRepository.save(livro);
    }

    private void validarQuantidadeTotal(Integer quantidadeTotal) {
        if (quantidadeTotal == null || quantidadeTotal < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade total deve ser maior ou igual a zero"
            );
        }
    }
}
