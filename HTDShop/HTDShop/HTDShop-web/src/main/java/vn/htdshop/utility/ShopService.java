package vn.htdshop.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.htdshop.entity.CartItem;
import vn.htdshop.entity.Customer;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.PreBuiltRating;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.entity.UserSetting;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.sb.PromotionFacadeLocal;

/**
 * shopService
 * @author Thien
 */
@Service("shopService")
public class ShopService {

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    private List<UserSetting> userSettings = null;

    private final String recaptchaSecret = "6LdeYdoUAAAAALcq3j6DUTz05OSoeXjcwqy4irFP";

    private final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // public boolean isBeforeOrNow(Date date) {
    // return date.compareTo(new LocalDate().toDate()) >= 0;
    // }

    // public Date localToday() {
    // return new LocalDate().toDate();
    // }

    // =========== CART FUNCTION ============
    public List<CartItem> getCart() {
        if (checkLogin()) {
            return getUserCart();
        }
        return getNonUserCart();
    }

    public boolean saveCart(List<CartItem> cartItems) {
        if (checkLogin()) {
            return saveUserCart(cartItems);
        }
        return saveNonUserCart(cartItems);
    }

    // Use when user successfully login, move all cart item to login setting.
    public List<CartItem> getLoggedInCart() {
        // Check for cookie cart
        List<CartItem> result = getNonUserCart();
        if (result == null) {
            result = new ArrayList<CartItem>();
        }
        List<CartItem> userCart = getUserCart();

        for (CartItem ci : userCart) {
            Integer stock = 0;
            Integer status = 0;
            if (ci.getId().substring(0, 1).equals("a")) {
                Product p = productFacade.find(Integer.parseInt(ci.getId().substring(1)));
                stock = p.getStock();
                status = p.getStatus();
            } else {
                PreBuilt p = preBuiltFacade.find(Integer.parseInt(ci.getId().substring(1)));
                if (p.getStaff() != null) {
                    stock = p.getStock();
                } else {
                    List<Integer> stocks = new ArrayList<Integer>();
                    stocks.add(p.getCases().getStatus() == 1 ? p.getCases().getStock() : 0);
                    stocks.add(p.getCpu().getStatus() == 1 ? p.getCpu().getStock() : 0);
                    stocks.add(p.getMemory().getStatus() == 1 ? p.getMemory().getStock() : 0);
                    stocks.add(p.getMotherboard().getStatus() == 1 ? p.getMotherboard().getStock() : 0);
                    stocks.add(p.getStorage().getStatus() == 1 ? p.getStorage().getStock() : 0);
                    stocks.add(p.getPsu().getStatus() == 1 ? p.getPsu().getStock() : 0);

                    if (p.getVga() != null) {
                        stocks.add(p.getVga().getStatus() == 1 ? p.getVga().getStock() : 0);
                    }
                    if (p.getCpucooler() != null) {
                        stocks.add(p.getCpucooler().getStatus() == 1 ? p.getCpucooler().getStock() : 0);
                    }
                    if (p.getMonitor() != null) {
                        stocks.add(p.getMonitor().getStatus() == 1 ? p.getMonitor().getStock() : 0);
                    }
                    stock = stocks.get(0);
                    for (Integer num : stocks) {
                        if (num < stock) {
                            stock = num;
                        }
                    }
                }
            }

            boolean found = false;
            for (CartItem ci2 : result) {
                if (ci2.getId().equals(ci.getId())) {
                    found = true;
                    ci2.setQuan(ci2.getQuan() + ci.getQuan());
                    if (ci2.getQuan() > stock) {
                        ci2.setQuan(stock);
                    }
                    if (ci2.getId().substring(0, 1).equals("a") && status != 1) {
                        ci2.setQuan(0);
                    }
                    break;
                }
            }
            if (found) {
                continue;
            } else {
                if (ci.getQuan() > stock) {
                    ci.setQuan(stock);
                }
                if (ci.getId().substring(0, 1).equals("a") && status != 1) {
                    ci.setQuan(0);
                }
                result.add(ci);
            }
        }
        removeNoneUserSetting();
        saveUserCart(result);
        return result;
    }

