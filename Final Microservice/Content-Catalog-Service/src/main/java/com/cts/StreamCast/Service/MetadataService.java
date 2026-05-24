package com.cts.StreamCast.Service;

import java.util.List;

import com.cts.StreamCast.Entity.TitleMetadata;
import com.cts.StreamCast.dto.MetadataRequestDTO;
import com.cts.StreamCast.dto.MetadataResponseDTO;

public interface MetadataService {

	MetadataResponseDTO createMetadata(int titleId, MetadataRequestDTO dto);

	List<MetadataResponseDTO> getMetadataByTitleId(int titleId);

	MetadataResponseDTO updateMetadata(int id, MetadataRequestDTO dto);

	void deleteMetadata(int id);

}
