package com.clickclack.controller;

import com.clickclack.dtos.PdfParameters;
import com.clickclack.utils.ErrorTracker;
import com.clickclack.utils.PdfContent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/pdf")
public class PdfController {

    private static final String MEDIA_DIRECTORY = "src/main/resources/pdf/";

    private final ErrorTracker errorTracker;

    public PdfController(ErrorTracker errorTracker) {
        this.errorTracker = errorTracker;
    }

    @GetMapping("/generate")
    public ModelAndView loadPage() {
        return new ModelAndView("index");
    }

    @PostMapping("/generate")
    public RedirectView generatePdf(
            @RequestParam String userText,
            @RequestParam String fileName,
            @RequestParam(required = false, defaultValue = "left") String alignText,
            @RequestParam(required = false, defaultValue = "center") String alignTitle,
            @RequestParam(defaultValue = "0.5") float characterSpacing,
            @RequestParam(defaultValue = "0.5") float lineSpacing,
            @RequestParam(defaultValue = "12") float fontSize,
            @RequestParam(required = false, defaultValue = "") String footerNote,
            @RequestParam(required = false, defaultValue = "") String qrCodeLink) {

        try {
            // Calculate leading spaces in the text
            long spaceCount = countLeadingSpaces(userText);

            // Build PDF parameters object
            PdfParameters pdfParameters = new PdfParameters(userText, spaceCount, fileName, alignText, alignTitle,
                    characterSpacing, lineSpacing, fontSize, footerNote, qrCodeLink);

            // Generate the PDF content
            byte[] pdfData = PdfContent.generatePdfContent(
                    pdfParameters.getUserText(),
                    pdfParameters.getSpaceCount(),
                    pdfParameters.getAlignText(),
                    pdfParameters.getAlignTitle(),
                    pdfParameters.getCharacterSpacing(),
                    pdfParameters.getLineSpacing(),
                    pdfParameters.getFontSize(),
                    pdfParameters.getFooterNote(),
                    pdfParameters.getQrCodeLink()
            );

            // Save the PDF to the media directory
            Path filePath = Path.of(MEDIA_DIRECTORY + fileName + ".pdf");
            Files.createDirectories(filePath.getParent()); // Ensure the directory exists
            Files.write(filePath, pdfData);

            // Redirect to the preview page with the filename as a query parameter
            RedirectView redirectView = new RedirectView("/pdf/preview");
            redirectView.addStaticAttribute("filename", fileName + ".pdf");
            return redirectView;

        } catch (IOException e) {
            // Log the error and redirect to the error page
            errorTracker.logError("Failed to generate or save the PDF file", e);
            RedirectView redirectView = new RedirectView("/pdf/error");
            redirectView.addStaticAttribute("errorMessage", errorTracker.generateUserFriendlyMessage("IO_ERR"));
            return redirectView;
        } catch (Exception e) {
            // Handle unexpected exceptions
            errorTracker.logError("An unexpected error occurred while generating the PDF", e);
            RedirectView redirectView = new RedirectView("/pdf/error");
            redirectView.addStaticAttribute("errorMessage", errorTracker.generateUserFriendlyMessage("GENERIC_ERR"));
            return redirectView;
        }
    }

    @GetMapping("/preview")
    public ModelAndView previewPdf(@RequestParam String filename) {
        ModelAndView modelAndView = new ModelAndView("preview");
        modelAndView.addObject("filename", filename);
        return modelAndView;
    }

    private long countLeadingSpaces(String text) {
        return text.chars().takeWhile(c -> c == ' ').count();
    }
}
