CREATE TABLE albums
(id BIGINT NOT NULL AUTO_INCREMENT 
, albumname VARCHAR(100) NOT NULL
, artist VARCHAR(100) NOT NULL
, genre VARCHAR(50) W
, coverimage VARCHAR(15) NOT NULL
, releaseyear BIGINT
,PRIMARY KEY (id)
);

INSERT INTO albums (albumname, artist, genre, coverimage, releaseyear) 
VALUES ("Maria", "Marison", "Manse", "170", 1974)
, ("Minnie", "Minison", "Turku", "171",1970);

SELECT * FROM albums;