package com.senac.biblioteca.repository;

import com.senac.biblioteca.model.Livro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Livro l where l.id = :id")
    Optional<Livro> buscarPorIdParaAtualizacao(@Param("id") Long id);
}
