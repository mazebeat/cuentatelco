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

import cl.intelidata.jpa.ResumenAnualCliente;
import cl.intelidata.negocio.NegocioMonthlyEvolution;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@RequestScoped
public class MonthlyEvolutionBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(MonthlyEvolutionBean.class);

    private LineChartModel chart;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public LineChartModel getChart() {
        return chart;
    }

    public void setChart(LineChartModel chart) {
        this.chart = chart;
    }

    @PostConstruct
    public void init() {
        try {
            createLineModels();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void createLineModels() {
        try {
            chart = new LineChartModel();
            LineChartSeries series = new LineChartSeries();

            NegocioMonthlyEvolution m = new NegocioMonthlyEvolution();
            List<ResumenAnualCliente> n = m.getDataChart(loginbean.getClient().getId());

            series.setLabel(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int month = 0, year = 0;

            for (ResumenAnualCliente rac : n) {
                Calendar a = m.getMesValido(rac.getIdMes().getId());
                if (a.get(Calendar.MONTH) < month && month != 0) {
                    a.add(Calendar.YEAR, 1);
                    year = a.get(Calendar.YEAR);
                }

                if (year != 0) {
                    a.add(Calendar.YEAR, year - a.get(Calendar.YEAR));
                }

                month = a.get(Calendar.MONTH);
                series.set(dateFormat.format(a.getTime()), rac.getMonto().intValue());
            }

            chart.addSeries(series);
            chart.setZoom(true);
            chart.setAnimate(true);
            chart.getAxis(AxisType.Y).setLabel("Total");

            DateAxis axis = new DateAxis("Fechas");
            axis.setTickAngle(-50);
            axis.setTickFormat("%b %#d");

            chart.getAxes().put(AxisType.X, axis);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
