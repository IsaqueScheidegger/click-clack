package com.clickclack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfParameters {
    private String userText;
    private long spaceCount;
    private String fileName;
    private String alignText;
    private String alignTitle;
    private float characterSpacing;
    private float lineSpacing;
    private float fontSize;
    private String footerNote;
    private String qrCodeLink;
}
