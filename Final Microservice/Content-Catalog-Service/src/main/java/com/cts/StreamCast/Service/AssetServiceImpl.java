package com.cts.StreamCast.Service;

import java.util.List;

import com.cts.StreamCast.Entity.Title;
import com.cts.StreamCast.Exception.TitlenotFoundException;
import com.cts.StreamCast.dto.AssetDTO;
import com.cts.StreamCast.dto.AssetDTORequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.StreamCast.Entity.Asset;
import com.cts.StreamCast.Exception.AssetNotFoundException;
import com.cts.StreamCast.Repository.AssetRepository;
import com.cts.StreamCast.Repository.TitleRepository;

@Service

public class AssetServiceImpl implements AssetService {

	@Autowired

	private AssetRepository repository;

	@Autowired

	private TitleRepository titleRepository;

	@Override

	public AssetDTO createAsset(Integer titleId, AssetDTORequest dto ) {
		Title title = titleRepository.findById(titleId)
				.orElseThrow(() -> new TitlenotFoundException("Title not found"));
		Asset asset = new Asset();
		asset.setAssetType(dto.getAssetType());
		asset.setFileURI(dto.getFileURI());
		asset.setChecksum(dto.getChecksum());
		asset.setDuration(dto.getDuration());
		asset.setStatus(dto.getStatus());
		asset.setTitle(title);
		Asset saved = repository.save(asset);
		return convertToDTO(saved);

	}


	@Override

	public AssetDTO updateAsset(Integer id, AssetDTORequest newAsset) {

		Asset existing = repository.findById(id)

				.orElseThrow(() -> new AssetNotFoundException("Asset not found with ID: " + id));

		existing.setAssetType(newAsset.getAssetType());

		existing.setFileURI(newAsset.getFileURI());

		existing.setChecksum(newAsset.getChecksum());

		existing.setDuration(newAsset.getDuration());

		existing.setStatus(newAsset.getStatus());

		Asset updated = repository.save(existing);
		return convertToDTO(updated);

	}

	@Override

	public void deleteAsset(int id) {

		if (!repository.existsById(id)) {

			throw new AssetNotFoundException("Asset not found with ID: " + id);

		}

		repository.deleteById(id);

	}
	@Override
	public List<AssetDTO> getAssetsByTitleId(Integer titleId) {
		List<Asset> assets = repository.findByTitleId(titleId);
		return assets.stream().map(asset -> {
			AssetDTO dto = new AssetDTO();
			dto.setId(asset.getId());
			dto.setAssetType(asset.getAssetType());
			dto.setFileURI(asset.getFileURI());
			dto.setChecksum(asset.getChecksum());
			dto.setDuration(asset.getDuration());
			dto.setStatus(asset.getStatus());
			dto.setTitleId(asset.getTitle().getId());
			dto.setTitleName(asset.getTitle().getName());
			return dto;
		}).toList();

	}
	public AssetDTO convertToDTO(Asset asset) {
		AssetDTO dto = new AssetDTO();
		dto.setId(asset.getId());
		dto.setAssetType(asset.getAssetType());
		dto.setFileURI(asset.getFileURI());
		dto.setChecksum(asset.getChecksum());
		dto.setDuration(asset.getDuration());
		dto.setStatus(asset.getStatus());
		dto.setTitleId(asset.getTitle().getId());
		dto.setTitleName(asset.getTitle().getName());
		return dto;
	}

}