package com.Cts.service;

import java.util.List;

import com.Cts.entity.TitleMetadata;

public interface MetadataService {

	TitleMetadata createMetadata(TitleMetadata metadata);

	List<TitleMetadata> getMetadataByTitleId(int titleId);

	TitleMetadata updateMetadata(int id, TitleMetadata metadata);

	void deleteMetadata(int id);

}
