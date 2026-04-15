package com.Cts.service;

import java.time.LocalDateTime;
import java.util.List;

import com.Cts.ExceptionHandler.TitlenotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Cts.entity.TitleMetadata;
import com.Cts.ExceptionHandler.MetadataNotFoundException;
import com.Cts.repository.MetadataRepository;
import com.Cts.repository.TitleRepository;

@Service

public class MetadataServiceImpl implements MetadataService {

	@Autowired

	private MetadataRepository repository;

	@Autowired

	private TitleRepository titleRepository;

	@Override

	public TitleMetadata createMetadata(TitleMetadata metadata) {

		if (!titleRepository.existsById(metadata.getTitleId())) {

			throw new TitlenotFoundException("Invalid Title ID");

		}

		metadata.setUpdatedAt(LocalDateTime.now());

		return repository.save(metadata);

	}

	@Override

	public List<TitleMetadata> getMetadataByTitleId(int titleId) {

		if (!titleRepository.existsById(titleId)) {

			throw new TitlenotFoundException("Title not found");

		}

		return repository.findByTitleId(titleId);

	}

	@Override
	public TitleMetadata updateMetadata(int id, TitleMetadata newMetadata) {

		TitleMetadata existing = repository.findById(id)

				.orElseThrow(() -> new MetadataNotFoundException("Metadata not found with ID: " + id));

		existing.setKey(newMetadata.getKey());
		existing.setValue(newMetadata.getValue());
		existing.setTitleId(newMetadata.getTitleId());
		existing.setUpdatedAt(LocalDateTime.now());

		return repository.save(existing);

	}

	@Override

	public void deleteMetadata(int id) {

		if (!repository.existsById(id)) {

			throw new MetadataNotFoundException("Metadata not found with ID: " + id);

		}

		repository.deleteById(id);

	}

}
