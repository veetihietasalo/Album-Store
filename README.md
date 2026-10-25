# Album Store

Spring Boot web app for browsing music albums and their songs. An admin logs in to add, edit and delete albums and add songs.

What it does:
- Album list (name, artist, genre, price, release year) and a song list per album
- Form login with two roles, USER and ADMIN. Only ADMIN gets the add, edit and delete buttons and pages
- Validation on the album form: name, artist and genre required, release year 1900 or later
- REST API at `/api/albums`: GET (albums and songs), POST, PUT `/{id}`, DELETE `/{id}`

Stack: Java 17, Spring Boot 3.1.5, Thymeleaf, Spring Data JPA, Spring Security (BCrypt), Bean Validation, MariaDB, Maven wrapper.

Run:
1. Start MariaDB on localhost:3306 and run `albumscript.sql`. It creates the `albums` database and the tables. Hibernate (`ddl-auto=update`) updates the tables after that.
2. Set `spring.datasource.username` and `spring.datasource.password` in `src/main/resources/application.properties` to your own MariaDB user.
3. Start the app with `sh mvnw spring-boot:run` (macOS / Linux) or `mvnw.cmd spring-boot:run` (Windows) and open http://localhost:8080/login

Accounts: `user` / `user` (USER) and `admin` / `admin` (ADMIN). The SQL script doesn't insert them. The code that does is commented out in `AlbumApplication.java`, so uncomment those 4 lines for the first run.

Known gaps:
- The album and song lists open without login, and `/api/**` is open with CSRF off, so the USER/ADMIN split only covers the web pages
- The controller asks for the views `addalbum` and `editalbum` but the files are `addAlbum.html` and `editAlbum.html`, so those two pages only load on a case-insensitive file system like Windows

Status: back-end course project, built 10 to 20 November 2023.
