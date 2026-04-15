package com.Cts.service;

import java.util.List;

import com.Cts.ExceptionHandler.TitlenotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Cts.entity.Asset;
import com.Cts.ExceptionHandler.AssetNotFoundException;
import com.Cts.repository.AssetRepository;
import com.Cts.repository.TitleRepository;

@Service

public class AssetServiceImpl implements AssetService {

	@Autowired

	private AssetRepository repository;

	@Autowired

	private TitleRepository titleRepository;

	@Override

	public Asset createAsset(Asset asset) {

		if (!titleRepository.existsById(asset.getTitleId())) {

			throw new TitlenotFoundException("Invalid Title ID");

		}

		return repository.save(asset);

	}

	@Override

	public List<Asset> getAssetsByTitleId(int titleId) {

		if (!titleRepository.existsById(titleId)) {

			throw new TitlenotFoundException("Title not found");

		}

		return repository.findByTitleId(titleId);

	}

	@Override

	public Asset updateAsset(int id, Asset newAsset) {

		Asset existing = repository.findById(id)

				.orElseThrow(() -> new AssetNotFoundException("Asset not found with ID: " + id));

		existing.setAssetType(newAsset.getAssetType());

		existing.setFileURI(newAsset.getFileURI());

		existing.setChecksum(newAsset.getChecksum());

		existing.setDuration(newAsset.getDuration());

		existing.setStatus(newAsset.getStatus());
		existing.setTitleId(newAsset.getTitleId());
		return repository.save(existing);

	}

	@Override

	public void deleteAsset(int id) {

		if (!repository.existsById(id)) {

			throw new AssetNotFoundException("Asset not found with ID: " + id);

		}

		repository.deleteById(id);

	}

}