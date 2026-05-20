package com.cts.StreamCast.Service;

import java.util.List;

import com.cts.StreamCast.Entity.Asset;
import com.cts.StreamCast.dto.AssetDTO;
import com.cts.StreamCast.dto.AssetDTORequest;

public interface AssetService {
	AssetDTO createAsset(Integer titleId, AssetDTORequest asset);

	List<AssetDTO> getAssetsByTitleId(Integer titleId);

	AssetDTO updateAsset(Integer id, AssetDTORequest asset);

	void deleteAsset(int id);
}
