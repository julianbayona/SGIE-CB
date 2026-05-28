package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

class PdfWriter implements AutoCloseable {

    private static final float MARGIN = 48;
    private final PDDocument document;
    private PDPage page;
    private PDPageContentStream content;
    private float y;

    PdfWriter(PDDocument document) throws IOException {
        this.document = document;
        newPage();
    }

    void title(String text) throws IOException {
        write(text, PDType1Font.HELVETICA_BOLD, 18, MARGIN, y);
        y -= 24;
    }

    void subtitle(String text) throws IOException {
        write(text, PDType1Font.HELVETICA_BOLD, 13, MARGIN, y);
        y -= 22;
    }

    void section(String text) throws IOException {
        ensureSpace(32);
        write(text, PDType1Font.HELVETICA_BOLD, 11, MARGIN, y);
        y -= 16;
    }

    void text(String text) throws IOException {
        ensureSpace(16);
        write(text, PDType1Font.HELVETICA, 9, MARGIN, y);
        y -= 14;
    }

    void emphasis(String text) throws IOException {
        ensureSpace(18);
        write(text, PDType1Font.HELVETICA_BOLD, 10, MARGIN, y);
        y -= 16;
    }

    void paragraph(String text) throws IOException {
        String remaining = text == null ? "" : text;
        while (remaining.length() > 95) {
            text(remaining.substring(0, 95));
            remaining = remaining.substring(95);
        }
        text(remaining);
    }

    void tableHeader() throws IOException {
        ensureSpace(22);
        write("Origen", PDType1Font.HELVETICA_BOLD, 8, MARGIN, y);
        write("Descripcion", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 70, y);
        write("Cant.", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 330, y);
        write("Valor unit.", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 380, y);
        write("Subtotal", PDType1Font.HELVETICA_BOLD, 8, MARGIN + 470, y);
        y -= 14;
    }

    void tableRow(String origen, String descripcion, String cantidad, String valorUnitario, String subtotal) throws IOException {
        ensureSpace(18);
        write(truncate(origen, 12), PDType1Font.HELVETICA, 8, MARGIN, y);
        write(truncate(descripcion, 48), PDType1Font.HELVETICA, 8, MARGIN + 70, y);
        write(cantidad, PDType1Font.HELVETICA, 8, MARGIN + 330, y);
        write(valorUnitario, PDType1Font.HELVETICA, 8, MARGIN + 380, y);
        write(subtotal, PDType1Font.HELVETICA, 8, MARGIN + 470, y);
        y -= 13;
    }

    void footer() throws IOException {
        ensureSpace(24);
        y -= 8;
        write("Documento generado automaticamente por SGIE Club Boyaca. Valores expresados en pesos colombianos.",
                PDType1Font.HELVETICA_OBLIQUE, 8, MARGIN, y);
    }

    void space(float amount) throws IOException {
        ensureSpace(amount);
        y -= amount;
    }

    private void ensureSpace(float needed) throws IOException {
        if (y - needed < MARGIN) {
            newPage();
        }
    }

    private void newPage() throws IOException {
        if (content != null) {
            content.close();
        }
        page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        content = new PDPageContentStream(document, page);
        y = page.getMediaBox().getHeight() - MARGIN;
    }

    private void write(String text, PDType1Font font, int fontSize, float x, float yPosition) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, yPosition);
        content.showText(sanitizePdfText(text));
        content.endText();
    }

    private String truncate(String text, int length) {
        if (text == null) {
            return "";
        }
        return text.length() <= length ? text : text.substring(0, Math.max(0, length - 3)) + "...";
    }

    private String sanitizePdfText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace('\u00A0', ' ')
                .replace("\r", " ")
                .replace("\n", " ")
                .replace("–", "-")
                .replace("—", "-");
    }

    @Override
    public void close() throws IOException {
        if (content != null) {
            content.close();
            content = null;
        }
    }
}
