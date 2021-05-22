package com.mx.m.searchinfile.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class ProcessFileService implements IProcessFileService {
	
	@Override
	public String convertPDF(String rutaFile) {

	    PDFParser parser = null;
	    PDDocument pdDoc = null;
	    COSDocument cosDoc = null;
	    PDFTextStripper pdfStripper;

	    String parsedText;
	    File file = new File(rutaFile);
	    try {
	        parser = new PDFParser(new RandomAccessBufferedFileInputStream(new FileInputStream(file)));
	        parser.parse();
	        cosDoc = parser.getDocument();
	        pdfStripper = new PDFTextStripper();
	        pdDoc = new PDDocument(cosDoc);
	        parsedText = pdfStripper.getText(pdDoc);
	        //log.info(parsedText.replaceAll("[^A-Za-z0-9. ]+", ""));
	        return parsedText;
	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            if (cosDoc != null)
	                cosDoc.close();
	            if (pdDoc != null)
	                pdDoc.close();
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        }

	    }
	    return "";
	}

	@Override
	public void saveFile(String path, String text) {
		try(FileWriter writer = new FileWriter(path)) {
		    writer.write(text.replace("],", "]\r\n")); 
		}
		catch(IOException e){
		    e.printStackTrace();
		}
	}
}
