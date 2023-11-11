package com.Albumstore.album.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.Song;
import com.Albumstore.album.model.SongRepository;


@Controller
public class AlbumController {

    @Autowired
    private AlbumRepository repository;
    
    @Autowired
    private SongRepository songRepository;
    
    @RequestMapping(value="/login")
    public String login() {	
        return "login";
    }	

    @RequestMapping(value= "/album", method = RequestMethod.GET)
    public String showAlbumList(Model model) {
        model.addAttribute("albums", repository.findAll());
        model.addAttribute("songs", songRepository.findAll());
        return "album";
    }
    
    @GetMapping("/album/{id}/songs")
    public String viewAlbumSongs(@PathVariable Long id, Model model) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        model.addAttribute("songs", album.getSongs());
        return "albumsongs";
        
    }
    
    @GetMapping("/album/{id}/song/add")
    public String addSongForm(@PathVariable Long id, Model model) {
        Song song = new Song();
        model.addAttribute("song", song);
        model.addAttribute("id", id);
        return "addsong"; // Thymeleaf template for adding a song
    }

    
    @PostMapping("/album/{id}/song/add")
    public String addSong(@PathVariable Long id, @ModelAttribute Song song) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        song.setAlbum(album);
        songRepository.save(song);
        return "redirect:/album/" + id + "/songs"; // Redirect to the song list of the album
    }


    @RequestMapping(value="/album/{id}", method = RequestMethod.GET)
    public @ResponseBody Optional<Album> findAlbumById(@PathVariable("id") Long id) {
    	return repository.findById(id);
    }

    @GetMapping(value = "/album/add")
    public String addAlbum(Model model) {
        model.addAttribute("album", new Album());
        return "addalbum";
    }
    
    @RequestMapping(value = "/album/save", method = RequestMethod.POST)
    public String save(@ModelAttribute Album album) {
        repository.save(album);
        return "redirect:/album";
    }
    
    @GetMapping("/album/edit/{id}")
    public String editAlbumForm(@PathVariable Long id, Model model) {
        Album album = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid album Id:" + id));
        model.addAttribute("album", album);
        return "editalbum";
    }
    
    @PostMapping("/album/update/{id}")
    public String updateAlbum(@PathVariable Long id, @ModelAttribute Album album) {
        album.setId(id);
        repository.save(album);
        return "redirect:/album";
    }
    
    @PostMapping(value = "/album/delete/{id}")
    public String deleteAlbum(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/album";
    }

    
    
    
}
