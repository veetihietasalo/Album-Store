package com.Albumstore.album.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Album {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String albumname, artist, genre;
	private double coverimage;
	private int releaseyear;
	
	public Album() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Album(String albumname, String artist, String genre, double coverimage, int releaseyear) {
		super();
		this.albumname = albumname;
		this.artist = artist;
		this.genre = genre;
		this.coverimage = coverimage;
		this.releaseyear = releaseyear;
	}
	
	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();
	
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
