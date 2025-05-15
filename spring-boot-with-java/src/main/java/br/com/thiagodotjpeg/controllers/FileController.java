package br.com.thiagodotjpeg.controllers;

import br.com.thiagodotjpeg.controllers.docs.FileControllerDocs;
import br.com.thiagodotjpeg.data.dto.UploadFileResponseDTO;
import br.com.thiagodotjpeg.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/file/")
public class FileController implements FileControllerDocs {

  private static final Logger log = LoggerFactory.getLogger(FileController.class);

  @Autowired
  private FileStorageService service;


  @PostMapping("/uploadFile")
  @Override
  public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
    var fileName = service.storeFile(file);
    var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/file/downloadFile/").path(fileName).toUriString();
    return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/uploadMultipleFiles")
  @Override
  public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("file") MultipartFile[] files) {
    return Arrays.stream(files).map(this::uploadFile).collect(Collectors.toList());
  }

  @GetMapping("/downloadFile/{fileName:.+}")
  @Override
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    Resource resource = service.loadFileAsResource(fileName);
    String contentType = "application/octet-stream";
    try {
      contentType = Objects.requireNonNullElse(request.getServletContext().getMimeType(resource.getFile().getAbsolutePath()), "application/octet-stream");
    } catch (Exception ex) {
      log.error("Couldn't determine file type!");
    }
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment=\"" + resource.getFilename() + "\"").body(resource);
  }
}
