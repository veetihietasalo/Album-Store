package com.Albumstore.album.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// Not exported by Spring Data REST. With spring.data.rest.base-path=/api it was
// served at /api/users with the password hashes, and anyone could add an ADMIN.
@RepositoryRestResource(exported = false)
public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);

}
