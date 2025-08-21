package com.cdac.service;

import java.util.List;

import com.cdac.dto.TheaterReqDto;
import com.cdac.dto.TheaterRespDto;

public interface TheaterService {
	
    TheaterRespDto addTheater(TheaterReqDto dto); //admin add theater
    
    String updateTheater(Long id, TheaterReqDto dto); //admin update theater details
    
    String deleteTheater(Long id); //admin delete theater
    
    List<TheaterRespDto> getAllTheaters(); //admin get all theater list
    
    TheaterRespDto getTheaterById(Long id); //admin get theater details by id
    
    List<TheaterRespDto> getAllTheatersForUser(); //get all theaters for user
    
    TheaterRespDto getTheaterByIdForUser(Long id); // get a theter bu id for user
    
    List<TheaterRespDto> getTheatersByLocation(String location);  //get theater by location by user
    
    List<TheaterRespDto> getTheatersByName(String name); //get theater by name by user by user

}
