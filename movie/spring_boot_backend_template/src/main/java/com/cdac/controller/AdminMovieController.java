package com.cdac.controller;

import com.cdac.dto.MovieReqDto;
import com.cdac.dto.MovieRespDto;
import com.cdac.service.MovieService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    @Autowired
    private MovieService movieService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MovieRespDto> addMovie(@RequestBody MovieReqDto dto) {
        return ResponseEntity.ok(movieService.addMovie(dto));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateMovie(@PathVariable Long id, @RequestBody MovieReqDto dto) {
        String msg = movieService.updateMovie(id, dto);
        return ResponseEntity.ok(msg);
    }
    
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable("movieId") Long movieId) {
        String msg = movieService.deleteMovie(movieId);
        return ResponseEntity.ok(msg);
    }
    
    @GetMapping
    public ResponseEntity<List<MovieRespDto>> getAllMovies() {
        List<MovieRespDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }
    
    @GetMapping("/id/{movieId}")
    public ResponseEntity<MovieRespDto> getMovieById(@PathVariable Long movieId) {
        MovieRespDto movie = movieService.getMovieById(movieId);
        return ResponseEntity.ok(movie);
    }
}

