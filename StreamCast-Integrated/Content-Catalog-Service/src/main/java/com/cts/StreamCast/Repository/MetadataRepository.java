package com.cts.StreamCast.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.StreamCast.Entity.TitleMetadata;

@Repository

public interface MetadataRepository extends JpaRepository<TitleMetadata, Integer> {

	List<TitleMetadata> findByTitleId(int titleId);

}
