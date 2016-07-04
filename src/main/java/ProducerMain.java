import de.unistuttgart.isw.serviceorchestration.api.MessageBus;
import de.unistuttgart.isw.serviceorchestration.api.MessageSender;

public class ProducerMain {

	static final String xmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
			"<ListOfFloat xmlns=\"http://opcfoundation.org/UA/2008/02/Types.xsd\">\n" +
			"    <Float>3.14</Float>\n" +
			"    <Float>42.0</Float>\n" +
			"    <Float>2.78</Float>\n" +
			"    <Float>2.81e-13</Float>\n" +
			"    <Float>NaN</Float>\n" +
			"</ListOfFloat>";

	public static void main(String[] args) throws Exception {
		// opcfoundation does not like Java default user agent
		System.setProperty("http.agent", "Mozilla/5.0");

		MessageBus bus = new MessageBus();
		MessageSender sender = bus.createSender("output", "https://opcfoundation.org/UA/2008/02/Types.xsd");

		while (true) {
			sender.send(xmlFile);
			Thread.sleep(1000);
			System.out.println("Sent message");
		}
	}

}
