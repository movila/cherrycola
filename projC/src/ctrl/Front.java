package ctrl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.AdItemBean;
import model.CategoryBean;
import model.ClientBean;
import model.FoodSYS;
import model.ItemBean;
import model.Order;

/**
 * Servlet implementation class Front
 */
@WebServlet(urlPatterns = {"/Home", "/Home/", "/Home/cat", "/Home/cat/*", "/Front", "/Home/q"})
public class Front extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    @Override
	public void init() throws ServletException {
		super.init();
		
		try {
			Map<AdItemBean, List<AdItemBean>> adMap = new HashMap<AdItemBean, List<AdItemBean>>();
			this.getServletContext().setAttribute("adMap", adMap);
			
			FoodSYS fSys = new FoodSYS();
			fSys.generateImage(this.getServletContext().getRealPath("static/image/")); 	// generate category image on start
			this.getServletContext().setAttribute("fSys", fSys);						// set global FoodSYS accessor
			
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
     * @see HttpServlet#HttpServlet()
     */
    public Front() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		FoodSYS fSys = (FoodSYS) getServletContext().getAttribute("fSys");
		request.setAttribute("contextPath", request.getContextPath());
		
		List<CategoryBean> cateList = null;
		List<ItemBean> itemList = null;
		
		String identifier; 	// term used to filter the result items list
		String sender;		// where the filter-criteria comes from
		if(request.getParameter("item")==null) {
			identifier = request.getPathInfo();		
			sender = "path";
		} else {
			identifier = request.getParameter("item");
			sender = "search";
		}
		
		try {
			cateList = fSys.retrieveCategory();
			itemList = fSys.retrieveItem(identifier, sender);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// Infinite loading
		// int page = 0; // put to top Private area
		// itemList = fSys.limitList(itemList, page);
		// if (request.getParameter("continueLoad") {
		// 		page++;
		//		itemList = fSys.limitList(itemList, page);
		//		response.setContentType("text/json");
		//		response.write(fSys.jsonParse(itemList));
		// } else {
		
		request.setAttribute("cateList", cateList);
		request.setAttribute("itemList", itemList);
		
		System.out.println("PATH: " + request.getRequestURI().substring(request.getContextPath().length()));
		
		
		// View dispatch
		if (request.getParameter("logout") != null) {
			session.removeAttribute("client");
			session.removeAttribute("order");
		} else if (request.getParameter("addToCart") != null) {
			System.out.println("ADD TO CARTs");

			String name = request.getParameter("name");
			String number = request.getParameter("number");
			String price = request.getParameter("price");
			String qty = request.getParameter("qty");
			if (fSys.itemValidation(number, name, price, qty)) { // if all the requested items correct, add to cart
				double dPrice = Double.parseDouble(price);
				int iQty = Integer.parseInt(qty);
				double extended = dPrice * iQty;
				ItemBean ib = new ItemBean(number, name, dPrice, iQty, extended);
				ClientBean client = (ClientBean) session.getAttribute("client");
				Order order = fSys.addToOrder(client, ib, (Order) session.getAttribute("order"));
				session.setAttribute("order", order);
			}
		}
		
		
		request.setAttribute("client", session.getAttribute("client"));
		request.setAttribute("order", session.getAttribute("order"));
		getServletContext().getRequestDispatcher("/Home.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
