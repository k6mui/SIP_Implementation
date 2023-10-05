package sipServlet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	@XmlAttribute(name = "id")
	private String id;

	@XmlElement(name = "Servlet-class")
	private ServletClass servletClass;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ServletClass getServletClass() {
		return servletClass;
	}

	public void setServletClass(ServletClass servletClass) {
		this.servletClass = servletClass;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", servletClass=" + servletClass + "]";
	}

	
}
