package com.clickclack.utils;

import com.google.zxing.WriterException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Component
public class PdfContent {

    private static final ErrorTracker errorTracker = new ErrorTracker();

    public static byte[] generatePdfContent(String userText, long spaceCount, String alignText, String alignTitle,
                                            float characterSpacing, float lineSpacing, float fontSize, String footerNote, String qrCodeLink) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);

            pdfDocument.addNewPage();

            Document document = new Document(pdfDocument, PageSize.A4, false);

            TextAlignment mainTextAlignment = getTextAlignment(alignText);
            TextAlignment titleAlignment = getTextAlignment(alignTitle);

            addMainText(document, userText, mainTextAlignment, titleAlignment, fontSize, characterSpacing, lineSpacing);

            float footerYPosition = 50;
            addFooterNote(document, pdfDocument, footerNote, fontSize, footerYPosition, lineSpacing);

            // Add QR Code to bottom-right corner
            float qrCodeX = pdfDocument.getDefaultPageSize().getWidth() - 100; // Adjust position
            float qrCodeY = 30; // Adjust position
            addQrCode(document, qrCodeLink, qrCodeX, qrCodeY);

            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException | WriterException e) {
            errorTracker.logError("Error generating PDF content", e);
            return new byte[0];
        }
    }

    private static void addQrCode(Document document, String qrCodeLink, float x, float y) throws IOException, WriterException {
        if (qrCodeLink != null && !qrCodeLink.isEmpty()) {
            // Generate QR code as byte array
            byte[] qrCodeBytes = QRCodeGenerator.generateQRCode(qrCodeLink, 100, 100);

            // Convert byte array to iText Image object
            com.itextpdf.layout.element.Image qrCodeImage = new com.itextpdf.layout.element.Image(
                    com.itextpdf.io.image.ImageDataFactory.create(qrCodeBytes)
            );

            // Set position and size for the QR code
            qrCodeImage.setFixedPosition(x, y);
            qrCodeImage.setWidth(100);
            qrCodeImage.setHeight(100);

            // Add the QR code to the document
            document.add(qrCodeImage);
        }
    }

    private static TextAlignment getTextAlignment(String alignText) {
        if ("right".equalsIgnoreCase(alignText)) {
            return TextAlignment.RIGHT;
        } else if ("center".equalsIgnoreCase(alignText)) {
            return TextAlignment.CENTER;
        } else if ("justify".equalsIgnoreCase(alignText)) {
            return TextAlignment.JUSTIFIED;
        } else {
            return TextAlignment.LEFT; // Default alignment
        }
    }

    private static Paragraph createParagraphWithFormatting(String text, PdfFont boldFont, PdfFont regularFont, float fontSize, float characterSpacing, float lineSpacing) {
        Paragraph paragraph = new Paragraph();
        paragraph.setFontSize(fontSize)
                .setCharacterSpacing(characterSpacing)
                .setMultipliedLeading(lineSpacing);

        String[] lines = text.split("\n");

        for (String line : lines) {
            String[] parts = line.split("\\*");

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];

                if (i % 2 == 1) {
                    paragraph.add(new com.itextpdf.layout.element.Text(part).setFont(boldFont));
                } else {
                    paragraph.add(new com.itextpdf.layout.element.Text(part).setFont(regularFont));
                }
            }

            paragraph.add("\n");
        }

        return paragraph;
    }

    private static void addMainText(Document document, String userText, TextAlignment mainTextAlignment, TextAlignment titleAlignment, float fontSize, float characterSpacing, float lineSpacing) throws IOException {
        if (userText != null && !userText.isEmpty()) {
            PdfFont regularFont = PdfFontFactory.createFont("src/main/resources/fonts/JMHTypewriter-Regular.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            PdfFont boldFont = PdfFontFactory.createFont("src/main/resources/fonts/JMH Typewriter-Black.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            // Split the text into lines
            String[] lines = userText.split("\n");

            // Determine if there is a title (e.g., check if the first line is significantly different or explicitly marked)
            boolean hasTitle = lines.length > 1 && !lines[0].trim().isEmpty();

            // Add the title if it exists
            if (hasTitle) {
                Paragraph titleParagraph = createParagraphWithFormatting(lines[0], boldFont, regularFont, fontSize, characterSpacing, lineSpacing);
                titleParagraph.setTextAlignment(titleAlignment).setFontSize(fontSize + 2); // Slightly larger font for the title
                document.add(titleParagraph);
            }

            // Add remaining lines as main content
            for (int i = (hasTitle ? 1 : 0); i < lines.length; i++) {
                Paragraph contentParagraph = createParagraphWithFormatting(lines[i], boldFont, regularFont, fontSize, characterSpacing, lineSpacing);
                contentParagraph.setTextAlignment(mainTextAlignment);
                document.add(contentParagraph);
            }
        }
    }


    private static void addFooterNote(Document document, PdfDocument pdfDocument, String footerNote, float fontSize, float lineSpacing, float qrCodeYPosition) throws IOException {
        // Define the fonts for footer text
        PdfFont regularFont = PdfFontFactory.createFont("src/main/resources/fonts/JMHTypewriter-Regular.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfFont boldFont = PdfFontFactory.createFont("src/main/resources/fonts/JMH Typewriter-Black.ttf", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        // Create the footer paragraph with the given formatting
        Paragraph footerParagraph = createParagraphWithFormatting(footerNote, boldFont, regularFont, fontSize, 0, lineSpacing);

        // Page width and height for positioning the footer
        float pageWidth = pdfDocument.getPage(1).getPageSize().getWidth();
        float pageHeight = pdfDocument.getPage(1).getPageSize().getHeight();

        // QR Code position adjustments (Assumed QR code at bottom-right)
        float qrCodeHeight = 100;  // Size of the QR Code

        // Set the position of the footer just above the QR Code (with some padding)
        float footerYPosition = qrCodeYPosition + qrCodeHeight + 10;  // Padding of 10 units above QR code
        if (footerYPosition > pageHeight - fontSize) {
            footerYPosition = pageHeight - fontSize;  // Prevent the footer from going off the page
        }

        // Show footer text aligned to the bottom-right (or left, if you prefer)
        document.showTextAligned(
                footerParagraph,
                pageWidth - 70,  // 70 units from the right
                footerYPosition,
                pdfDocument.getNumberOfPages(),
                TextAlignment.RIGHT,
                VerticalAlignment.BOTTOM,
                0
        );
    }


}
