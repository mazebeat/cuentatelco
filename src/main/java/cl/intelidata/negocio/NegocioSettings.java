/*
 * Copyright (c) 2016, Intelidata S.A.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cl.intelidata.negocio;

import cl.intelidata.jpa.Modelo;
import cl.intelidata.jpa.Settings;
import cl.intelidata.utils.EntityHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioSettings {

    private static Logger logger = LoggerFactory.getLogger(NegocioSettings.class);

    /**
     *
     * @return @throws Exception
     */
    public List<Modelo> getDevicesList() throws Exception {
        EntityManager em = null;
        List<Modelo> m = new ArrayList<>();

        try {
            em = EntityHelper.getInstance().getEntityManager();
            m = em.createNamedQuery("Modelo.findAll", Modelo.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return m;
    }

    /**
     *
     * @param s
     * @return
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nf) {
            logger.info(nf.getMessage(), nf);
            return false;
        } catch (NullPointerException n) {
            logger.info(n.getMessage(), n);
            return false;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     *
     * @param view
     * @return
     */
    public static String cleanURI(String view) {
        String[] a = view.split("/");
        //return NegocioSettings.toTitleCase(a[a.length - 1].replace(".xhtml", ""));
        return a[a.length - 1].replace(".xhtml", "");
    }

    /**
     *
     * @param s
     * @return
     */
    public static String toTitleCase(String s) {
        String d = "";

        if (s.isEmpty()) {
            return s;
        }

        if (s.contains("_") || s.contains("-") || s.contains("/") || s.contains("\\.")) {
            String[] b = s.split("-|_|/|\\.");
            for (String c : b) {
                d += toTitleCase(c);
            }
        } else {
            d = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }

        return d;
    }

    /**
     *
     * @param idCliente
     * @return
     */
    public static Map<String, List<Settings>> getSettings(int idCliente) {
        Map<String, List<Settings>> set = new HashMap<>();

        try {
            // TODO: Cambiar por BBDD
            set.put("month_detail", monthlyDetailSettings().isEmpty() ? monthlyDetailDefaultSettings() : monthlyDetailSettings());
            set.put("monthly_evolution", monthlyEvolutionSettings().isEmpty() ? monthlyEvolutionDefaultSettings() : monthlyEvolutionSettings());
            set.put("historical_category", historicalCategorySettings().isEmpty() ? historicalCategoryDefaultSettings() : historicalCategorySettings());
            set.put("phones_product", phonesProductSettings().isEmpty() ? phonesProductDefaultSettings() : phonesProductSettings());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return set;
    }

    /**
     *
     * @return
     */
    public static Map<String, List<Settings>> defaultSettings() {
        Map<String, List<Settings>> set = new HashMap<>();

        try {
            // TODO: Cambiar por BBDD
            set.put("month_detail", monthlyDetailDefaultSettings());
            set.put("monthly_evolution", monthlyEvolutionDefaultSettings());
            set.put("historical_category", historicalCategoryDefaultSettings());
            set.put("phones_product", phonesProductDefaultSettings());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return set;
    }

    /**
     *
     * @return
     */
    public static List<Settings> monthlyDetailSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            // XXX: Change for DDBB
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Teléfono");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.numero");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> phonesProductSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            // XXX: Change for DDBB
            lcs.add(new Settings());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> historicalCategorySettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            // XXX: Change for DDBB
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Producto");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.id_producto");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> monthlyEvolutionSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            // XXX: Change for DDBB
            Settings c = new Settings();
            c.setLabel1("Periodo");
            c.setLabel2("Cliente");
            c.setDimension1("*");
            c.setDimension2("id");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    public static List<Settings> monthlyDetailDefaultSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Teléfono");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.numero");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> phonesProductDefaultSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            lcs.add(new Settings());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> historicalCategoryDefaultSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Producto");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.id_producto");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    private static List<Settings> monthlyEvolutionDefaultSettings() {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Periodo");
            c.setLabel2("Cliente");
            c.setDimension1("*");
            c.setDimension2("id");
            lcs.add(c);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @param map
     * @param view
     * @return
     */
    public static List<Settings> getSettingByView(Map<String, List<Settings>> map, String view) {
        return map.get(view);
    }
//
//    public List<Settings> dbSettingsByView(int idClient, String view) {
//        EntityManager em = null;
//        List<Settings> s = new ArrayList<>();
//
//        try {
//            em = EntityHelper.getInstance().getEntityManager();
//            m = em.createNamedQuery("", Modelo.class).getResultList();
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//            throw ex;
//        } finally {
//            if (em != null && em.isOpen()) {
//                em.close();
//            }
//        }
//
//        return m;
//    }
//
//    public List<Settings> dbSettings(int idClient) {
//        EntityManager em = null;
//        List<Settings> s = new ArrayList<>();
//
//        try {
//            em = EntityHelper.getInstance().getEntityManager();
//            m = em.createNamedQuery("", Modelo.class).getResultList();
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//            throw ex;
//        } finally {
//            if (em != null && em.isOpen()) {
//                em.close();
//            }
//        }
//
//        return m;
//    }
}
