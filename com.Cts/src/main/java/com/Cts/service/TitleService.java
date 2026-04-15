package com.Cts.service;

import java.util.List;


import com.Cts.entity.Title;
import com.Cts.service.TitleService;


public interface TitleService {

	Title createTitle(Title title);

	List<Title> getAllTitles();

	Title getTitleById(int id);

	Title updateTitle(int id, Title title);

	void deleteTitle(int id);

}
