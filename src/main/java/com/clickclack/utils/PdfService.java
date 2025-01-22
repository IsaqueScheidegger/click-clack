package com.clickclack.utils;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfService {

    // Method to save PDF
    public void savePdf(byte[] pdfBytes, String filename) throws IOException {
        // Define the path to the static media folder
        Path path = Paths.get("src/main/resources/static/media", filename);

        // Create parent directories if they don't exist
        Files.createDirectories(path.getParent());

        // Write the PDF content to the specified file
        Files.write(path, pdfBytes);
    }
}
