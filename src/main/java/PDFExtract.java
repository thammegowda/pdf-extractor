import com.google.common.io.Files;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.List;


public class PDFExtract {

    private Parser parser;
    private ParseContext parseContext;
    public PDFExtract(){
        parser = new AutoDetectParser();
        TesseractOCRConfig config = new TesseractOCRConfig();
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);

        parseContext = new ParseContext();
        parseContext.set(TesseractOCRConfig.class, config);
        parseContext.set(PDFParserConfig.class, pdfConfig);
        //need to add this to make sure recursive parsing happens!
        parseContext.set(Parser.class, parser);
    }

    public String extract(String path) throws Exception {
        BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
        try (FileInputStream stream = new FileInputStream(path)) {
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata, parseContext);
            //System.out.println(metadata);
            return handler.toString();
        }
    }

    public static void main(String[] args)
            throws Exception {
        if (args.length != 1) {
            System.out.println("Error: Invalid args.");
            System.out.println("Args: [FileList]");
            System.out.println("Format of input is a list of lines, each line contains following" +
                    "\n /path/to/input.pdf,/path/to/output.txt");
            System.exit(1);
        }
        String fileList = args[0];
        PDFExtract extractor = new PDFExtract();
        List<String> lines = Files.readLines(new File(fileList), Charset.defaultCharset());

        for (int i = 0; i < lines.size(); i++) {
            long st =  System.currentTimeMillis();
            String[] parts = lines.get(i).split(",");
            String input = parts[0];
            String output = parts[1];
            System.out.println(i + "/" + lines.size() + " :: " + input + " --> " + output);
            File outFile = new File(output);
            try {
                outFile.getParentFile().mkdirs();
                String content = extractor.extract(input);
                Files.write(content, outFile, Charset.forName("UTF8"));
            } catch (Exception e){
                System.out.println("Error:, " + lines.get(i) + ", " + e.getMessage());
            }
            System.out.println("Time took:" + (System.currentTimeMillis()  - st) + "ms" );
        }
        System.out.println("==Done==");
    }
}
