package com.cts.StreamCast.Service;

import java.util.List;


import com.cts.StreamCast.Entity.Title;


public interface TitleService {

	Title createTitle(Title title);

	List<Title> getAllTitles();

	Title getTitleById(int id);

	Title updateTitle(int id, Title title);

	void deleteTitle(int id);

}
