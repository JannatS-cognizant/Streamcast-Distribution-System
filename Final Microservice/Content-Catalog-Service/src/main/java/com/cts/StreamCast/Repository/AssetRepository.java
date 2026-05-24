package com.cts.StreamCast.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.StreamCast.Entity.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {
	List<Asset> findByTitleId(Integer titleId);

}
