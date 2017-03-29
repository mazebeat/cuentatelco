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
import cl.intelidata.jpa.Telefono;
import cl.intelidata.negocio.NegocioMonthDetail;
import cl.intelidata.utils.Utils;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@RequestScoped
public class MonthlyDetailBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(MonthlyDetailBean.class);

    private Calendar date;
    private PieChartModel chart;
    private int columns;

    private List<PieChartModel> chartList;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @ManagedProperty(value = "#{settingsBean}")
    private SettingsBean settingsBean;

    @PostConstruct
    public void init() {
        try {
            createModels();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Create line chart list from the user settings
     */
    private void createModels() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            String dateInString = "2015-05-01";
            date = GregorianCalendar.getInstance(Utils.LOCAL_ES);
            date.setTime(formatter.parse(dateInString));

            columns = 1;
            NegocioMonthDetail n = new NegocioMonthDetail();

            chart = new PieChartModel();
            for (int i = 0; i < 10; i++) {
                chart.set("numero " + i, Math.random() * 1000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    public SettingsBean getSettingsBean() {
        return settingsBean;
    }

    public void setSettingsBean(SettingsBean settingsBean) {
        this.settingsBean = settingsBean;
    }

    /**
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

}
