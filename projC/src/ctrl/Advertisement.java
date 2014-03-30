package ctrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.AdItemBean;
import model.FoodSYS;

/**
 * Servlet implementation class Advertisement
 */
@WebServlet({"/Advertisement", "/Ad"})
public class Advertisement extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Advertisement() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 HttpSession session = request.getSession();
		 Map<AdItemBean, List<AdItemBean>> adMap = (Map<AdItemBean, List<AdItemBean>>) getServletContext().getAttribute("adMap");
		 FoodSYS fSys = (FoodSYS) getServletContext().getAttribute("fSys");
		 
		 String promoteID = request.getParameter("promoteID");
		 String relatedID = request.getParameter("relatedID");
		 
		 if (promoteID != null && relatedID != null) {
			 try {
				fSys.addAdRelation(adMap, promoteID, relatedID);
			 } catch (Exception e) {
				request.setAttribute("error", e.getMessage());
			 }
		 }
		 
		 // Debug Advertise Map
		 System.out.println("\nAD MAP size: " + adMap.size());
		 for (Map.Entry<AdItemBean, List<AdItemBean>> entry: adMap.entrySet()) {
			 System.out.println("---------" + entry.getKey().getName());
			 for (AdItemBean aib: entry.getValue()) {
				 System.out.println("\t|------" + aib.getName());
			 }
		 }
		 
		 request.setAttribute("client", session.getAttribute("client"));
		 getServletContext().getRequestDispatcher("/Advertisement.jspx").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
