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
package cl.intelidata.beans;

import cl.intelidata.negocio.NegocioConfiguration;
import cl.intelidata.services.ConfigurationService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.chart.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@SessionScoped
public class ConfigurationBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(ConfigurationBean.class);

    private int columns;
//    private static String view;
    private String label1, label2, dimension1, dimension2, view;
//    private static final List<ConfigurationService> configList = new ArrayList<>();
    private static List<ConfigurationService> configList = new ArrayList<>();
    private List<PieChartModel> chartList;
    private List<String> dimensions1, dimensions2;
    public FacesMessage msg = null;
    public static Map<String, List<ConfigurationService>> settingsChart = new HashMap<>();

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public List<ConfigurationService> getConfigList() {
        return configList;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public List<PieChartModel> getChartList() {
        return chartList;
    }

    public void setChartList(List<PieChartModel> chartList) {
        this.chartList = chartList;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<String> getDimensions1() {
        return dimensions1;
    }

    public void setDimensions1(List<String> dimensions1) {
        this.dimensions1 = dimensions1;
    }

    public List<String> getDimensions2() {
        return dimensions2;
    }

    public void setDimensions2(List<String> dimensions2) {
        this.dimensions2 = dimensions2;
    }

    public Map<String, List<ConfigurationService>> getSettingsChart() {
        return settingsChart;
    }

    public void setSettingsChart(Map<String, List<ConfigurationService>> settingsChart) {
        this.settingsChart = settingsChart;
    }

    @PostConstruct
    public void init() {
        try {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            view = NegocioConfiguration.cleanURI(req.getHeader("Referer"));

            if (settingsChart.isEmpty()) {
                settingsChart = NegocioConfiguration.getSettings(loginbean.getClient().getId());
            }

            if (!view.equals("")) {
                if (!settingsChart.isEmpty()) {
                    configList = settingsChart.get(view);
                }

                generateDimensions(view);
            } else {
                logger.warn("Variable 'view' no encontrada");
            }
            columns = 1;
            genCharts();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void generateDimensions(String view) {
        dimensions1 = new ArrayList<>();
        dimensions2 = new ArrayList<>();

        switch (view) {
            case "month_detail":
                dimensions1.add("t.monto_total");
                dimensions2.add("te.numero");
                dimensions2.add("p.id");
                break;
            case "monthly_evolution":
                break;
            case "historical_category":
                break;
            case "phones_product":
                break;
            default:
                System.out.println("cl.intelidata.beans.ConfigurationBean.init() CLASS");
                break;
        }
    }

    public void add() {
        try {
            if (configList.size() < 3) {
                ConfigurationService con = new ConfigurationService(label1, label2, dimension1, dimension2);
                configList.add(con);

                settingsChart.remove(view);
                settingsChart.put(view, configList);

                msg = new FacesMessage("Item Added", label1);
                RequestContext.getCurrentInstance().execute("PF('addConfigDlg').hide()");
                columns = configList.size();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "No more", null);
            }

            FacesContext.getCurrentInstance().addMessage(null, msg);
            // genCharts();            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void edit(RowEditEvent event) {
        try {
            msg = new FacesMessage("Item Edited", ((ConfigurationService) event.getObject()).getLabel1());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void cancel(RowEditEvent event) {
        try {
            msg = new FacesMessage("Item Cancelled");
            configList.remove((ConfigurationService) event.getObject());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void delete(ConfigurationService conf) {
        try {
            configList.remove(conf);

            settingsChart.remove(view);
            settingsChart.put(view, configList);

            msg = new FacesMessage("Item Deleted", conf.getLabel1());

            if (columns > 1) {
                columns = configList.size();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void genCharts() {
        try {
            if (!configList.isEmpty()) {
                chartList = new ArrayList<>();

                for (ConfigurationService cs : configList) {
                    PieChartModel pieModel = new PieChartModel();

                    pieModel.set(cs.getLabel1(), Math.random());
                    pieModel.set(cs.getLabel2(), Math.random());

                    pieModel.setTitle(cs.getDimension1() + "/" + cs.getDimension2());
                    pieModel.setLegendPosition("w");

                    chartList.add(pieModel);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static Map<String, List<ConfigurationService>> getSettings(int idCliente) {
        settingsChart = NegocioConfiguration.getSettings(idCliente);
        return settingsChart;
    }
}
