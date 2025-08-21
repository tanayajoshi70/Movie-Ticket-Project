package com.cdac.controller;

import com.cdac.dto.TheaterRespDto;
import com.cdac.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class UserTheaterController {

    @Autowired
    private TheaterService theaterService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TheaterRespDto>> getAllTheatersForUser() {
        return ResponseEntity.ok(theaterService.getAllTheatersForUser());
    }
    
    @GetMapping("/{theaterId}")
    public ResponseEntity<TheaterRespDto> getTheaterById(@PathVariable Long theaterId) {
        TheaterRespDto dto = theaterService.getTheaterByIdForUser(theaterId);
        return ResponseEntity.ok(dto);
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search")
    public ResponseEntity<List<TheaterRespDto>> getTheatersByLocation(@RequestParam String location) {
        List<TheaterRespDto> theaters = theaterService.getTheatersByLocation(location);
        return ResponseEntity.ok(theaters);
    }
    
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/search-by-name")
    public ResponseEntity<List<TheaterRespDto>> getTheatersByName(@RequestParam String name) {
        List<TheaterRespDto> result = theaterService.getTheatersByName(name);
        return ResponseEntity.ok(result);
    }

}

