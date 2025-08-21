package com.cdac.controller;

import com.cdac.dto.MovieRespDto;
import com.cdac.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@PreAuthorize("hasRole('USER')")
public class MovieUserController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieRespDto>> getAllMoviesForUsers() {
        List<MovieRespDto> movies = movieService.getAllMoviesForUsers();
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MovieRespDto> getMovieById(@PathVariable Long id) {
        MovieRespDto movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieRespDto>> searchMovies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language) {
        List<MovieRespDto> list = movieService.searchMovies(name, genre, language);
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/now-showing")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieRespDto>> getNowShowingMovies() {
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }
    
    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<MovieRespDto>> getUpcomingMovies() {
        return ResponseEntity.ok(movieService.getUpcomingMovies());
    }


}

