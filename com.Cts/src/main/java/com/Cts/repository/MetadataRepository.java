package com.Cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Cts.entity.TitleMetadata;

@Repository

public interface MetadataRepository extends JpaRepository<TitleMetadata, Integer> {

	List<TitleMetadata> findByTitleId(int titleId);

}
