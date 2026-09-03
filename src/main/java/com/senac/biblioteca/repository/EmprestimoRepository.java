package com.senac.biblioteca.repository;

import com.senac.biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByLivroId(Long livroId);
}
