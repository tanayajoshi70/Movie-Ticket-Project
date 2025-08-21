package com.cdac.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.cdac.dto.ShowReqDto;
import com.cdac.dto.ShowRespDto;

public interface ShowService {
    ShowRespDto addShow(ShowReqDto dto); // add show by admin 
    
    ShowRespDto updateShow(Long showId, ShowReqDto dto); // update show by admin 
    
    String deleteShow(Long showId); // delete show by admin 
    
    List<ShowRespDto> getAllShows(); // get all  show by admin 
    
    ShowRespDto getShowById(Long showId); // get show by id by admin 
    
    List<ShowRespDto> getShowsByTheater(Long theaterId);  // get show by theater id by admin 
    
    List<ShowRespDto> getShowsByMovie(Long movieId); // get show by movie id by admin 
    
    ShowRespDto getShowDetails(Long showId); //get  all shows for user
    
    List<ShowRespDto> getShowsByMovieTitle(String title);  //get show by movies title for user
     
    List<ShowRespDto> getShowsByTheaterName(String theaterName); // get show by theater name by user
    
    List<ShowRespDto> searchShowsByDate(LocalDate date); //get show by date
    
    List<ShowRespDto> searchShowsByStartTime(LocalDateTime datetime); //get show by start time


    
}

