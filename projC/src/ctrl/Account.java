package ctrl;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ClientBean;
import model.FoodSYS;
import model.Order;

/**
 * Servlet implementation class Account
 */
@WebServlet("/Login")
public class Account extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Account() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		
		if (session.getAttribute("client") != null) { // no need to log in again, go Home
			request.setAttribute("client", session.getAttribute("client"));
			response.sendRedirect(request.getContextPath() + "/Home");
			//getServletContext().getRequestDispatcher("/Home.jspx").forward(request, response);
		} else {
			
			if (request.getParameter("login") == null) { // Log in page fresh visit
				getServletContext().getRequestDispatcher("/Login.jspx").forward(request, response);
			} else { // user pushed log in button
				
				String clientName = request.getParameter("clientName");
				String password = request.getParameter("password");
				
				FoodSYS fSys = (FoodSYS) this.getServletContext().getAttribute("fSys");
				try {
					// validate client log in info against database
					ClientBean client = fSys.clientValidation(clientName, password);
					if (client != null) { // log in info is correct, go Home
						session.setAttribute("client", client);
						
						Order order = (Order) session.getAttribute("order");
						if(order != null) order.setCustomer(client);
						
						if (session.getAttribute("checkout") != null) {
							session.setAttribute("checkout", null);
							response.sendRedirect(request.getContextPath() + "/Checkout");
						}
						else
							response.sendRedirect(request.getContextPath() + "/Home");
						//getServletContext().getRequestDispatcher("/Home.jspx").forward(request, response);
						
					} else { // info not right, refill the log in info
						
						request.setAttribute("loginError", "Log in info is NOT corrected");
						getServletContext().getRequestDispatcher("/Login.jspx").forward(request, response);
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
