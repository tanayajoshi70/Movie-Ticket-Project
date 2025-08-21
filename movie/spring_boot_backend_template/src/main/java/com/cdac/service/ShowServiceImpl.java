package com.cdac.service;

import com.cdac.custom_exception.ResourceNotFoundException;
import com.cdac.dto.ShowReqDto;
import com.cdac.dto.ShowRespDto;
import com.cdac.entities.Movie;
import com.cdac.entities.Show;
import com.cdac.entities.Theater;
import com.cdac.repository.MovieRepository;
import com.cdac.repository.ShowRepository;
import com.cdac.repository.TheaterRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepo;
    private final MovieRepository movieRepo;
    private final TheaterRepository theaterRepo;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public ShowRespDto addShow(ShowReqDto dto) {
        Movie movie = movieRepo.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        Theater theater = theaterRepo.findById(dto.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setStartTime(dto.getStartTime());
        show.setEndTime(dto.getEndTime());
        show.setPricePerSeat(dto.getPricePerSeat());
        show.setTitle(movie.getTitle() + " - " + show.getStartTime().toLocalTime());
        show.setTitle(dto.getTitle()); 

        Show saved = showRepo.save(show);

        ShowRespDto resp = mapper.map(saved, ShowRespDto.class);
        resp.setMovieTitle(saved.getMovie().getTitle());
        resp.setTheaterName(saved.getTheater().getName());
        resp.setTheaterLocation(saved.getTheater().getLocation());

        return resp;
    }
    
    @Override
    @Transactional
    public ShowRespDto updateShow(Long showId, ShowReqDto dto) {
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        Movie movie = movieRepo.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Theater theater = theaterRepo.findById(dto.getTheaterId())
                .orElseThrow(() -> new RuntimeException("Theater not found"));

        show.setMovie(movie);
        show.setTheater(theater);
        show.setStartTime(dto.getStartTime());
        show.setEndTime(dto.getEndTime());
        show.setPricePerSeat(dto.getPricePerSeat());
        show.setTitle(movie.getTitle() + " - " + show.getStartTime().toLocalTime());

        Show updated = showRepo.save(show);

        ShowRespDto resp = mapper.map(updated, ShowRespDto.class);
        resp.setMovieTitle(movie.getTitle());
        resp.setTheaterName(theater.getName());
        resp.setTheaterLocation(theater.getLocation());

        return resp;
    }
    
    @Override
    @Transactional
    public String deleteShow(Long showId) {
        if (!showRepo.existsById(showId)) {
            throw new RuntimeException("Show not found with ID: " + showId);
        }
        showRepo.deleteById(showId);
        return "Show deleted successfully";
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShowRespDto> getAllShows() {
        List<Show> shows = showRepo.findAll();
        return shows.stream().map(show -> {
            ShowRespDto dto = mapper.map(show, ShowRespDto.class);
            dto.setMovieTitle(show.getMovie().getTitle());
            dto.setTheaterName(show.getTheater().getName());
            dto.setTheaterLocation(show.getTheater().getLocation());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ShowRespDto getShowById(Long showId) {
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        ShowRespDto dto = mapper.map(show, ShowRespDto.class);
        dto.setMovieTitle(show.getMovie().getTitle());
        dto.setTheaterName(show.getTheater().getName());
        dto.setTheaterLocation(show.getTheater().getLocation());
        

        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShowRespDto> getShowsByTheater(Long theaterId) {
        List<Show> shows = showRepo.findByTheaterTheaterId(theaterId);

        return shows.stream().map(show -> {
            ShowRespDto dto = mapper.map(show, ShowRespDto.class);
            dto.setMovieTitle(show.getMovie().getTitle());
            dto.setTheaterName(show.getTheater().getName());
            dto.setTheaterLocation(show.getTheater().getLocation());
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<ShowRespDto> getShowsByMovie(Long movieId) {
        List<Show> shows = showRepo.findByMovieMovieId(movieId);
        
        return shows.stream().map(show -> {
            ShowRespDto dto = mapper.map(show, ShowRespDto.class);
            dto.setMovieTitle(show.getMovie().getTitle());
            dto.setTheaterName(show.getTheater().getName());
            return dto;
        }).toList();
    }
    
    @Override
    public ShowRespDto getShowDetails(Long showId) {
        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + showId));

        ShowRespDto dto = mapper.map(show, ShowRespDto.class);
        dto.setMovieTitle(show.getMovie().getTitle());
        dto.setTheaterName(show.getTheater().getName());
        dto.setTheaterLocation(show.getTheater().getLocation());
        return dto;
    }
    
    @Override
    public List<ShowRespDto> getShowsByMovieTitle(String title) {
        List<Show> shows = showRepo.findByMovieTitleIgnoreCase(title);

        return shows.stream()
            .map(show -> {
                ShowRespDto dto = mapper.map(show, ShowRespDto.class);
                dto.setMovieTitle(show.getMovie().getTitle());
                dto.setTheaterName(show.getTheater().getName());
                return dto;
            }).collect(Collectors.toList());
    }
    
    @Override
    public List<ShowRespDto> getShowsByTheaterName(String theaterName) {
        List<Show> shows = showRepo.findByTheaterNameIgnoreCase(theaterName);

        return shows.stream()
            .map(show -> {
                ShowRespDto dto = mapper.map(show, ShowRespDto.class);
                dto.setMovieTitle(show.getMovie().getTitle());
                dto.setTheaterName(show.getTheater().getName());
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ShowRespDto> searchShowsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Show> shows = showRepo.findByStartTimeBetween(startOfDay, endOfDay);

        return shows.stream()
            .map(show -> {
                ShowRespDto dto = mapper.map(show, ShowRespDto.class);
                dto.setMovieTitle(show.getMovie().getTitle());
                dto.setTheaterName(show.getTheater().getName());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ShowRespDto> searchShowsByStartTime(LocalDateTime datetime) {
        List<Show> shows = showRepo.findByStartTime(datetime);
        return shows.stream()
                .map(show -> {
                    ShowRespDto dto = mapper.map(show, ShowRespDto.class);
                    dto.setMovieTitle(show.getMovie().getTitle());
                    dto.setTheaterName(show.getTheater().getName());
                    dto.setTheaterLocation(show.getTheater().getLocation());

                    return dto;
                })
                .collect(Collectors.toList());
    }

}

