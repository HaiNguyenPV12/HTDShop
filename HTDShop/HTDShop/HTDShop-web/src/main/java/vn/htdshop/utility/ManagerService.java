package vn.htdshop.utility;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.htdshop.entity.RoleRights;
import vn.htdshop.entity.Staff;
import vn.htdshop.sb.StaffFacadeLocal;

/**
 * managerService
 */
@Service("managerService")
public class ManagerService {

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    public Boolean checkLogin() {
        if (session.getAttribute("loggedInStaff") != null) {
            return true;
        } else {
            if (request.getCookies() != null) {
                String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (cookie != null) {
                    Staff staff = staffFacade.find(cookie);
                    if (staff != null) {
                        session.setAttribute("loggedInStaff", staff);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean checkLoginWithRole(String role) {
        if (checkLogin()) {
            String user = ((Staff) session.getAttribute("loggedInStaff")).getUsername();
            for (RoleRights roleRight : staffFacade.find(user).getRole().getRoleRightsCollection()) {
                if (roleRight.getRightsDetail().getTag().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Staff getLoggedInStaff() {
        if (checkLogin()) {
            return (Staff) session.getAttribute("loggedInStaff");
        }
        return null;
    }

}
