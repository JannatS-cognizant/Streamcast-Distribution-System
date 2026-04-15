package com.Cts.service;

import java.util.List;

import com.Cts.entity.Asset;

public interface AssetService {
	Asset createAsset(Asset asset);

	List<Asset> getAssetsByTitleId(int titleId);

	Asset updateAsset(int id, Asset asset);

	void deleteAsset(int id);
}
