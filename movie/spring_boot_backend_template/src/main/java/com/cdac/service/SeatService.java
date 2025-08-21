package com.cdac.service;

import com.cdac.dto.SeatReqDto;
import com.cdac.dto.SeatRespDto;
import com.cdac.dto.SeatUpdateDto;
import com.cdac.dto.SeatUserRespDto;

import java.util.List;

public interface SeatService {
    List<SeatRespDto> addSeats(SeatReqDto dto); //add seats by admin
    
    SeatRespDto updateSeat(Long seatId, SeatUpdateDto dto); //update seats by admin
    
    void deleteSeat(Long seatId); //delete seats by admin
    
    List<SeatRespDto> getSeatsByShowId(Long showId); //get seats by show id by admin
    
    List<SeatUserRespDto> getAvailableSeatsByShowId(Long showId); //get seats by user
    
    
}
