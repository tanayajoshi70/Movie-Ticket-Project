package com.cdac.controller;

import com.cdac.dto.ShowRespDto;
import com.cdac.service.ShowService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/shows")
public class UserShowController {

    @Autowired
    private ShowService showService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{showId}")
    public ShowRespDto getShowDetails(@PathVariable Long showId) {
        return showService.getShowDetails(showId);
    }
    
    @GetMapping("/by-movie")
    public List<ShowRespDto> getShowsByMovieTitle(@RequestParam String title) {
        return showService.getShowsByMovieTitle(title);
    }
    
    @GetMapping("/by-theater")
    public List<ShowRespDto> getShowsByTheaterName(@RequestParam String name) {
        return showService.getShowsByTheaterName(name);
    }
    
    @GetMapping("/by-date")
    @PreAuthorize("hasRole('USER')")
    public List<ShowRespDto> getShowsByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return showService.searchShowsByDate(date);
    }

    @GetMapping("/search/by-start-datetime")
    @PreAuthorize("hasRole('USER')")
    public List<ShowRespDto> searchShowsByStartTime(
            @RequestParam("datetime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime) {
        return showService.searchShowsByStartTime(datetime);
    }
    
   



}
