/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/comment")
public class managerCommentController {
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "ProductCommentFacade")
    ProductCommentFacadeLocal commentFacade;

    @EJB(mappedName = "ProductCommentReplyFacade")
    ProductCommentReplyFacadeLocal commentReplyFacade;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // === Comment View ===\\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        if (!managerService.checkLoginWithRole("comment_read")) {
            return redirectHome;
        }
        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass order list to session
        model.asMap().put("comments", commentFacade.findAll().stream()
                .sorted(Comparator.comparingInt(ProductComment::getId)).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "comment");

        return "HTDManager/comment";
    }

    // == Reply-Process ==\\
    @RequestMapping(value = "doReply", method = RequestMethod.POST)
    public String doReply(@RequestParam(value = "commentid", required = false) Integer commentid,
            @RequestParam(value = "reply", required = false) String reply) {
        try {
            ProductCommentReply r = new ProductCommentReply();
            r.setId(null);
            r.setProductComment(new ProductComment(commentid));
            r.setCustomer(null);
            r.setStaff(managerService.getLoggedInStaff());
            r.setContent(reply);
            r.setCreatedAt(new Date());
            commentReplyFacade.create(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/manager/comment";
    }

    @RequestMapping(value = "content", method = RequestMethod.POST)
    public String getCommentContent(Model model, @RequestParam(value = "id", required = false) Integer id) {
        ProductComment cmt = commentFacade.find(id);
        model.asMap().put("content", cmt);
        return "HTDManager/comment_content";
    }

}
