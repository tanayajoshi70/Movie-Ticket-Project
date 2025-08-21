package com.cdac.controller;

import com.cdac.dto.TheaterReqDto;
import com.cdac.dto.TheaterRespDto;
import com.cdac.service.TheaterService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/theaters")
public class AdminTheaterController {

    @Autowired
    private TheaterService theaterService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheaterRespDto> addTheater(@Valid @RequestBody TheaterReqDto dto) {
        TheaterRespDto saved = theaterService.addTheater(dto);
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/update/{theaterId}")
    public ResponseEntity<String> updateTheater(@PathVariable Long theaterId, @RequestBody TheaterReqDto dto) {
        return ResponseEntity.ok(theaterService.updateTheater(theaterId, dto));
    }
    
    @DeleteMapping("/delete/{theaterId}")
    public ResponseEntity<String> deleteTheater(@PathVariable Long theaterId) {
        return ResponseEntity.ok(theaterService.deleteTheater(theaterId));
    }
    
    @GetMapping
    public ResponseEntity<List<TheaterRespDto>> getAllTheaters() {
        return ResponseEntity.ok(theaterService.getAllTheaters());
    }
    
    @GetMapping("/{theaterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheaterRespDto> getTheaterById(@PathVariable Long theaterId) {
        TheaterRespDto dto = theaterService.getTheaterById(theaterId);
        return ResponseEntity.ok(dto);
    }
}

