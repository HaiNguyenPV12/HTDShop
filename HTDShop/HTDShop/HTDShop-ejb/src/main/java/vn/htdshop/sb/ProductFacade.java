/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import vn.htdshop.entity.Product;

/**
 *
 * @author Hai
 */
@Stateless
public class ProductFacade extends AbstractFacade<Product> implements ProductFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductFacade() {
        super(Product.class);
    }

    @Override
    public List<Product> search(Integer cateId, String search) {
        TypedQuery<Product> query = null;
        if (cateId == 0) {
            query = em.createQuery("SELECT p FROM Product p WHERE p.name LIKE :search", Product.class);
        } else {
            query = em.createQuery("SELECT p FROM Product p WHERE p.category.id = :cateId AND p.name LIKE :search",
                    Product.class);
            query.setParameter("cateId", cateId);
        }
        query.setParameter("search", "%" + search + "%");
        return query.getResultList();
    }

    @Override
    public List<Product> search(Integer cateId, String search, Integer promoId) {
        TypedQuery<Product> query = null;
        String qStr = "SELECT p FROM Product p WHERE p.name LIKE :search";
        if (cateId != null && cateId > 0) {
            qStr += " AND p.category.id = :cateId";
        }
        if (promoId != null && promoId > 0) {
            qStr += " AND (p.promotionCollection.promotionDetail.id = :promoId OR p.category.promotionCollection.promotionDetail.id = :promoId)";
        }
        query = em.createQuery(qStr, Product.class);

        query.setParameter("search", "%" + search + "%");
        if (cateId != null && cateId > 0) {
            query.setParameter("cateId", cateId);
        }
        if (promoId != null && promoId > 0) {
            query.setParameter("promoId", promoId);
        }
        return query.getResultList();
    }

    @Override
    public List<String> getStringList(String attr) {
        Query q = null;
        if (attr == null) {
            return null;
        }
        if (attr.equals("socketCpu")) {
            q = em.createNativeQuery("SELECT DISTINCT CAST(Socket AS VARCHAR(MAX)) FROM Product WHERE CateId = 1");
        } else if (attr.equals("series") || attr.equals("seriesCpu")) {
            q = em.createNativeQuery("SELECT DISTINCT Series FROM Product WHERE CateId = 1");
        } else if (attr.equals("manu") || attr.equals("manuAll")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product");
        } else if (attr.equals("manuCpu")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 1");
        } else if (attr.equals("manuMotherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 2");
        } else if (attr.equals("manuGpu")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 3");
        } else if (attr.equals("manuMemory")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 4");
        } else if (attr.equals("manuPsu")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 5");
        } else if (attr.equals("manuStorage")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 6");
        } else if (attr.equals("manuCpuCooler")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 7");
        } else if (attr.equals("manuCase")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 8");
        } else if (attr.equals("manuMonitor")) {
            q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = 9");
        } else if (attr.equals("socketMotherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT CAST(Socket AS VARCHAR(MAX)) FROM Product WHERE CateId = 2");
        } else if (attr.equals("chipsetMotherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT Chipset FROM Product WHERE CateId = 2");
        } else if (attr.equals("memoryTypeMotherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT MemoryType FROM Product WHERE CateId = 2");
        } else if (attr.equals("memoryTypeGpu")) {
            q = em.createNativeQuery("SELECT DISTINCT MemoryType FROM Product WHERE CateId = 3");
        } else if (attr.equals("memoryTypeMemory")) {
            q = em.createNativeQuery("SELECT DISTINCT MemoryType FROM Product WHERE CateId = 4");
        } else if (attr.equals("formFactorMotherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT FormFactor FROM Product WHERE CateId = 2");
        } else if (attr.equals("psuFormFactorPsu") || attr.equals("pSUFormFactorPsu")) {
            q = em.createNativeQuery("SELECT DISTINCT PSUFormFactor FROM Product WHERE CateId = 5");
        } else if (attr.equals("formFactorStorage")) {
            q = em.createNativeQuery("SELECT DISTINCT FormFactor FROM Product WHERE CateId = 6");
        } else if (attr.equals("formFactorCase")) {
            q = em.createNativeQuery("SELECT DISTINCT FormFactor FROM Product WHERE CateId = 8");
        } else if (attr.equals("psuFormFactorCase") || attr.equals("pSUFormFactorCase")) {
            q = em.createNativeQuery("SELECT DISTINCT PSUFormFactor FROM Product WHERE CateId = 8");
        } else if (attr.equals("chipsetGpu")) {
            q = em.createNativeQuery("SELECT DISTINCT Chipset FROM Product WHERE CateId = 3");
        } else if (attr.equals("interfaceGpu") || attr.equals("interface1Gpu")) {
            q = em.createNativeQuery("SELECT DISTINCT Interface FROM Product WHERE CateId = 3");
        } else if (attr.equals("interfaceStorage") || attr.equals("interface1Storage")) {
            q = em.createNativeQuery("SELECT DISTINCT Interface FROM Product WHERE CateId = 6");
        } else if (attr.equals("storageTypeStorage")) {
            q = em.createNativeQuery("SELECT DISTINCT StorageType FROM Product WHERE CateId = 6");
        } else if (attr.equals("resolutionMonitor")) {
            q = em.createNativeQuery("SELECT DISTINCT Resolution FROM Product WHERE CateId = 9");
        } else if (attr.equals("memoryMemory") || attr.equals("capacityMemory")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(Memory AS VARCHAR) AS Memory FROM Product WHERE CateId = 4) As Mem ORDER BY CAST(Memory As INT)");
        } else if (attr.equals("memoryModulesMemory")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(MemoryModules AS VARCHAR) AS MemoryModules FROM Product WHERE CateId = 4) As MemoryModules ORDER BY CAST(MemoryModules As INT)");
        } else if (attr.equals("interfaceMemory") || attr.equals("interface1Memory")) {
            q = em.createNativeQuery("SELECT DISTINCT Interface FROM Product WHERE CateId = 4 AND Interface != 'NULL'");
        } else if (attr.equals("interfaceMotherboard") || attr.equals("interface1Motherboard")) {
            q = em.createNativeQuery("SELECT DISTINCT Interface FROM Product WHERE CateId = 2 AND Interface != 'NULL'");
        } else if (attr.equals("coreCpu")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(Core AS VARCHAR) AS Core FROM Product WHERE CateId = 1) As Core ORDER BY CAST(Core As INT)");
        } else if (attr.equals("threadCpu")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(Thread AS VARCHAR) AS Thread FROM Product WHERE CateId = 1) As Thread ORDER BY CAST(Thread As INT)");
        } else if (attr.equals("warrantyPeriod")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(WarrantyPeriod AS VARCHAR) AS WarrantyPeriod FROM Product) As WarrantyPeriod ORDER BY CAST(WarrantyPeriod As INT)");
        } else if (attr.equals("memorySlotMotherboard")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(MemorySlot AS VARCHAR) AS MemorySlot FROM Product WHERE CateId = 2) As MemorySlot ORDER BY CAST(MemorySlot As INT)");
        } else if (attr.equals("memoryGpu")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(Memory AS VARCHAR) AS Memory FROM Product WHERE CateId = 3) As Memory ORDER BY CAST(Memory As INT)");
        } else if (attr.equals("memoryStorage")) {
            q = em.createNativeQuery(
                    "SELECT * FROM (SELECT DISTINCT CAST(Memory AS VARCHAR) AS Memory FROM Product WHERE CateId = 6) As Memory ORDER BY CAST(Memory As INT)");
        } else {
            return null;
        }

        if (q.getResultList().size() == 0) {
            return new ArrayList<String>();
        }
        return (List<String>) q.getResultList();
    }

    @Override
    public List<Integer> getIntegerList(String attr) {
        TypedQuery<Integer> q = null;
        if (attr.equals("core")) {
            q = em.createQuery("SELECT DISTINCT p.core FROM Product p WHERE p.category.id = 1", Integer.class);
        } else if (attr.equals("thread")) {
            q = em.createQuery("SELECT DISTINCT p.thread FROM Product p WHERE p.category.id = 1", Integer.class);
        } else if (attr.equals("memoryModuleMemory")) {
            q = em.createQuery("SELECT DISTINCT p.memoryModules FROM Product p WHERE p.category.id = 4", Integer.class);
        } else {
            return null;
        }
        return q.getResultList();
    }

    @Override
    public List<Double> getDoubleList(String attr) {
        TypedQuery<Double> q = null;
        if (attr.equals("screenSizeMonitor")) {
            q = em.createQuery("SELECT DISTINCT p.screenSize FROM Product p WHERE p.category.id = 9", Double.class);
        } else {
            return null;
        }
        return q.getResultList();
    }

    @Override
    public Product find(Integer id) {
        // TODO Auto-generated method stub
        return em.find(Product.class, id);
    }

    @Override
    public List<String> getStringList(String attr, String options) {
        Query q = null;
        String qStr = "";
        if (attr.equals("socketCpu")) {
            qStr = "SELECT DISTINCT CAST(Socket AS VARCHAR(MAX)) FROM Product WHERE CateId = 1";
        } else if (attr.equals("series") || attr.equals("seriesCpu")) {
            qStr = "SELECT DISTINCT Series FROM Product WHERE CateId = 1";
        } else {
            return null;
        }

        StringTokenizer token = new StringTokenizer(options, ",");
        while (token.hasMoreTokens()) {
            String a = token.nextToken();
            if (a != null && !a.isEmpty()) {
                String option = a.substring(0, a.indexOf(":"));
                String value = a.substring(a.indexOf(":") + 1, a.length());
                if (option != null && !option.isEmpty() && value != null && !value.isEmpty()) {
                    if (option.equals("Socket")) {
                        qStr += " AND CAST(Socket AS VARCHAR(MAX)) = '" + value + "'";
                    } else {
                        qStr += " AND " + option + " = '" + value + "'";
                    }
                }
            }
        }
        q = em.createNativeQuery(qStr);

        if (q.getResultList().size() == 0) {
            return new ArrayList<String>();
        }
        return (List<String>) q.getResultList();
    }

    @Override
    public List<String> getManuOtherList(String cateId) {
        if (null == cateId) {
            return new ArrayList<String>();
        }
        Query q = em.createNativeQuery("SELECT DISTINCT Manufacturer FROM Product WHERE CateId = " + cateId);
        if (q.getResultList().size() == 0) {
            return new ArrayList<String>();
        }
        return (List<String>) q.getResultList();
    }

}
