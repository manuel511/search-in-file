package com.mx.m.searchinfile.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchFilesService {

	@Value("${ruta.file}")
    private String rutaFile;
	
	@Value("${ruta.salida}")
    private String rutaSalida;
	
	@Value("${listaExp}")
    private List<String> listExp;
	
	@Value("${page.inicio}")
    private int start;
	
	@Value("${page.fin}")
    private int end;
	
	@Autowired
	private IProcessFileService processFile;
	
	
	@EventListener(ApplicationReadyEvent.class)
	public void searchFile() {
		
		processFile.saveFile(rutaSalida + "dummy.txt",
				processFile.convertPDF(rutaFile));
		
		boolean isFirst = true;
		
		List<String> expediente = new ArrayList<>();
		List<String> listaExpediente = new ArrayList<>();
		LinkedHashSet<String> newlistExp = new LinkedHashSet<>();
		String idExp = null;
		try(BufferedReader reader = new BufferedReader(
				new FileReader(rutaSalida + "dummy.txt"))
				) {
			
			String[] word = null;
			boolean is = false;
			String line = reader.readLine();
			while (line != null) {
				if(line.contains("Núm. Exp.")) {
					word = line.split("Núm. Exp.");
					is = true;
				} else if(line.contains("Acdo. Núm.")) {
					word = line.split("Acdo. Núm.");
					is = true;
				} else if(line.contains("Acdos. Núm.")) {
					word = line.split("Acdos. Núm.");
					is = true;
				}
				
				if(is) {
					isFirst = false;
					word = (word.length == 2 ) ? word[1].split(" ") : word;
					if(word.length > 1) {
						
						idExp = word[1].replace(".", "");
						if(listExp.contains(idExp.trim())) {
							newlistExp.add(idExp);
							if(!line.substring(line.length() - 2).equals(". ")) {
								log.info(line);
								expediente.add(line);
								line = reader.readLine();
							}
						} else {
							is = false;
							line = reader.readLine();
							expediente.clear();
							continue;
						}
						log.debug(">>" + idExp + "<<<<<");
					} else {
						expediente.add(line);
						line = reader.readLine();
						word = line.split(" ");
						if(word[0].equals("Exp.")) idExp = word[1].replace(".", "");
						else idExp = word[0].replace(".", "");

						if(listExp.contains(idExp.trim())) {
							newlistExp.add(idExp);
						} else {
							is = false;
							line = reader.readLine();
							expediente.clear();
							continue;
						}
						log.debug(">>" + idExp + "<<<<<");
					}
				}
				
				if(is) {
					
					if(isFirst) {
						expediente.add("Es primer registro: " + line);
						isFirst = false;
					} else {
						expediente.add(line);
					}
					listaExpediente.add(expediente.toString().replace(" ,", ""));
					expediente.clear();
					
					line = reader.readLine();
					is = false;
					continue;
					
				}
				if(!isFirst) {
					expediente.add(line);
				}
				
				// read next line
				line = reader.readLine();
			}
			reader.close();
			
			saveExp(listaExpediente, newlistExp);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		File fichero = new File(rutaSalida + "dummy.txt");
		
		if (!fichero.delete()) log.warn("El fichero dummy no puede ser borrado");
	}
	
	private void saveExp(List<String> listaExpediente, LinkedHashSet<String> newlistExp) {
		processFile.saveFile(rutaSalida + "expedientes.txt",
				listaExpediente.toString().replace("SOLO CONSULTA   SOLO CONSULTA   SOLO CONSULTA, ", ""));
		
		LinkedHashSet<String> listNotFound = new LinkedHashSet<>();
		for(String uniq: listExp) {
			if(!newlistExp.contains(uniq.trim())) {
				listNotFound.add(uniq);
			}
		}
		processFile.saveFile(rutaSalida + "expedientes no encontados.txt",
				listNotFound.toString().replace(",", "\r\n"));
	}
	
}
