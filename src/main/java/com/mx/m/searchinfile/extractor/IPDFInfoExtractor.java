package com.mx.m.searchinfile.extractor;

import java.util.List;

public interface IPDFInfoExtractor {

	List<String> parsePDFDocument(String filePath, int start, int end);

}