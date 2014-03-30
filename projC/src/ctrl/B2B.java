package ctrl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.B2BBean;
import model.FoodSYS;

/**
 * Servlet implementation class B2B
 */
@WebServlet({"/B2B", "/Manage"})
public class B2B extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public B2B() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 HttpSession session = request.getSession();
		FoodSYS fSys = (FoodSYS) getServletContext().getAttribute("fSys");
		String filepath = getServletContext().getRealPath("export/");
		List<B2BBean> b2b = null;
		
		
		if (request.getParameter("placeorder") != null) {
			try {
				Map<String, Integer> itemMap = fSys.unmarshalToMap(filepath, request.getParameter("range"));
				b2b = fSys.requestB2B(itemMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (request.getParameter("generimage") != null) {
			int generateProgress = 0;
			try {
				generateProgress = fSys.itemImageGenerator(getServletContext().getRealPath("static/image/google/"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		request.setAttribute("client", session.getAttribute("client"));
		request.setAttribute("b2b", b2b);
		getServletContext().getRequestDispatcher("/B2B.jspx").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
