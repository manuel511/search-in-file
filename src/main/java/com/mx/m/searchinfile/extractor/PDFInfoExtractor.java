package com.mx.m.searchinfile.extractor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

@Component
public class PDFInfoExtractor extends PDFTextStripper implements IPDFInfoExtractor {

    List<String> parsedText;
    
    public PDFInfoExtractor() throws IOException {}

    @Override
	public List<String> parsePDFDocument(String filePath, int start, int end) {
        parsedText = new ArrayList<>();
        
        PDDocument pdDocument = null;
        try {
            pdDocument = PDDocument.load(new File(filePath));

            this.setSortByPosition(true);
            this.setStartPage(start);
            this.setEndPage(end);
            Writer dummyWriter = new OutputStreamWriter(new ByteArrayOutputStream());
            this.writeText(pdDocument, dummyWriter);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return parsedText;
    }
    
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException{
        parsedText.add(text);
    }
}
