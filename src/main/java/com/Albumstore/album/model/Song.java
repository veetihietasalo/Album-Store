package com.Albumstore.album.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Song {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String song;
	
	
	public Song(Long id, String song, Album album) {
		super();
		this.id = id;
		this.song = song;
		this.album = album;
	}

	public Song() {
		super();
		// TODO Auto-generated constructor stub
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	private Album album;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@Override
	public String toString() {
		return "Song [id=" + id + ", song=" + song + ", album=" + album + "]";
	}
	
}
