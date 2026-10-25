package com.Albumstore.album.controller;

import java.util.Optional;
// Importing necessary classes and packages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.Song;
import com.Albumstore.album.model.SongRepository;

import jakarta.validation.Valid;

@Controller // Marks this class as a Spring MVC controller
public class AlbumController {

    @Autowired
    private AlbumRepository repository; // Injecting AlbumRepository for data operations
    
    @Autowired
    private SongRepository songRepository; // Injecting SongRepository for data operations
    
    @RequestMapping(value="/login") // Mapping for login page
    public String login() {  
        return "login"; // Returns the login view
    }   

    @RequestMapping(value= "/album", method = RequestMethod.GET) // Mapping for displaying all albums
    public String showAlbumList(Model model) {
        model.addAttribute("albums", repository.findAll()); // Adds all albums to the model
        model.addAttribute("songs", songRepository.findAll()); // Adds all songs to the model
        return "album"; // Returns the album view
    }
    
    @GetMapping("/album/{id}/songs") // Mapping to view songs of a specific album
    public String viewAlbumSongs(@PathVariable Long id, Model model) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        model.addAttribute("songs", album.getSongs()); // Adds songs of the specified album to the model
        return "albumsongs"; // Returns the view for album songs
    }
    
    @GetMapping("/album/{id}/song/add") // Mapping for the form to add a new song
    public String addSongForm(@PathVariable Long id, Model model) {
        Song song = new Song();
        model.addAttribute("song", song); // Adds a new song to the model
        model.addAttribute("id", id); // Adds the album id to the model
        return "addsong"; // Returns the view for adding a new song
    }

    // Takes the song name as a plain parameter. With @ModelAttribute Song the
    // "song" field clashed with the attribute name (every add failed with 400),
    // and the {id} path variable was bound as the song id.
    @PostMapping("/album/{id}/song/add") // Mapping for adding a new song to an album
    public String addSong(@PathVariable Long id, @RequestParam("song") String name) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        songRepository.save(new Song(name, album)); // Saves the new song
        return "redirect:/album/" + id + "/songs"; // Redirects to the song list of the album
    }

    @RequestMapping(value="/album/{id}", method = RequestMethod.GET) // Mapping to find an album by ID
    public @ResponseBody Optional<Album> findAlbumById(@PathVariable("id") Long id) {
        return repository.findById(id); // Returns the album with the specified ID
    }

    @GetMapping(value = "/album/add") // Mapping for the form to add a new album
    public String addAlbum(Model model) {
        model.addAttribute("album", new Album()); // Adds a new album to the model
        return "addalbum"; // Returns the view for adding a new album
    }
    
    @PostMapping("/album/save")
    public String save(@Valid @ModelAttribute("album") Album album, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println("Errors: " + result.getAllErrors());
            return "addalbum";
        }
        album.setId(null); // always a new album, a posted id must not overwrite one
        repository.save(album);
        return "redirect:/album";
    }
    
    @GetMapping("/album/edit/{id}") // Mapping for the form to edit an album
    public String editAlbumForm(@PathVariable Long id, Model model) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        model.addAttribute("album", album); // Adds the album to be edited to the model
        return "editalbum"; // Returns the view for editing an album
    }
    
    @PostMapping("/album/update/{id}") // Mapping for updating an album
    public String updateAlbum(@PathVariable Long id, @Valid @ModelAttribute("album") Album album, BindingResult result, Model model) {
        if (result.hasErrors()) {
        	model.addAttribute("id", id);
        	return "editalbum";
        }
        // Copy the form fields onto the saved album. Saving the form object itself
        // replaced the song list with an empty one, and orphanRemoval deleted the songs.
        Album existing = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        existing.setAlbumname(album.getAlbumname());
        existing.setArtist(album.getArtist());
        existing.setGenre(album.getGenre());
        existing.setCoverimage(album.getCoverimage());
        existing.setReleaseyear(album.getReleaseyear());
        repository.save(existing); // Saves the updated album
        return "redirect:/album"; // Redirects to the album list
    }
    
    @PostMapping(value = "/album/delete/{id}") // Mapping for deleting an album
    public String deleteAlbum(@PathVariable Long id) {
        repository.deleteById(id); // Deletes the specified album
        return "redirect:/album"; // Redirects to the album list
    }
}
