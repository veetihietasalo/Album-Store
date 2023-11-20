package com.Albumstore.album.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Album {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Size(min=1, max=100)
	@NotBlank(message = "Album name is required!")
	private String albumname;
	
	@Size(min=1, max=100)
	@NotBlank(message = "Artist is required!")
	private String artist;
	
	@Size(min=1, max=50)
	@NotBlank(message = "Genre is required")
	private String genre;
	
	private double coverimage;
	
	@Min(value = 1900, message = "Release year must be after 1900")
	private int releaseyear;
	
	public Album() {
		
	}
	
	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
    private List<Song> songs = new ArrayList<>();

	public Album(String albumname, String artist, String genre, double coverimage, int releaseyear) {
		super();
		this.albumname = albumname;
		this.artist = artist;
		this.genre = genre;
		this.coverimage = coverimage;
		this.releaseyear = releaseyear;
	}
	
	public String getAlbumname() {
		return albumname;
	}


	public void setAlbumname(String albumname) {
		this.albumname = albumname;
	}


	public String getArtist() {
		return artist;
	}


	public void setArtist(String artist) {
		this.artist = artist;
	}


	public String getGenre() {
		return genre;
	}


	public void setGenre(String genre) {
		this.genre = genre;
	}


	public double getCoverimage() {
		return coverimage;
	}


	public void setCoverimage(double coverimage) {
		this.coverimage = coverimage;
	}


	public int getReleaseyear() {
		return releaseyear;
	}


	public void setReleaseyear(int releaseyear) {
		this.releaseyear = releaseyear;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Album [id=" + id + ", albumname=" + albumname + ", artist=" + artist + ", genre=" + genre + ", coverimage=" + coverimage
				+ ", releaseyear=" + releaseyear + "]";
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}




	



	
	
	
	
	

}
