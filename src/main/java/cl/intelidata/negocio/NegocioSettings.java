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

import cl.intelidata.controllers.SettingsJpaController;
import cl.intelidata.jpa.Cliente;
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
    private SettingsJpaController ctrl;

    /**
     *
     * @param set
     * @throws Exception
     */
    public void saveSettings(Settings set) throws Exception {
        EntityManager em = null;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            em.getTransaction().begin();

            em.persist(set);
            em.flush();

            Settings st = em.find(Settings.class, set.getId());
            logger.info("Save correctly", st);

            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

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
     * @param view
     * @return
     */
    public String cleanURI(String view) {
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
     * @param client
     * @return
     */
    public Map<String, List<Settings>> getSettings(Cliente client) {
        Map<String, List<Settings>> set = new HashMap<>();

        try {
            set.put("month_detail", monthlyDetailSettings(client, "month_detail"));
            set.put("monthly_evolution", monthlyEvolutionSettings(client, "monthly_evolution"));
            set.put("historical_category", historicalCategorySettings(client, "historical_category"));
            set.put("phones_product", phonesProductSettings(client, "phones_product"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return set;
    }

    /**
     *
     * @return
     */
    public Map<String, List<Settings>> defaultSettings(Cliente client) {
        Map<String, List<Settings>> set = new HashMap<>();

        try {
            // TODO: Cambiar por BBDD
            set.put("month_detail", monthlyDetailDefaultSettings(client, "month_detail"));
            set.put("monthly_evolution", monthlyEvolutionDefaultSettings(client, "monthly_evolution"));
            set.put("historical_category", historicalCategoryDefaultSettings(client, "historical_category"));
            set.put("phones_product", phonesProductDefaultSettings(client, "phones_product"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return set;
    }

    /**
     *
     * @param client
     * @return
     */
    public List<Settings> monthlyDetailSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            lcs = dbSettingsByView(client, "month_detail");

            if (lcs.isEmpty()) {
                lcs = monthlyDetailDefaultSettings(client, view);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @param client
     * @return
     */
    private List<Settings> monthlyEvolutionSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            lcs = dbSettingsByView(client, "monthly_evolution");
            if (lcs.isEmpty()) {
                lcs = monthlyEvolutionDefaultSettings(client, view);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @param client
     * @return
     */
    private List<Settings> phonesProductSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            lcs = dbSettingsByView(client, view);

            if (lcs.isEmpty()) {
                lcs = phonesProductDefaultSettings(client, view);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @param client
     * @return
     */
    private List<Settings> historicalCategorySettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            lcs = dbSettingsByView(client, view);

            if (lcs.isEmpty()) {
                lcs = historicalCategoryDefaultSettings(client, view);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return lcs;
    }

    /**
     *
     * @return
     */
    public List<Settings> monthlyDetailDefaultSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Teléfono");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.numero");
            c.setIdCliente(client);
            c.setView(view);
            save(c);
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
    private List<Settings> phonesProductDefaultSettings(Cliente cliente, String view) {
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
    private List<Settings> historicalCategoryDefaultSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Monto Total");
            c.setLabel2("Producto");
            c.setDimension1("t.monto_total");
            c.setDimension2("te.id_producto");
            c.setIdCliente(client);
            c.setView(view);
            save(c);
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
    private List<Settings> monthlyEvolutionDefaultSettings(Cliente client, String view) {
        List<Settings> lcs = new ArrayList<>();

        try {
            Settings c = new Settings();
            c.setLabel1("Periodo");
            c.setLabel2("Cliente");
            c.setDimension1("*");
            c.setDimension2("id");
            c.setIdCliente(client);
            c.setView(view);
            save(c);
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
    public List<Settings> getSettingByView(Map<String, List<Settings>> map, String view) {
        return map.get(view);
    }

    /**
     *
     * @param client
     * @param view
     * @return
     */
    private List<Settings> dbSettingsByView(Cliente client, String view) {
        EntityManager em = null;
        List<Settings> s = new ArrayList<>();

        try {
            em = EntityHelper.getInstance().getEntityManager();
            s = em.createNamedQuery("Settings.findByIdClienteView", Settings.class)
                    .setParameter("view", view)
                    .setParameter("idCliente", client)
                    .getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return s;
    }

    /**
     *
     * @param client
     * @return
     */
    private List<Settings> dbSettings(Cliente client) {
        EntityManager em = null;
        List<Settings> s = new ArrayList<>();

        try {
            em = EntityHelper.getInstance().getEntityManager();
            s = em.createNamedQuery("Settings.findByIdCliente", Settings.class)
                    .setParameter("idCliente", client)
                    .getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return s;
    }

    /**
     *
     * @param s
     * @throws Exception
     */
    public void delete(Settings s) throws Exception {
        try {
            ctrl = new SettingsJpaController(EntityHelper.getInstance().getEntityManagerFactory());

            if (s != null) {
                ctrl.destroy(s.getId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param s
     * @throws Exception
     */
    public void save(Settings s) throws Exception {
        try {
            ctrl = new SettingsJpaController(EntityHelper.getInstance().getEntityManagerFactory());

            if (s != null) {
                ctrl.create(s);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param s
     * @throws Exception
     */
    public void edit(Settings s) throws Exception {
        try {
            ctrl = new SettingsJpaController(EntityHelper.getInstance().getEntityManagerFactory());

            if (s != null) {
                ctrl.edit(s);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
