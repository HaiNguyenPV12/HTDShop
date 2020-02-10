package vn.htdshop.controller.shop;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.htdshop.entity.User;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.UserFacadeLocal;

/**
 * shopService
 */
@Service("shopService")
public class ShopService {
    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "UserFacade")
    UserFacadeLocal userFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    public boolean checkLogin() {
        System.out.println("Service called.");
        if (session.getAttribute("loggedInCustomer") != null) {
            System.out.println("Have session.");
            return true;
        } else {
            System.out.println("No session.");
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInCustomer"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                User user = userFacade.find(Integer.parseInt(cookie));
                System.out.println("Have cookie.");
                if (user != null) {
                    session.setAttribute("loggedInCustomer", user.getCustomerCollection().toArray()[0]);
                    return true;
                }
            }
            System.out.println("No cookie.");
        }
        return false;
    }

    public static String test(){
        return "test";
    }
}