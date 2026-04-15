package com.Cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Cts.entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {
	List<Asset> findByTitleId(int titleId);

}
