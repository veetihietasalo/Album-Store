package com.Albumstore.album;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.User;
import com.Albumstore.album.model.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTests {

	private static final String ALBUM_JSON = "{\"albumname\":\"Test\",\"artist\":\"Tester\",\"genre\":\"Rock\",\"coverimage\":9.9,\"releaseyear\":2001}";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AlbumRepository albumRepository;

	private Long albumId;

	@BeforeEach
	void setUp() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (userRepository.findByUsername("user") == null) {
			userRepository.save(new User("user", encoder.encode("userpass"), "USER"));
			userRepository.save(new User("admin", encoder.encode("adminpass"), "ADMIN"));
		}
		albumRepository.deleteAll();
		albumId = albumRepository.save(new Album("Test", "Tester", "Rock", 9.9, 2001)).getId();
	}

	// REST API

	@Test
	void apiNeedsLogin() throws Exception {
		mockMvc.perform(get("/api/albums")).andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api")).andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/songs")).andExpect(status().isUnauthorized());
		mockMvc.perform(post("/api/albums").contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/api/albums/" + albumId)).andExpect(status().isUnauthorized());
	}

	@Test
	void anyUserCanReadTheApi() throws Exception {
		mockMvc.perform(get("/api/albums").with(httpBasic("user", "userpass")))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"albumname\":\"Test\"")));
	}

	@Test
	void wrongPasswordIsRejected() throws Exception {
		mockMvc.perform(get("/api/albums").with(httpBasic("admin", "wrong")))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/albums").with(httpBasic("nobody", "x")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void apiWritesNeedAdmin() throws Exception {
		mockMvc.perform(post("/api/albums").with(httpBasic("user", "userpass"))
				.contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
				.andExpect(status().isForbidden());
		mockMvc.perform(delete("/api/albums/" + albumId).with(httpBasic("user", "userpass")))
				.andExpect(status().isForbidden());
		mockMvc.perform(post("/api/songs").with(httpBasic("user", "userpass"))
				.contentType(MediaType.APPLICATION_JSON).content("{\"song\":\"x\"}"))
				.andExpect(status().isForbidden());

		// ADMIN can write, no CSRF token needed on the stateless API
		mockMvc.perform(post("/api/albums").with(httpBasic("admin", "adminpass"))
				.contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
				.andExpect(status().isCreated());
		mockMvc.perform(delete("/api/albums/" + albumId).with(httpBasic("admin", "adminpass")))
				.andExpect(status().isOk());
	}

	@Test
	void usersAreNotExported() throws Exception {
		mockMvc.perform(get("/api/users").with(httpBasic("admin", "adminpass")))
				.andExpect(status().isNotFound());
		mockMvc.perform(post("/api/users").with(httpBasic("admin", "adminpass"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"x\",\"passwordHash\":\"x\",\"role\":\"ADMIN\"}"))
				.andExpect(status().isNotFound());
		mockMvc.perform(get("/api").with(httpBasic("admin", "adminpass")))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("/api/users"))))
				.andExpect(content().string(not(containsString("$2a$"))));
	}

	@Test
	void apiIgnoresTheLoginSession() throws Exception {
		MockHttpSession session = (MockHttpSession) mockMvc
				.perform(formLogin().user("admin").password("adminpass"))
				.andExpect(redirectedUrl("/album"))
				.andReturn().getRequest().getSession(false);
		// The session works for the web pages...
		mockMvc.perform(get("/album/add").session(session)).andExpect(status().isOk());
		// ...but not for the API, so a cross-site request can't use the cookie
		mockMvc.perform(get("/api/albums").session(session)).andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/api/albums/" + albumId).session(session)).andExpect(status().isUnauthorized());
	}

	// Web pages

	@Test
	void albumListsAreOpenForBrowsing() throws Exception {
		mockMvc.perform(get("/album"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Sign In")))
				.andExpect(content().string(not(containsString("Sign Out"))))
				.andExpect(content().string(not(containsString("Add New Album"))));
		mockMvc.perform(get("/album/" + albumId + "/songs")).andExpect(status().isOk());
	}

	@Test
	void adminPagesNeedAdmin() throws Exception {
		mockMvc.perform(get("/album/add")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
		// HTTP Basic is only for the API, the web pages ignore it
		mockMvc.perform(get("/album/add").with(httpBasic("admin", "adminpass"))).andExpect(status().is3xxRedirection());

		MockHttpSession userSession = (MockHttpSession) mockMvc
				.perform(formLogin().user("user").password("userpass"))
				.andReturn().getRequest().getSession(false);
		mockMvc.perform(get("/album/add").session(userSession)).andExpect(status().isForbidden());
		mockMvc.perform(get("/album/edit/" + albumId).session(userSession)).andExpect(status().isForbidden());
		mockMvc.perform(post("/album/delete/" + albumId).session(userSession).with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	void webFormsNeedCsrfToken() throws Exception {
		MockHttpSession session = (MockHttpSession) mockMvc
				.perform(formLogin().user("admin").password("adminpass"))
				.andReturn().getRequest().getSession(false);
		mockMvc.perform(post("/album/delete/" + albumId).session(session)).andExpect(status().isForbidden());
		mockMvc.perform(post("/album/delete/" + albumId).session(session).with(csrf()))
				.andExpect(redirectedUrl("/album"));
	}

	@Test
	void customLoginPageAndFrameOptions() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("login.css")))
				.andExpect(header().string("X-Frame-Options", "DENY"));
	}

	@Test
	void unknownUserCannotLogIn() throws Exception {
		mockMvc.perform(formLogin().user("nobody").password("x"))
				.andExpect(redirectedUrl("/login?error"));
	}

}
