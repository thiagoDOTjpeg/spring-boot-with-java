package br.com.thiagodotjpeg.file.exporter.impl;

import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;
import br.com.thiagodotjpeg.file.exporter.contract.PersonExporter;
import br.com.thiagodotjpeg.services.QRCodeService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PdfExporter implements PersonExporter {

  @Autowired
  private QRCodeService service;

  @Override
  public Resource exportPeople(List<PersonDTO> people) throws Exception {
    InputStream inputStream = getClass().getResourceAsStream("/templates/people.jrxml");
    if(inputStream == null) throw new RuntimeException("Could not find template file: /templates/people.jrxml");

    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(people);
    Map<String, Object> parameters = new HashMap<>();
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
      return new ByteArrayResource(outputStream.toByteArray());
    } catch (JRException e) {
      throw new RuntimeException("Could not export PDF file", e);
    } finally {
      inputStream.close();
    }
  }

  @Override
  public Resource exportPerson(PersonDTO person) throws Exception {
    InputStream mainTemplateStream = getClass().getResourceAsStream("/templates/person.jrxml");
    if(mainTemplateStream == null) throw new RuntimeException("Could not find template file: /templates/person.jrxml");

    InputStream subReportStream = getClass().getResourceAsStream("/templates/books.jrxml");
    if(subReportStream == null) throw new RuntimeException("Could not find template file: /templates/books.jrxml");

    JasperReport mainReport = JasperCompileManager.compileReport(mainTemplateStream);
    JasperReport subReport = JasperCompileManager.compileReport(subReportStream);

    JRBeanCollectionDataSource mainReportDataSource = new JRBeanCollectionDataSource(Collections.singleton(person));
    JRBeanCollectionDataSource subReportDataSource = new JRBeanCollectionDataSource(Collections.singleton(person.getBooks()));
    InputStream qrCodeStream = service.generateQRCode(person.getProfileUrl(), 200, 200);


    String path = getClass().getResource("/templates/books.jasper").getPath();

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("SUB_REPORT_DATA_SOURCE", subReportDataSource);
    parameters.put("SUB_REPORT_DIR", path);
    parameters.put("BOOK_SUB_REPORT", subReport);
    parameters.put("QR_CODEIMAGE", qrCodeStream);


    JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainReportDataSource);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
      JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
      return new ByteArrayResource(outputStream.toByteArray());
    }
  }
}
