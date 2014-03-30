package ctrl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.AdItemBean;
import model.FoodSYS;
import model.ItemBean;
import model.Order;

/**
 * Servlet implementation class Cart
 */
@WebServlet("/Cart")
public class Cart extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Cart() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String filepath = getServletContext().getRealPath("export/");
		
		Map<AdItemBean, List<AdItemBean>> adMap = (Map<AdItemBean, List<AdItemBean>>) getServletContext().getAttribute("adMap");
		FoodSYS fSys = (FoodSYS) getServletContext().getAttribute("fSys");
		Order order = (Order)session.getAttribute("order");
		
		
		if(request.getParameter("update") != null)
		{				
			String[] markedDelList = request.getParameterValues("delete");
			Order orderAfterDelete = fSys.delFromCart(markedDelList, order); // RUN DEL LIST
			
			String[] zeroQtyDelList = new String[orderAfterDelete.getItems().size()];
			
			int i = 0;
			for (ItemBean item: orderAfterDelete.getItems()) {
				
				String newQty = request.getParameter("qty" + item.getNumber());
				if (newQty != null && newQty.matches("\\d+") && Integer.parseInt(newQty) >= 0) { // validate qty
					if (newQty.equals("0")) {
						zeroQtyDelList[i] = item.getNumber();
						i++;
					}
					else {
						fSys.updateCart(item.getNumber(), Integer.parseInt(newQty), orderAfterDelete);
					}
				}
								
			}
			
			System.out.println("Zero Del List: ");
			for (String zero: zeroQtyDelList) {
				System.out.print(zero + " ");
			}
			System.out.println();
			orderAfterDelete = fSys.delFromCart(zeroQtyDelList, orderAfterDelete);
			
			
		}
		
		Map<String, Integer> itemMap = null;
		try {
			itemMap = fSys.unmarshalToMap(filepath, "ALL");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<AdItemBean> popList = fSys.getAdItems(adMap, itemMap, 6);
		request.setAttribute("mostPop", popList);
		
		List<AdItemBean> adList = fSys.getAdItems(adMap, (Order)session.getAttribute("order"), 6);
		request.setAttribute("adList", adList);
		
		request.setAttribute("client", session.getAttribute("client"));
		request.setAttribute("order", session.getAttribute("order"));
		if (session.getAttribute("order") != null && ((Order)session.getAttribute("order")).getItems().size() <= 0)
			session.setAttribute("order", null);
		getServletContext().getRequestDispatcher("/Cart.jspx").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
