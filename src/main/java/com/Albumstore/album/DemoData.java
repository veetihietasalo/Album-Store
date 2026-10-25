package com.Albumstore.album;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.Song;
import com.Albumstore.album.model.SongRepository;
import com.Albumstore.album.model.User;
import com.Albumstore.album.model.UserRepository;

// Sample data for the demo profile only (in-memory H2).
// The accounts user/user and admin/admin are demo credentials.
@Configuration
@Profile("demo")
public class DemoData {

	@Bean
	public CommandLineRunner loadDemoData(UserRepository users, AlbumRepository albums, SongRepository songs) {
		return (args) -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			users.save(new User("user", encoder.encode("user"), "USER"));
			users.save(new User("admin", encoder.encode("admin"), "ADMIN"));

			Album kindOfBlue = albums.save(new Album("Kind of Blue", "Miles Davis", "Jazz", 14.90, 1959));
			songs.save(new Song("So What", kindOfBlue));
			songs.save(new Song("Freddie Freeloader", kindOfBlue));
			songs.save(new Song("Blue in Green", kindOfBlue));

			Album abbeyRoad = albums.save(new Album("Abbey Road", "The Beatles", "Rock", 19.90, 1969));
			songs.save(new Song("Come Together", abbeyRoad));
			songs.save(new Song("Something", abbeyRoad));

			Album rumours = albums.save(new Album("Rumours", "Fleetwood Mac", "Rock", 17.90, 1977));
			songs.save(new Song("Dreams", rumours));
			songs.save(new Song("Go Your Own Way", rumours));
		};
	}
}
