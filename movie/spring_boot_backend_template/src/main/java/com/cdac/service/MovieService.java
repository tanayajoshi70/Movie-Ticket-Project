package com.cdac.service;

import java.util.List;

import com.cdac.dto.MovieReqDto;
import com.cdac.dto.MovieRespDto;

public interface MovieService {
    MovieRespDto addMovie(MovieReqDto dto); //admin add movie
    
    String updateMovie(Long movieId, MovieReqDto dto); //admin update movie
    
    String deleteMovie(Long movieId); //admin delete movie
    
    List<MovieRespDto> getAllMovies(); //admin get all movies
    
    MovieRespDto getMovieById(Long movieId); //admin get a movie by id
    
    
	List<MovieRespDto> getAllMoviesForUsers(); //user get all movies
	
	MovieRespDto getMovieByIdForUser(Long movieId); //user get a movie by id - details of one movie
	
	List<MovieRespDto> searchMovies(String title, String genre, String language); 
	//user search a movie by name or genre or language
	
	List<MovieRespDto> getNowShowingMovies(); // user - now showing
	
	List<MovieRespDto> getUpcomingMovies(); // user - get upcoming movies
}

