package com.Albumstore.album.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Repository component 
public interface AlbumRepository extends CrudRepository<Album, Long> {
	// This interface extends CrudRepository, providing CRUD operations for the Album entity.
}