    public List<CartItem> getUserCart() {
        List<CartItem> result = null;
        // Check for logged in user
        Customer customer = getLoggedInCustomer();
        if (customer != null) {
            if (userSettings == null) {

                userSettings = new ArrayList<UserSetting>();
                result = new ArrayList<CartItem>();
                userSettings.add(new UserSetting(customer.getId()));
            } else {
                boolean found = false;
                for (UserSetting us : userSettings) {
                    if (us.getCustomerId() == customer.getId()) {
                        found = true;
                        result = us.getCart();
                        if (result == null) {
                            result = new ArrayList<CartItem>();
                        }
                        us.setCart(result);
                        break;
                    }
                }
                if (found == false) {
                    result = new ArrayList<CartItem>();
                    userSettings.add(new UserSetting(customer.getId()));
                }
            }
        } else {
            result = new ArrayList<CartItem>();
        }
        return result;
    }

    public List<CartItem> getNonUserCart() {
        List<CartItem> result = new ArrayList<CartItem>();
        // Check for cookie
        String nonUserId = null;
        if (request.getCookies() != null) {
            nonUserId = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("JSESSIONID")).findFirst()
                    .map(Cookie::getValue).orElse(null);

            if (nonUserId != null) {
                if (userSettings == null) {
                    userSettings = new ArrayList<UserSetting>();
                    result = new ArrayList<CartItem>();
                    userSettings.add(new UserSetting(nonUserId));
                } else {
                    boolean found = false;
                    for (UserSetting us : userSettings) {
                        if (nonUserId.equals(us.getSessionId())) {
                            found = true;
                            result = us.getCart();
                            if (result == null) {
                                result = new ArrayList<CartItem>();
                            }
                            us.setCart(result);
                            break;
                        }
                    }
                    if (found == false) {
                        result = new ArrayList<CartItem>();
                        userSettings.add(new UserSetting(nonUserId));
                    }
                }
            } else {
                result = new ArrayList<CartItem>();
            }
        }
        return result;
    }

    public boolean saveUserCart(List<CartItem> cartItems) {
        Customer customer = getLoggedInCustomer();
        if (customer == null) {
            return false;
        }
        if (userSettings == null) {
            userSettings = new ArrayList<UserSetting>();
        }
        boolean found = false;
        for (UserSetting us : userSettings) {
            if (us.getCustomerId() == customer.getId()) {
                found = true;
                us.setCart(cartItems);
                break;
            }
        }
        if (!found) {
            UserSetting us = new UserSetting(customer.getId());
            us.setCart(cartItems);
            userSettings.add(us);
        }
        return true;
    }

    public boolean saveNonUserCart(List<CartItem> cartItems) {
        String nonUserId = null;
        if (request.getCookies() != null) {
            nonUserId = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("JSESSIONID")).findFirst()
                    .map(Cookie::getValue).orElse(null);
        } else {
            return false;
        }
        if (nonUserId == null) {
            return false;
        }
        boolean found = false;
        for (UserSetting us : userSettings) {
            if (nonUserId.equals(us.getSessionId())) {
                found = true;
                us.setCart(cartItems);
                break;
            }
        }
        if (!found) {
            UserSetting us = new UserSetting(nonUserId);
            us.setCart(cartItems);
            userSettings.add(us);
        }
        return true;
    }

    public boolean removeNoneUserSetting() {
        String nonUserId = null;
        if (request.getCookies() != null) {
            nonUserId = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("JSESSIONID")).findFirst()
                    .map(Cookie::getValue).orElse(null);
        } else {
            return false;
        }
        if (nonUserId == null) {
            return false;
        }
        if (userSettings != null) {
            Iterator<UserSetting> it = userSettings.iterator();
            while (it.hasNext()) {
                UserSetting us = it.next();
                if (nonUserId.equals(us.getSessionId())) {
                    it.remove();
                    break;
                }
            }
        }
        return true;
    }

    public Double getCartTotal() {
        Double result = 0d;
        List<CartItem> cartItems = getCart();
        if (cartItems != null && cartItems.size() > 0) {
            for (CartItem item : cartItems) {
                if (item.getId().substring(0, 1).equals("a")) {
                    result += (getDiscountPrice(productFacade.find(Integer.parseInt(item.getId().substring(1))))
                            * item.getQuan());
                } else {
                    result += (getPreBuiltDiscountPrice(
                            preBuiltFacade.find(Integer.parseInt(item.getId().substring(1)))) * item.getQuan());
                }

            }
        }
        return result;
    }

    public Double getcartItemPrice(String id, Integer quan) {
        Double result = 0d;
        if (id != null) {
            Product product = null;
            PreBuilt preBuilt = null;
            if (id.substring(0, 1).equals("a")) {
                product = productFacade.find(Integer.parseInt(id.substring(1)));
                if (product != null) {
                    result = (getDiscountPrice(product)) * quan;
                }else{
                    return result;
                }
            } else {
                preBuilt = preBuiltFacade.find(Integer.parseInt(id.substring(1)));
                if (preBuilt != null) {
                    result = (getPreBuiltDiscountPrice(preBuilt)) * quan;
                }
                else{
                    return result;
                }
            }
        }
        return result;
    }

    public Object getCartInfo(String id) {
        if (id.substring(0, 1).equals("a")) {
            return productFacade.find(Integer.parseInt(id.substring(1)));
        } else {
            return preBuiltFacade.find(Integer.parseInt(id.substring(1)));
        }

    }

    // =========== CUSTOMER FUNCTION ============

    public boolean checkLogin() {
        Boolean result = false;
        if (userSettings == null) {
            userSettings = new ArrayList<UserSetting>();
        }
        Customer customer = (Customer) session.getAttribute("loggedInCustomer");
        if (customer != null) {
            result = true;
            if (findUserSetting(customer.getId()) == null) {
                userSettings.add(new UserSetting(customer.getId()));
            }
        } else {
            if (request.getCookies() != null) {
                String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInCustomer"))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (cookie != null) {
                    customer = customerFacade.find(Integer.parseInt(cookie));
                    if (customer != null) {
                        session.setAttribute("loggedInCustomer", customer);
                        result = true;
                        if (findUserSetting(customer.getId()) == null) {
                            userSettings.add(new UserSetting(customer.getId()));
                        }
                    }
                }
            }

        }

        if (session.getAttribute("categories") == null) {
            session.setAttribute("categories", categoryFacade.findAll());
        }
        if (!result) {
            if (request.getCookies() != null) {
                String nonUserId = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("JSESSIONID"))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (nonUserId != null && findUserSetting(nonUserId) == null) {
                    userSettings.add(new UserSetting(nonUserId));
                }
            }

        }
        return result;
    }

    public Customer getLoggedInCustomer() {
        if (checkLogin()) {
            return (Customer) session.getAttribute("loggedInCustomer");
        }
        return null;
    }


    public UserSetting findUserSetting(Integer id) {
        for (UserSetting us : userSettings) {
            if (id.equals(us.getCustomerId())) {
                return us;
            }
        }
        return null;
    }

    public UserSetting findUserSetting(String sessionid) {
        for (UserSetting us : userSettings) {
            if (sessionid.equals(us.getSessionId())) {
                return us;
            }
        }
        return null;
    }

    


    // =========== PRICE MANAGEMENT ============

    public Double getDiscountPrice(Product product) {
        Double discount = 0d;
        if (product.getStatus() != 3) {
            // Discount price calculation
            Double oPrice = product.getPrice();
            // Collection<Promotion> productList = product.getPromotionCollection();
            // product's discount
            for (Promotion promo : product.getPromotionCollection()) {
                if (promo.getMaxQuantity() == null && promo.getMinQuantity() == null) {
                    if (promo.getLimitedQuantity() != null && promo.getQuantityLeft() == 0) {
                        continue;
                    }
                    if (promo.getPromotionDetail().getIsDisabled()) {
                        continue;
                    }
                    if (!(promo.getPromotionDetail().getIsAlways())
                            && (promo.getPromotionDetail().getEndDate().compareTo(new LocalDate().toDate()) < 0)) {
                        continue;
                    }
                    Double percentageDiscount = 0d;
                    Double exactDiscount = 0d;
                    if (promo.getPercentage() != null) {
                        percentageDiscount = oPrice * promo.getPercentage() / 100;
                        if (promo.getMaxSaleOff() != null && percentageDiscount > promo.getMaxSaleOff()) {
                            percentageDiscount = promo.getMaxSaleOff();
                        }
                    }
                    if (promo.getExactSaleOff() != null) {
                        exactDiscount = promo.getExactSaleOff();
                    }
                    discount += percentageDiscount + exactDiscount;
                }
            }

            // category's percentage discount
            // Init valid promotion list
            for (Promotion promo : product.getCategory().getPromotionCollection()) {
                if (promo.getMaxQuantity() == null && promo.getMinQuantity() == null) {
                    if (promo.getLimitedQuantity() != null && promo.getQuantityLeft() == 0) {
                        continue;
                    }
                    if (promo.getPromotionDetail().getIsDisabled()) {
                        continue;
                    }
                    if (!(promo.getPromotionDetail().getIsAlways())
                            && (promo.getPromotionDetail().getEndDate().compareTo(new LocalDate().toDate()) < 0)) {
                        continue;
                    }
                    Double percentageDiscount = 0d;
                    Double exactDiscount = 0d;
                    if (promo.getPercentage() != null) {
                        percentageDiscount = oPrice * promo.getPercentage() / 100;
                        if (promo.getMaxSaleOff() != null && percentageDiscount > promo.getMaxSaleOff()) {
                            percentageDiscount = promo.getMaxSaleOff();
                        }
                    }
                    if (promo.getExactSaleOff() != null) {
                        exactDiscount = promo.getExactSaleOff();
                    }
                    discount += percentageDiscount + exactDiscount;
                }
            }
        }
        return product.getPrice() - discount;
    }

    public Double getPreBuiltPrice(PreBuilt prebuilt) {
        Double result = 0d;
        if (prebuilt.getStaff() != null) {
            result = prebuilt.getPrice();
        } else {
            if (prebuilt.getCpu() != null) {
                result += getDiscountPrice(prebuilt.getCpu());
            }
            if (prebuilt.getMotherboard() != null) {
                result += getDiscountPrice(prebuilt.getMotherboard());
            }
            if (prebuilt.getVga() != null) {
                result += getDiscountPrice(prebuilt.getVga());
            }
            if (prebuilt.getMemory() != null) {
                result += getDiscountPrice(prebuilt.getMemory());
            }
            if (prebuilt.getPsu() != null) {
                result += getDiscountPrice(prebuilt.getPsu());
            }
            if (prebuilt.getStorage() != null) {
                result += getDiscountPrice(prebuilt.getStorage());
            }
            if (prebuilt.getCpucooler() != null) {
                result += getDiscountPrice(prebuilt.getCpucooler());
            }
            if (prebuilt.getCases() != null) {
                result += getDiscountPrice(prebuilt.getCases());
            }
            if (prebuilt.getMonitor() != null) {
                result += getDiscountPrice(prebuilt.getMonitor());
            }
        }

        return result;
    }

    public Double getPreBuiltDiscountPrice(PreBuilt prebuilt) {
        Double discount = 0d;
        List<Promotion> promoList = promotionFacade.findAll().stream().filter(p -> p.getPreBuiltTarget() != null)
                .collect(Collectors.toList());
        Double oPrice = getPreBuiltPrice(prebuilt);
        for (Promotion promo : promoList) {
            if (promo.getMaxQuantity() == null && promo.getMinQuantity() == null) {
                if (promo.getLimitedQuantity() != null && promo.getQuantityLeft() == 0) {
                    continue;
                }
                if (promo.getPromotionDetail().getIsDisabled()) {
                    continue;
                }
                if (!(promo.getPromotionDetail().getIsAlways())
                        && (promo.getPromotionDetail().getEndDate().compareTo(new LocalDate().toDate()) < 0)) {
                    continue;
                }
                if (promo.getPreBuiltTarget() == 1 && prebuilt.getStaff() == null) {
                    continue;
                }
                Double percentageDiscount = 0d;
                Double exactDiscount = 0d;
                if (promo.getPercentage() != null) {
                    percentageDiscount = oPrice * promo.getPercentage() / 100;
                    if (promo.getMaxSaleOff() != null && percentageDiscount > promo.getMaxSaleOff()) {
                        percentageDiscount = promo.getMaxSaleOff();
                    }
                }
                if (promo.getExactSaleOff() != null) {
                    exactDiscount = promo.getExactSaleOff();
                }
                discount += percentageDiscount + exactDiscount;
            }
        }
        return getPreBuiltPrice(prebuilt) - discount;
    }

    // =========== OTHER FUNCTION ============

    public Long getAverageRating(PreBuilt prebuilt) {
        Long result = 0L;
        Collection<PreBuiltRating> ratings = prebuilt.getPreBuiltRatingCollection();
        Integer size = ratings.size();
        if (size > 0) {
            double total = 0d;
            for (PreBuiltRating rate : ratings) {
                total += rate.getRating();
            }
            result = Math.round(total / Double.parseDouble(size.toString()));
        }
        return result;
    }

    public boolean verifyReCaptcha(String gRecaptchaResponse) {
        if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
            return false;
        }

        try {
            URL verifyUrl = new URL(SITE_VERIFY_URL);

            HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String postParams = "secret=" + recaptchaSecret + "&response=" + gRecaptchaResponse;

            conn.setDoOutput(true);

            OutputStream outStream = conn.getOutputStream();
            outStream.write(postParams.getBytes());

            outStream.flush();
            outStream.close();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode=" + responseCode);

            InputStream is = conn.getInputStream();

            JsonReader jsonReader = Json.createReader(is);
            JsonObject jsonObject = jsonReader.readObject();
            jsonReader.close();

            // ==> {"success": true}
            System.out.println("Response: " + jsonObject);

            boolean success = jsonObject.getBoolean("success");
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
