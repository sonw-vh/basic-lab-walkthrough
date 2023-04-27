package com.learning.vulnearable.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import com.google.common.io.ByteSource;

@Controller
public class ReportController {
    private static final String REGEX = "^ReportUtils\\.(?:(?:subStringFunc\\(.{1,35}\\,\\s?[0-9\"]{1,5}\\s?\\,\\s?[0-9\"]{1,5}\\)$)|(?:replaceFunc\\(.{1,35}\\,\\s?[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W0-9\"]{1,15}\\s?\\,\\s?[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W0-9\"]{1,15}\\s?\\)$)|(?:concatFunc\\([a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W0-9\\s$\\{}\"]{1,35}\\,\\s?(?:[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W0-9\\s$\\{}\",])+\\s?\\)$)|(?:upperCaseFunc\\([^()!@#%^&_=,|?.]{1,35}\\)$)|(?:lowerCaseFunc\\([^()!@#%^&_=,|?.]{1,35}\\)$)|(?:comparisonFunc\\([^()!@#%^&_=,|?.><]{1,35}\\,[^()!@#%^&_=,|?.><]{1,35}\\,\\s?[\"=!><]{2,4}\\s?\\,[^()!@#%^&_=,|?.><]{1,35}\\,[^()!@#%^&_=,|?.><]{1,35}\\)$))";
    @RequestMapping("/")
    public String greeting() {
        return "report";
    }

    @PostMapping(value = "/upload-report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> createConsultReportTemplate(@RequestParam(value = "file") MultipartFile templateFile) throws URISyntaxException, IOException, JRException {
        if (templateFile == null || templateFile.isEmpty() || !templateFile.getOriginalFilename().endsWith(".jrxml") || templateFile.getSize() > 900000) {
            return ResponseEntity.ok().body("Try again, please select a jrxml file.".getBytes());
        }

        byte[] reportBytes = null;
        try {
            validateExpressionJavaLanguage(templateFile);
            reportBytes = generateReport(templateFile);
        } catch (Exception e) {
            return ResponseEntity.ok().body("Your file is not valid.".getBytes());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);
    }
    private byte[] generateReport(MultipartFile templateFile) throws JRException, IOException {
        InputStream jasperStream = templateFile.getInputStream();
        JasperDesign jasperDesign = JRXmlLoader.load(jasperStream);

        Map<String, Object> params = new HashMap<>();
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        return pdfBytes;
    }
    public static void validateExpressionJavaLanguage(MultipartFile file) throws JRException, IOException {

        if (file.isEmpty()) {
            return;
        }

        InputStream targetStream = ByteSource.wrap(file.getBytes()).openStream();
        JasperDesign jasperDesign = JRXmlLoader.load(targetStream);
        JRVariable[] jrVariables = jasperDesign.getVariables();
        for (JRVariable jrVariable : jrVariables) {
            if(jrVariable.getName().equalsIgnoreCase("logo")){
                continue;
            }
            JRExpression jrExpression = jrVariable.getExpression();
            checkExpression(jrExpression);
        }

        JRBand[] jrBands = jasperDesign.getAllBands();
        for (JRBand band : jrBands) {
            JRElement[] element = band.getElements();
            for (JRElement jrElement : element) {
                if (jrElement instanceof JRDesignTextField) {
                    JRDesignTextField jrDesignTextField = (JRDesignTextField) jrElement;
                    JRExpression jrExpression = jrDesignTextField.getExpression();
                    checkExpression(jrExpression);
                }else if (jrElement instanceof JRDesignFrame) {
                    JRDesignFrame jrDesignFrame = (JRDesignFrame) jrElement;
                    List<JRChild> childs =  jrDesignFrame.getChildren();
                    for(JRChild child : childs){
                        if(child instanceof JRDesignTextField){
                            JRDesignTextField jrDesignTextField =(JRDesignTextField) child;
                            JRExpression jrExpression = jrDesignTextField.getExpression();
                            checkExpression(jrExpression);
                        }
                    }
                }
            }
        }

    }
    public static void checkExpression(JRExpression jrExpression){
        if (jrExpression != null) {
            Pattern pattern = Pattern.compile(REGEX, Pattern.MULTILINE);
            String fullChunkText = "";
            int count = 0;
            if (jrExpression.getChunks().length > 1) {
                for (JRExpressionChunk jrExpressionChunk : jrExpression.getChunks()) {
                    fullChunkText = fullChunkText.concat(jrExpressionChunk.getText());
                    count++;
                }

                if (count == jrExpression.getChunks().length) {
                    if (!acceptKeyParameter().contains(fullChunkText) && !acceptExpression().contains(fullChunkText) && !pattern.matcher(fullChunkText).matches()) {
                        System.out.printf("Expression wrong: {}", fullChunkText);
                        System.out.printf("1");
                    }
                }
            } else {
                for (JRExpressionChunk jrExpressionChunk : jrExpression.getChunks()) {
                    if (!acceptKeyParameter().contains(jrExpressionChunk.getText()) && !acceptExpression().contains(jrExpressionChunk.getText()) && !pattern.matcher(jrExpressionChunk.getText()).matches()) {
                        System.out.printf("Expression wrong: {}", jrExpressionChunk.getText());
                        System.out.printf("2");
                    }
                }
            }
        }

    }
    public static List<String> acceptKeyParameter() {

        return Arrays.asList("param1","param2", "param3", "param4", "currentDay");
    }
    public static List<String> acceptExpression() {

        return Arrays.asList("new java.lang.Integer(1)",
                "new SimpleDateFormat(\"yyyy\").format(new Date())",
                "new SimpleDateFormat(\"MM\").format(new Date())",
                "new SimpleDateFormat(\"DD\").format(new Date())",
                "new SimpleDateFormat(\"dd\").format(new Date())",
                "new SimpleDateFormat(\"HH\").format(new Date())",
                "new SimpleDateFormat(\"HH:mm\").format(new Date())",
                "new SimpleDateFormat(\"mm\").format(new Date())",
                ".toUpperCase()", ".toLowerCase()");
    }

}
