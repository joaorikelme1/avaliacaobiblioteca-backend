package com.senac.biblioteca.repository;

import com.senac.biblioteca.model.Emprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByLivroId(Long livroId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Emprestimo e where e.id = :id")
    Optional<Emprestimo> buscarPorIdParaAtualizacao(@Param("id") Long id);
}
