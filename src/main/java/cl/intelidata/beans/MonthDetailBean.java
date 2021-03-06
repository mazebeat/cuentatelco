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

import cl.intelidata.jpa.Settings;
import cl.intelidata.services.PhoneDetailService;
import cl.intelidata.jpa.Telefono;
import cl.intelidata.jpa.TelefonosServicios;
import cl.intelidata.negocio.NegocioMonthDetail;
import cl.intelidata.utils.Utils;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@RequestScoped
public class MonthDetailBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(MonthDetailBean.class);

    private String phone, sContratados, sConsumidos, sPcsnoincluidos, sTerceros, cobrosDesc;
    private Number price;
    private List<Telefono> phoneList;
    private List<PhoneDetailService> phoneDetail;
    private Calendar date;

    private int columns;
    private PieChartModel chart;
    private List<PieChartModel> chartList;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @ManagedProperty(value = "#{settingsBean}")
    private SettingsBean settingsBean;

    @PostConstruct
    public void init() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String dateInString = "2015-05-01";
            date = GregorianCalendar.getInstance(Utils.LOCAL_ES);
            date.setTime(formatter.parse(dateInString));

            createPieModel();
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    private void createPieModel() {
        try {
            List<Settings> configList = new ArrayList<>();
            configList = settingsBean.getSettingByView("month_detail");

            if (configList.size() > 0) {
                columns = 1;

                if (configList.size() > 1) {
                    columns = 2;
                }

                chartList = new ArrayList<>();
                NegocioMonthDetail n = new NegocioMonthDetail();

                for (Settings cs : configList) {
                    phoneList = n.getDataChart(loginbean.getClient().getId(), date, cs.getDimension2());

                    chart = new PieChartModel();

                    for (Telefono telefono : phoneList) {
                        chart.set(telefono.getNumero(), telefono.getTotalList().get(0).getMontoTotal());
                    }

                    chart.setTitle(cs.getLabel1() + "/" + cs.getLabel2());
                    chart.setLegendPosition("s");
                    chart.setShowDataLabels(true);
                    chart.setMouseoverHighlight(true);
//                    chart.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
                    chart.setLegendCols(5);
                    chart.setLegendRows(2);
                    chart.setExtender("chartExtender");
                    chartList.add(chart);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param event
     */
    public void itemSelect(ItemSelectEvent event) {
        try {
            LinkedHashMap<String, Number> m = (LinkedHashMap<String, Number>) chart.getData();
            int i = event.getItemIndex();
            phone = (String) getPhoneElement(m, i);
            price = (Number) getPriceElement(m, i);
            phoneDetail = PhoneDetailService.convertList(fillDatatable(phone), (int) price);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param map
     * @param index
     * @return
     */
    public Object getPriceElement(LinkedHashMap map, int index) {
        return map.get((map.keySet().toArray())[index]);
    }

    /**
     *
     * @param map
     * @param index
     * @return
     */
    public Object getPhoneElement(LinkedHashMap map, int index) {
        return map.keySet().toArray()[index];
    }

    /**
     *
     * @param phone
     * @return
     */
    private List<TelefonosServicios> fillDatatable(String phone) {
        if (date == null) {
            date = Calendar.getInstance();
        }
        NegocioMonthDetail n = new NegocioMonthDetail();
        return n.getDetail(phone, date);
    }

    public SettingsBean getSettingsBean() {
        return settingsBean;
    }

    public void setSettingsBean(SettingsBean settingsBean) {
        this.settingsBean = settingsBean;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getsContratados() {
        return sContratados;
    }

    public void setsContratados(String sContratados) {
        this.sContratados = sContratados;
    }

    public String getsConsumidos() {
        return sConsumidos;
    }

    public void setsConsumidos(String sConsumidos) {
        this.sConsumidos = sConsumidos;
    }

    public String getsPcsnoincluidos() {
        return sPcsnoincluidos;
    }

    public void setsPcsnoincluidos(String sPcsnoincluidos) {
        this.sPcsnoincluidos = sPcsnoincluidos;
    }

    public String getsTerceros() {
        return sTerceros;
    }

    public void setsTerceros(String sTerceros) {
        this.sTerceros = sTerceros;
    }

    public String getCobrosDesc() {
        return cobrosDesc;
    }

    public void setCobrosDesc(String cobrosDesc) {
        this.cobrosDesc = cobrosDesc;
    }

    public List<Telefono> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<Telefono> phoneList) {
        this.phoneList = phoneList;
    }

    public List<PhoneDetailService> getPhoneDetail() {
        return phoneDetail;
    }

    public void setPhoneDetail(List<PhoneDetailService> phoneDetail) {
        this.phoneDetail = phoneDetail;
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public PieChartModel getChart() {
        return chart;
    }

    public void setChart(PieChartModel chart) {
        this.chart = chart;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<PieChartModel> getChartList() {
        return chartList;
    }

    public void setChartList(List<PieChartModel> chartList) {
        this.chartList = chartList;
    }
}
