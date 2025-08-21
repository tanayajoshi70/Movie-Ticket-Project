package com.cdac.controller;

import com.cdac.dto.ShowReqDto;
import com.cdac.dto.ShowRespDto;
import com.cdac.service.ShowService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shows")
@AllArgsConstructor
public class AdminShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowRespDto> addShow(@Valid @RequestBody ShowReqDto dto) {
        return ResponseEntity.ok(showService.addShow(dto));
    }
    
    @PutMapping("/update/{showId}")
    public ResponseEntity<ShowRespDto> updateShow(@PathVariable("showId") Long showId,
                                                  @RequestBody ShowReqDto dto) {
        ShowRespDto updated = showService.updateShow(showId, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/delete/{showId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteShow(@PathVariable Long showId) {
        String result = showService.deleteShow(showId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowRespDto>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }
    
    @GetMapping("/{showId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowRespDto> getShowById(@PathVariable Long showId) {
        return ResponseEntity.ok(showService.getShowById(showId));
    }
    
    @GetMapping("/theater/{theaterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShowRespDto>> getShowsByTheater(@PathVariable Long theaterId) {
        return ResponseEntity.ok(showService.getShowsByTheater(theaterId));
    }
    
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowRespDto>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }
}
