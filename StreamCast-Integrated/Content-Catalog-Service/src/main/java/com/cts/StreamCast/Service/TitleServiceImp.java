package com.cts.StreamCast.Service;

import com.cts.StreamCast.Entity.Title;

import com.cts.StreamCast.Exception.DuplicateTitleException;
import com.cts.StreamCast.Exception.TitlenotFoundException;

import java.util.List;


import com.cts.StreamCast.Repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TitleServiceImp implements TitleService {

	@Autowired

	private TitleRepository repository;

	@Override

	public Title createTitle(Title title) {
		boolean exists = repository.existsByNameAndReleaseDateAndLanguage(title.getName(), title.getReleaseDate(), title.getLanguage());
		if (exists) {

			throw new DuplicateTitleException("Duplicate title not allowed");

		}
		return repository.save(title);

	}

	@Override

	public List<Title> getAllTitles() {

		return repository.findAll();

	}

	@Override

	public Title getTitleById(int id) {

		return repository.findById(id)

				.orElseThrow(() -> new TitlenotFoundException("Title not found with ID: " + id));

	}

	@Override

	public Title updateTitle(int id, Title newTitle) {

		Title existing = repository.findById(id)

				.orElseThrow(() -> new TitlenotFoundException("Title not found with ID: " + id));

		existing.setName(newTitle.getName());

		existing.setGenre(newTitle.getGenre());

		existing.setLanguage(newTitle.getLanguage());

		existing.setStatus(newTitle.getStatus());

		existing.setReleaseDate(newTitle.getReleaseDate());

		return repository.save(existing);

	}

	@Override

	public void deleteTitle(int id) {

		if (!repository.existsById(id)) {

			throw new TitlenotFoundException("Title not found with ID: " + id);

		}

		repository.deleteById(id);

	}

}