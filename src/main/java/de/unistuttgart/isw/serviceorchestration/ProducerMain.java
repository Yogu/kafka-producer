package de.unistuttgart.isw.serviceorchestration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageBus;
import de.unistuttgart.isw.serviceorchestration.servicecore.MessageSender;


public class ProducerMain {


    public static String createXML() {
        XStream xstream = new XStream(new StaxDriver());
        float x = (float) Math.random();
        return xstream.toXML(x);
    }



    public static void main(String[] args) throws Exception {
        // opcfoundation does not like Java default user agent
        System.setProperty("http.agent", "Mozilla/5.0");

        MessageBus bus = new MessageBus();
        MessageSender sender = bus.createSender("output", "https://opcfoundation.org/UA/2008/02/Types.xsd");

        while (true) {
            sender.send(createXML());
            Thread.sleep(1000);
        }
    }

}
