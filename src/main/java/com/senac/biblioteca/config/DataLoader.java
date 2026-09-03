package com.senac.biblioteca.config;

import com.senac.biblioteca.model.Livro;
import com.senac.biblioteca.repository.LivroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final LivroRepository livroRepository;

    public DataLoader(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Override
    public void run(String... args) {
        if (livroRepository.count() == 0) {
            livroRepository.save(new Livro(null, "Clean Code", "Robert C. Martin", "9780132350884", 3, 3));
            livroRepository.save(new Livro(null, "Dom Casmurro", "Machado de Assis", "9788525406958", 5, 5));
            livroRepository.save(new Livro(null, "O Senhor dos Aneis", "J.R.R. Tolkien", "9788533613379", 2, 2));
        }
    }
}
