package com.cts.StreamCast.Repository;

import com.cts.StreamCast.Entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TitleRepository extends JpaRepository<Title,Integer> {
    boolean existsByNameAndReleaseDateAndLanguage(String name, LocalDate releaseDate, String language);
}
