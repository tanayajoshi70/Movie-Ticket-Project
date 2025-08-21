package com.cdac.service;

import com.cdac.dto.MovieReqDto;
import com.cdac.dto.MovieRespDto;
import com.cdac.entities.Movie;
import com.cdac.repository.MovieRepository;
import com.cdac.service.MovieService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MovieRespDto addMovie(MovieReqDto dto) {
        Movie movie = modelMapper.map(dto, Movie.class);
        movie = movieRepo.save(movie);
        return modelMapper.map(movie, MovieRespDto.class);
    }
    
    @Override
    public String updateMovie(Long movieId, MovieReqDto dto) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        modelMapper.map(dto, movie); // this will update only matching fields

        movieRepo.save(movie);

        return "Movie updated successfully.";
    }
    
    @Override
    public String deleteMovie(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));
        movieRepo.delete(movie);
        return "Movie deleted successfully.";
    }
    
    @Override
    public List<MovieRespDto> getAllMovies() {
        List<Movie> movies = movieRepo.findAll();
        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieRespDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public MovieRespDto getMovieById(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        return modelMapper.map(movie, MovieRespDto.class);
    }
    
    @Override
    public List<MovieRespDto> getAllMoviesForUsers() {
        List<Movie> movies = movieRepo.findAll(); // ← make sure this is not filtered
        return movies.stream()
                     .map(movie -> modelMapper.map(movie, MovieRespDto.class))
                     .toList();
    }
    
    @Override
    public MovieRespDto getMovieByIdForUser(Long movieId) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + movieId));

        return modelMapper.map(movie, MovieRespDto.class);
    }
    
    @Override
    public List<MovieRespDto> searchMovies(String title, String genre, String language) {
        List<Movie> movies = movieRepo.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCaseAndLanguageContainingIgnoreCase(
                title == null ? "" : title,
                genre == null ? "" : genre,
                language == null ? "" : language
        );

        return movies.stream()
                .map(movie -> modelMapper.map(movie, MovieRespDto.class))
                .toList();
    }
    
    @Override
    public List<MovieRespDto> getNowShowingMovies() {
        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(30);
        List<Movie> movies = movieRepo.findByReleaseDateBetween(fromDate, today);
        return movies.stream()
                .map(m -> modelMapper.map(m, MovieRespDto.class))
                .toList();
    }
    
    @Override
    public List<MovieRespDto> getUpcomingMovies() {
        LocalDate today = LocalDate.now();
        List<Movie> movies = movieRepo.findByReleaseDateAfter(today);
        return movies.stream()
                .map(m -> modelMapper.map(m, MovieRespDto.class))
                .toList();
    }


}
