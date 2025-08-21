package com.cdac.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdac.custom_exception.ResourceNotFoundException;
import com.cdac.dto.TheaterReqDto;
import com.cdac.dto.TheaterRespDto;
import com.cdac.entities.Theater;
import com.cdac.repository.TheaterRepository;

@Service
public class TheaterServiceImpl implements TheaterService {

    @Autowired
    private TheaterRepository theaterRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TheaterRespDto addTheater(TheaterReqDto dto) {
        Theater t = modelMapper.map(dto, Theater.class);
        Theater saved = theaterRepo.save(t);
        return modelMapper.map(saved, TheaterRespDto.class);
    }
    
    @Override
    public String updateTheater(Long id, TheaterReqDto dto) {
        Theater theater = theaterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with ID: " + id));

        theater.setName(dto.getName());
        theater.setLocation(dto.getLocation());
        theater.setTotalSeats(dto.getTotalSeats());

        theaterRepo.save(theater);
        return "Theater updated successfully.";
    }
    
    @Override
    public String deleteTheater(Long id) {
        Theater t = theaterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater not found with ID: " + id));
        theaterRepo.delete(t);
        return "Theater deleted successfully.";
    }
    
    @Override
    public List<TheaterRespDto> getAllTheaters() {
        List<Theater> list = theaterRepo.findAll();
        return list.stream()
                .map(t -> modelMapper.map(t, TheaterRespDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public TheaterRespDto getTheaterById(Long id) {
        Theater t = theaterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater not found with ID: " + id));
        return modelMapper.map(t, TheaterRespDto.class);
    }
    
    @Override
    public List<TheaterRespDto> getAllTheatersForUser() {
        List<Theater> theaters = theaterRepo.findAll();
        return theaters.stream()
                .map(t -> modelMapper.map(t, TheaterRespDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public TheaterRespDto getTheaterByIdForUser(Long id) {
        Theater theater = theaterRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Theater not found with ID: " + id));
        return modelMapper.map(theater, TheaterRespDto.class);
    }
    
    @Override
    public List<TheaterRespDto> getTheatersByLocation(String location) {
        List<Theater> list = theaterRepo.findByLocationIgnoreCase(location);
        return list.stream()
                .map(t -> modelMapper.map(t, TheaterRespDto.class))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TheaterRespDto> getTheatersByName(String name) {
        List<Theater> theaters = theaterRepo.findByNameContainingIgnoreCase(name);
        return theaters.stream()
                .map(t -> modelMapper.map(t, TheaterRespDto.class))
                .collect(Collectors.toList());
    }

}
