package com.cdac.service;

import com.cdac.dto.SeatAvailableRespDto;
import com.cdac.dto.SeatReqDto;
import com.cdac.dto.SeatRespDto;
import com.cdac.dto.SeatUpdateDto;

import com.cdac.dto.SeatUserRespDto;
import com.cdac.entities.Seat;
import com.cdac.entities.Show;
import com.cdac.custom_exception.*;
import com.cdac.repository.SeatRepository;
import com.cdac.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<SeatRespDto> addSeats(SeatReqDto dto) {
        Show show = showRepository.findById(dto.getShowId())
            .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> seats = dto.getSeats().stream()
            .map(s -> {
                Seat seat = new Seat();
                seat.setSeatNo(s.getSeatNo());
                seat.setPrice(s.getPrice());
                seat.setBooked(s.isBooked()); // set booking status
                seat.setShow(show);
                return seat;
            }).toList();

        List<Seat> saved = seatRepository.saveAll(seats);
        return saved.stream()
                .map(seat -> {
                    SeatRespDto resp = modelMapper.map(seat, SeatRespDto.class);
                    resp.setShowId(seat.getShow().getShowId());
                    return resp;
                }).collect(Collectors.toList());
    }
    
    @Override
    public SeatRespDto updateSeat(Long seatId, SeatUpdateDto dto) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        if (dto.getPrice() != null) {
            seat.setPrice(dto.getPrice());
        }
        if (dto.getBooked() != null) {
            seat.setBooked(dto.getBooked());
        }

        Seat updated = seatRepository.save(seat);
        SeatRespDto resp = modelMapper.map(updated, SeatRespDto.class);
        resp.setShowId(seat.getShow().getShowId());
        return resp;
    }
    
    @Override
    public void deleteSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
            .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        seatRepository.delete(seat);
    }
    
    @Override
    public List getSeatsByShowId(Long showId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> seats = seatRepository.findByShowShowId(showId);

        return seats.stream()
                .map(seat -> new SeatAvailableRespDto(
                        seat.getId(),
                        seat.getSeatNo(),
                        seat.isBooked(),
                        seat.getPrice()))
                .toList();
    }
    
    @Override
    public List getAvailableSeatsByShowId(Long showId) {
        // Optional: Validate show exists
        showRepository.findById(showId)
            .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<Seat> availableSeats = seatRepository.findByShowShowIdAndIsBookedFalse(showId);

        return availableSeats.stream()
                .map(seat -> new SeatUserRespDto(
                        seat.getId(),
                        seat.getSeatNo(),
                        seat.isBooked(),
                        seat.getPrice()))
                .toList();
    }



}
