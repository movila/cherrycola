package ctrl;

import java.io.File;
import java.io.IOException;

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
 * Servlet implementation class Checkout
 */
@WebServlet("/Checkout")
public class Checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Checkout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		if (session.getAttribute("client") == null) {
			session.setAttribute("checkout", "Confirm");
			response.sendRedirect(request.getContextPath() + "/Login");
		} else if(session.getAttribute("order")==null) {
			response.sendRedirect(request.getContextPath() + "/Cart");
		} else {
			FoodSYS fSys = (FoodSYS) getServletContext().getAttribute("fSys");
			
			Order order = (Order) session.getAttribute("order");
			int orderID = fSys.getMaxOrMaxPO(-1, new File(getServletContext().getRealPath("export/")));
			System.out.println("===============================\nOrderID: " + orderID);
			order.setId(orderID);
			
			
			int clientNumber = ((ClientBean) session.getAttribute("client")).getNumber();
			int poNumber = fSys.getMaxOrMaxPO(clientNumber, new File(getServletContext().getRealPath("export/")));
			String filepath = this.getServletContext().getRealPath("export/po" + clientNumber + "_" + poNumber + ".xml");
			System.out.println(filepath);
			
			try {
				fSys.export(order, orderID, filepath, request.getContextPath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			
			request.setAttribute("POLink", "po" + clientNumber + "_" + poNumber + ".xml");
			request.setAttribute("order", session.getAttribute("order"));
			getServletContext().getRequestDispatcher("/Checkout.jspx").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
