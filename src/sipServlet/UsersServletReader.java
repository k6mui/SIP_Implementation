package sipServlet;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class UsersServletReader {
	public static void main(String[] args) {
		try (InputStream xml = UsersServletReader.class
				.getResourceAsStream("users.xml");) {
			JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Users users = (Users) jaxbUnmarshaller.unmarshal(xml);
			System.out.println(users.toString());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
}