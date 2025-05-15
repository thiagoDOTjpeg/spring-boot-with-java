package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.config.FileStorageConfig;
import br.com.thiagodotjpeg.controllers.FileController;
import br.com.thiagodotjpeg.exceptions.FileNotFoundException;
import br.com.thiagodotjpeg.exceptions.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

  private static final Logger log = LoggerFactory.getLogger(FileController.class);

  private final Path fileStorageLocation;

  @Autowired
  public FileStorageService(FileStorageConfig fileStorageConfig) {
    Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();
    this.fileStorageLocation = path;
    try {
      log.info("Creating directories");
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      log.error("Couldn't create the directory where files will be stored!");
      throw new FileStorageException("Couldn't create the directory where files will be stored!", ex);
    }
  }
  public String storeFile(MultipartFile file) {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    try {
      if(fileName.contains("..")) throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);

      log.info("Saving file in disk");
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      return fileName;
    } catch (Exception ex) {
      log.error("Couldn't store file {}. Please try again! ", fileName);
      throw new FileStorageException("Couldn't store file " + fileName + ". Please try again!", ex);
    }
  }

  public Resource loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(!resource.exists()) throw new FileNotFoundException("File not found " + fileName);
      return resource;

    } catch (Exception ex) {
      log.error("Couldn't load file {}", fileName);
      throw new FileNotFoundException("File not found " + fileName, ex);
    }
  }
}
