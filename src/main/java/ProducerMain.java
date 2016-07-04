import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.Properties;

public class ProducerMain {

    static final String xmlFile = "<?xml vesion=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<ListOfFloat xmlns=\"http://opcfoundation.org/UA/2008/02/Types.xsd\">\n" +
            "    <Float>3.14</Float>\n" +
            "    <Float>42.0</Float>\n" +
            "    <Float>2.78</Float>\n" +
            "    <Float>2.81e-13</Float>\n" +
            "    <Float>NaN</Float>\n" +
            "    <Float+Inf>\n" +
            "</ListOfFloat>";
    static final String EXI_EXTENSION = "_exi";
    static final String EXI_SCHEMA_EXTENSION = "_exi_schema";
    static final String XML_EXTENSION = ".xml";


    private String getEXILocation() {

        return "/home/martin/Dokumente/test" + "Test" + EXI_SCHEMA_EXTENSION;

    }

    protected void encode(String xml, ContentHandler ch) throws Exception {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(ch);

        // parse xml file
        xmlReader.parse(new InputSource(new StringReader(xml)));
    }

    protected void decode(XMLReader exiReader, String exiLocation)
            throws SAXException, IOException, TransformerException {

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        InputStream exiIS = new FileInputStream(exiLocation);
        SAXSource exiSource = new SAXSource(new InputSource(exiIS));
        exiSource.setXMLReader(exiReader);

        OutputStream os = new FileOutputStream(exiLocation + XML_EXTENSION);
        transformer.transform(exiSource, new StreamResult(os));
        os.close();
    }

    protected void codeSchemaInformed() throws Exception {
        String exiLocation = getEXILocation();

        // create default factory and EXI grammar for schema
        EXIFactory exiFactory = DefaultEXIFactory.newInstance();
        GrammarFactory grammarFactory = GrammarFactory.newInstance();
        Grammars g = grammarFactory.createGrammars(new URL("https://opcfoundation.org/UA/2008/02/Types.xsd").openStream());
        exiFactory.setGrammars(g);

        // encode
        OutputStream exiOS = new FileOutputStream(exiLocation);
        EXIResult exiResult = new EXIResult(exiFactory);
        exiResult.setOutputStream(exiOS);
        encode(xmlFile, exiResult.getHandler());
        exiOS.close();

        // decode
        EXISource saxSource = new EXISource(exiFactory);
        XMLReader xmlReader = saxSource.getXMLReader();
        decode(xmlReader, exiLocation);
    }

    public static void main(String[] args) throws Exception {
        ProducerMain main =  new ProducerMain();
        main.codeSchemaInformed();

//        // XML
//        String xmlLocation;
//        String xmlName;
//        // XML Schema
//        String xsdLocation;
//
//        String xml = " <xs:element name=\"Float\" nillable=\"true\" type=\"xs:float\" />";
//
//        Properties props = new Properties();
//        props.put("bootstrap.servers", "kafka:9092");
//        props.put("acks", "all");
//        props.put("retries", 0);
//        props.put("batch.size", 16384);
//        props.put("linger.ms", 1);
//        props.put("buffer.memory", 33554432);
//        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        String topicName = System.getenv("OUTPUT_output");
//        System.out.println("Using topic " + topicName);
//
//        System.out.println("Creating producer...");
//        Producer<String, String> producer = new KafkaProducer<>(props);
//        System.out.println("Created producer. Pushing message stream");
//        int i = 0;
//        while (true) {
//            producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(i),
//                    Integer.toString(i)));
//            i++;
//        }

    }
}
