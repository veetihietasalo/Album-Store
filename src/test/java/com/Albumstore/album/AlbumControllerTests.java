package com.Albumstore.album;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.Song;
import com.Albumstore.album.model.SongRepository;
import com.Albumstore.album.model.User;
import com.Albumstore.album.model.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlbumControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private SongRepository songRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JdbcTemplate jdbc;

	private Long albumId;

	@BeforeEach
	void setUp() {
		if (userRepository.findByUsername("admin") == null) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			userRepository.save(new User("user", encoder.encode("userpass"), "USER"));
			userRepository.save(new User("admin", encoder.encode("adminpass"), "ADMIN"));
		}
		albumRepository.deleteAll();
		Album album = albumRepository.save(new Album("Old name", "Artist", "Rock", 9.9, 2001));
		albumId = album.getId();
		songRepository.save(new Song("First", album));
		songRepository.save(new Song("Second", album));
	}

	private int songCount(Long id) {
		return jdbc.queryForObject("select count(*) from song where album_id = ?", Integer.class, id);
	}

	@Test
	void addAndEditPagesRender() throws Exception {
		mockMvc.perform(get("/album/add").with(user("admin").authorities(() -> "ADMIN")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Add Album to the list")));
		mockMvc.perform(get("/album/edit/" + albumId).with(user("admin").authorities(() -> "ADMIN")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("value=\"Old name\"")));
	}

	@Test
	void editingAnAlbumKeepsItsSongs() throws Exception {
		mockMvc.perform(post("/album/update/" + albumId).with(user("admin").authorities(() -> "ADMIN")).with(csrf())
				.param("albumname", "New name").param("artist", "Artist").param("genre", "Rock")
				.param("coverimage", "9.9").param("releaseyear", "2001"))
				.andExpect(redirectedUrl("/album"));
		assertThat(albumRepository.findById(albumId).get().getAlbumname()).isEqualTo("New name");
		assertThat(songCount(albumId)).isEqualTo(2);
	}

	@Test
	void invalidEditShowsTheFormAgain() throws Exception {
		mockMvc.perform(post("/album/update/" + albumId).with(user("admin").authorities(() -> "ADMIN")).with(csrf())
				.param("albumname", "").param("artist", "Artist").param("genre", "Rock")
				.param("coverimage", "9.9").param("releaseyear", "1800"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Album name is required!")))
				.andExpect(content().string(containsString("action=\"/album/update/" + albumId + "\"")));
		assertThat(songCount(albumId)).isEqualTo(2);
	}

	@Test
	void addingASongWorks() throws Exception {
		mockMvc.perform(post("/album/" + albumId + "/song/add").with(user("admin").authorities(() -> "ADMIN")).with(csrf())
				.param("song", "Third"))
				.andExpect(redirectedUrl("/album/" + albumId + "/songs"));
		assertThat(songCount(albumId)).isEqualTo(3);
		mockMvc.perform(get("/album/" + albumId + "/songs"))
				.andExpect(content().string(containsString("Third")))
				.andExpect(content().string(containsString("First")));
	}

	@Test
	void restPostWithAnIdCreatesANewAlbum() throws Exception {
		mockMvc.perform(post("/api/albums").with(httpBasic("admin", "adminpass"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":" + albumId + ",\"albumname\":\"Other\",\"artist\":\"A\",\"genre\":\"Pop\",\"coverimage\":1,\"releaseyear\":2000}"))
				.andExpect(status().isCreated());
		assertThat(albumRepository.findById(albumId).get().getAlbumname()).isEqualTo("Old name");
		assertThat(songCount(albumId)).isEqualTo(2);
		assertThat(albumRepository.count()).isEqualTo(2);
	}

}
