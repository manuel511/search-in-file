package com.mx.m.searchinfile.service;

public interface IProcessFileService {

	String convertPDF(String rutaFile);

	void saveFile(String path, String text);

}