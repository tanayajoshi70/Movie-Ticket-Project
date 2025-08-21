package com.cdac.service;

import com.cdac.dto.ReceiptDownloadDto;
import com.cdac.entities.Booking;
import com.cdac.repository.BookingRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
@Transactional
@AllArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final BookingRepository bookingRepo;

    @Override
    public void downloadInvoice(Long bookingId, HttpServletResponse response) {
        Booking booking = bookingRepo.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        ReceiptDownloadDto dto = new ReceiptDownloadDto();
        dto.setBookingId(booking.getBookingId());
        dto.setUserName(booking.getUser().getName());
        dto.setMovieTitle(booking.getShow().getMovie().getTitle());
        dto.setTheaterName(booking.getShow().getTheater().getName());
        dto.setShowTime(booking.getShow().getStartTime()
                .format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")));
        dto.setBookedSeats(booking.getBookingSeats().stream()
                .map(bs -> bs.getSeat().getSeatNumber())
                .collect(Collectors.toList()));
        dto.setPaymentMode(booking.getPayment().getPaymentMode());
        dto.setTotalAmount(booking.getPayment().getTotalAmount());
        dto.setBookingTime(booking.getBookingTime());

        generatePdf(dto, response);
    }

    private void generatePdf(ReceiptDownloadDto dto, HttpServletResponse response) {
        try {
            Document document = new Document();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Invoice_Booking_" + dto.getBookingId() + ".pdf");
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Movie Ticket Booking Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Empty line

            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Booking ID: " + dto.getBookingId(), contentFont));
            document.add(new Paragraph("Customer Name: " + dto.getUserName(), contentFont));
            document.add(new Paragraph("Movie Title: " + dto.getMovieTitle(), contentFont));
            document.add(new Paragraph("Theater: " + dto.getTheaterName(), contentFont));
            document.add(new Paragraph("Show Time: " + dto.getShowTime(), contentFont));
            document.add(new Paragraph("Booked Seats: " + String.join(", ", dto.getBookedSeats()), contentFont));
            document.add(new Paragraph("Payment Mode: " + dto.getPaymentMode(), contentFont));
            document.add(new Paragraph("Total Amount: ₹" + dto.getTotalAmount(), contentFont));
            document.add(new Paragraph("Booking Time: " + dto.getBookingTime().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a")), contentFont));

            document.close();
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Error while generating invoice: " + e.getMessage());
        }
    }
}
