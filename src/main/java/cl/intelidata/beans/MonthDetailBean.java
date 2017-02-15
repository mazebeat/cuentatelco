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

import cl.intelidata.jpa.Telefono;
import cl.intelidata.negocio.NegocioMonthDetail;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@ViewScoped
public class MonthDetailBean implements Serializable {
    
    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(MonthDetailBean.class);
    
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;
    
    private PieChartModel chart;
    
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
    
    public MonthDetailBean() {
    }
    
    @PostConstruct
    public void init() {
        try {
            createPieModel();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void createPieModel() {
        try {
            NegocioMonthDetail n = new NegocioMonthDetail();
            List<Telefono> data = n.getDataChart(loginbean.getClient().getId(), new Date());
            
            chart = new PieChartModel();
            
            for (Telefono telefono : data) {
                chart.set(telefono.getNumero(), telefono.getTotalList().get(0).getMontoTotal());
            }
            
            chart.setLegendPosition("s");
            chart.setShowDataLabels(true);
            chart.setMouseoverHighlight(true);
            chart.setLegendPlacement(LegendPlacement.INSIDE);
            chart.setLegendCols(5);
            chart.setLegendRows(4);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
}
