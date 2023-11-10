package com.Albumstore.album;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.User;
import com.Albumstore.album.model.UserRepository;

@SpringBootApplication
public class AlbumApplication {
	private static final Logger log = LoggerFactory.getLogger(com.Albumstore.album.AlbumApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(AlbumApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner albumDemo(AlbumRepository repository) {
		return (args) -> {
			//repository.save(new Album( null, null, null, 0, 0));
			//repository.save(new Album( null, null, null, 0, 0));
			log.info("print albums");
			for (Album album : repository.findAll()) {
				log.info(album.toString());
			}	
		};
	}
	
	@Bean
	public CommandLineRunner userDemo(UserRepository urepository) {
		return (args) -> {
			//User user1 = new User("user", "$2a$06$3jYRJrg0ghaaypjZ/.g4SethoeA51ph3UD4kZi9oPkeMTpjKU5uo6", "USER");
			//User user2 = new User("admin", "$2a$10$0MMwY.IQqpsVc1jC8u7IJ.2rT8b0Cd3b3sfIBGV2zfgnPGtT4r0.C", "ADMIN");
			//urepository.save(user1);
			//urepository.save(user2);
			log.info("print users");
			for (User user : urepository.findAll()) {
				log.info(user.toString());
			}
		};
	}
	
}
