package com.senac.biblioteca.service;

import com.senac.biblioteca.model.Livro;
import com.senac.biblioteca.repository.LivroRepository;
import org.springframework.stereotype.Service;

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
        livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());
        return livroRepository.save(livro);
    }

    public Livro atualizar(Long id, Livro dadosAtualizados) {
        Livro livro = buscarPorId(id);
        livro.setTitulo(dadosAtualizados.getTitulo());
        livro.setAutor(dadosAtualizados.getAutor());
        livro.setIsbn(dadosAtualizados.getIsbn());
        livro.setQuantidadeTotal(dadosAtualizados.getQuantidadeTotal());
        return livroRepository.save(livro);
    }

    public void excluir(Long id) {
        livroRepository.deleteById(id);
    }

    public void decrementarDisponibilidade(Long livroId) {
        Livro livro = buscarPorId(livroId);
        // BUG: nao verifica se quantidadeDisponivel > 0 antes de decrementar,
        // permitindo que o estoque de livros disponiveis fique negativo
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);
    }

    public void incrementarDisponibilidade(Long livroId) {
        Livro livro = buscarPorId(livroId);
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livroRepository.save(livro);
    }
}
