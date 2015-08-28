package verizon.montoring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateTicket extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GenerateTicket() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		new DBConnectionClass().makeConnectionTicket();
		String deviceId = request.getParameter("deviceId");
		int zipcode = Integer.parseInt(request.getParameter("zipcode"));
		System.out.println("Before call");
		new MonitoringDAO().GenerateTicket(zipcode, deviceId, "open");
		System.out.println("After call");
		this.getServletContext().getRequestDispatcher("/DeviceStatus.jsp")
				.forward(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
