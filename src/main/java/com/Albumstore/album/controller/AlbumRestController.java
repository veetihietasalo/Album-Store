package com.Albumstore.album.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Albumstore.album.model.Album;
import com.Albumstore.album.model.AlbumRepository;
import com.Albumstore.album.model.SongRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/albums")
public class AlbumRestController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    // GET all albums and songs
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlbumsAndSongs() {
        Map<String, Object> response = new HashMap<>();
        response.put("albums", albumRepository.findAll());
        response.put("songs", songRepository.findAll());
        return ResponseEntity.ok(response);
    }

    // POST - Add a new album
    @PostMapping
    public ResponseEntity<Album> addAlbum(@Valid @RequestBody Album album) {
    	System.out.println("Received album: " + album);
        Album savedAlbum = albumRepository.save(album);
        return new ResponseEntity<>(savedAlbum, HttpStatus.CREATED);
    }

    // PUT - Update an existing album
    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable Long id, @Valid @RequestBody Album albumDetails) {
        return albumRepository.findById(id)
            .map(existingAlbum -> {
                existingAlbum.setAlbumname(albumDetails.getAlbumname());
                existingAlbum.setArtist(albumDetails.getArtist());
                existingAlbum.setGenre(albumDetails.getGenre());
                existingAlbum.setCoverimage(albumDetails.getCoverimage());
                existingAlbum.setReleaseyear(albumDetails.getReleaseyear());
                // Add other necessary fields here

                Album updatedAlbum = albumRepository.save(existingAlbum);
                return ResponseEntity.ok(updatedAlbum);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // DELETE - Delete an album
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlbum(@PathVariable Long id) {
        return albumRepository.findById(id)
            .map(album -> {
                albumRepository.delete(album);
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
