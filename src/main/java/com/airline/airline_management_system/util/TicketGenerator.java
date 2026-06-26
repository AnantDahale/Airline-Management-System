package com.airline.airline_management_system.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class TicketGenerator {

    public byte[] generateTicketPdf(String pnr, String passengerName, String flightNumber,
                                    String source, String destination,
                                    String departureTime, String seatNumber) throws IOException, WriterException, DocumentException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

        document.add(new Paragraph("Airline E-Ticket", titleFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        addRow(table, "PNR", pnr, normalFont);
        addRow(table, "Passenger Name", passengerName, normalFont);
        addRow(table, "Flight Number", flightNumber, normalFont);
        addRow(table, "Route", source + " → " + destination, normalFont);
        addRow(table, "Departure", departureTime, normalFont);
        addRow(table, "Seat", seatNumber, normalFont);

        document.add(table);
        document.add(new Paragraph(" "));

        // Generate QR code
        byte[] qrBytes = generateQrCode(pnr);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.scaleToFit(150, 150);
        document.add(qrImage);

        document.close();
        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value, Font font) {
        table.addCell(new PdfPCell(new Phrase(label, font)));
        table.addCell(new PdfPCell(new Phrase(value, font)));
    }

    private byte[] generateQrCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}