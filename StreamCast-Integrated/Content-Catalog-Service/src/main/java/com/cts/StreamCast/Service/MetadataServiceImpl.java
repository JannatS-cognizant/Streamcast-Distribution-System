package com.cts.StreamCast.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.cts.StreamCast.Entity.Title;
import com.cts.StreamCast.Exception.TitlenotFoundException;
import com.cts.StreamCast.dto.MetadataRequestDTO;
import com.cts.StreamCast.dto.MetadataResponseDTO;
import com.cts.StreamCast.dto.TitleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.StreamCast.Entity.TitleMetadata;
import com.cts.StreamCast.Exception.MetadataNotFoundException;
import com.cts.StreamCast.Repository.MetadataRepository;
import com.cts.StreamCast.Repository.TitleRepository;

@Service

public class MetadataServiceImpl implements MetadataService {

	@Autowired

	private MetadataRepository repository;

	@Autowired

	private TitleRepository titleRepository;

	@Override

	public MetadataResponseDTO createMetadata(int titleId, MetadataRequestDTO dto) {
		Title title = titleRepository.findById(titleId)
				.orElseThrow(() -> new TitlenotFoundException("Title not found"));
		TitleMetadata metadata = new TitleMetadata();
		metadata.setKey(dto.getKey());
		metadata.setValue(dto.getValue());
		metadata.setTitle(title);
		metadata.setUpdatedAt(LocalDateTime.now());
		TitleMetadata saved = repository.save(metadata);
		return mapToResponseDTO(saved);
	}



	@Override

	public List<MetadataResponseDTO> getMetadataByTitleId(int titleId) {

		if (!titleRepository.existsById(titleId)) {
			throw new TitlenotFoundException("Title not found with id " + titleId);
		}

		List<TitleMetadata> list = repository.findByTitleId(titleId);
		return list.stream().map(this::mapToResponseDTO).toList();

	}

	@Override

	public MetadataResponseDTO updateMetadata(int id, MetadataRequestDTO dto) {
		TitleMetadata metadata = repository.findById(id)
				.orElseThrow(() -> new MetadataNotFoundException("Metadata not found with ID: " + id));
		metadata.setKey(dto.getKey());
		metadata.setValue(dto.getValue());
		metadata.setUpdatedAt(LocalDateTime.now());
		TitleMetadata updated = repository.save(metadata);
		return mapToResponseDTO(updated);

	}

	@Override

	public void deleteMetadata(int id) {

		if (!repository.existsById(id)) {

			throw new MetadataNotFoundException("Metadata not found with ID: " + id);

		}

		repository.deleteById(id);

	}
	private MetadataResponseDTO mapToResponseDTO(TitleMetadata metadata) {
		MetadataResponseDTO dto = new MetadataResponseDTO();
		dto.setId(metadata.getId());
		dto.setKey(metadata.getKey());
		dto.setValue(metadata.getValue());
		TitleDTO titleDTO = new TitleDTO();
		titleDTO.setId(metadata.getTitle().getId());
		titleDTO.setName(metadata.getTitle().getName());
		dto.setTitleDTO(titleDTO);
		return dto;

	}

}
