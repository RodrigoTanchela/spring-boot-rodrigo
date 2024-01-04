package br.com.rodrigo.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import br.com.rodrigo.config.FileStorageConfig;
import br.excpetions.FileStorageException;
import br.excpetions.MyFileNotFoundException;

@Service
public class FileStorageService {
	
	//Essa variavel é o caminho completo ate a pasta a onde serao salvos
	private final Path fileStorageLocation;
	
	//Transformando em path java.nio
	@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUploadDir())
				.toAbsolutePath().normalize();
		
		//Se o diretorio existir ele usa
		this.fileStorageLocation = path;
		
		// Se não ele cria
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch(Exception e) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored!", e);
		}
	}
	
	//Gravando arquivos em disco
	public String storeFile(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());		
		try {
			// Filename..txt
			//Verificando se a extensão é valida pode mudar de acordo com a regra de negocio
			if(filename.contains("..")) {
				throw new FileStorageException("Sorry! filename contains invalid path sequence "+ filename);
			}
			//Tamaho completo ate o arquivo
			//Realiza a copia do arquivo substituindo se existe
			//Para salvar na nuvem deve ser alterado essa duas linhas
			Path targetLocation = this.fileStorageLocation.resolve(filename);			
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return filename; 
		} catch (Exception e) {
			throw new FileStorageException("Could not store file" + filename+ " Please try again!", e);
		}
	}
	
	public Resource loadFileAsResource(String filename) {
		try {
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if(resource.exists()) return resource;
			else throw new MyFileNotFoundException("File not found");
		} catch (Exception e) {
			throw new MyFileNotFoundException("File not found" + filename, e);
		}
	}
}
