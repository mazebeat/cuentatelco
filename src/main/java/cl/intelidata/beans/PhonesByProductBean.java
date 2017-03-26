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

import cl.intelidata.jpa.PhonesByProduct;
import cl.intelidata.jpa.Producto;
import cl.intelidata.negocio.NegocioPhonesByProduct;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.PieChartModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@RequestScoped
public class PhonesByProductBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(PhonesByProductBean.class);

    private Calendar date;
    private String month;
    private int product;
    private PieChartModel chart;
    private List<Producto> productList;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;
    
    private int columns;
    private List<PieChartModel> chartList;

    @PostConstruct
    public void init() {
        try {
            fillProductList();
            createPieModel();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void createPieModel() {
        try {
            date = Calendar.getInstance();
            date.set(2015, 4, 1, 0, 0);
            chart = new PieChartModel();

            NegocioPhonesByProduct n = new NegocioPhonesByProduct();
            List<PhonesByProduct> l = n.getData(loginbean.getClient().getId(), product, date);

            for (PhonesByProduct pp : l) {
                chart.set(pp.getNumero(), pp.getMontoTotal());
            }

            chart.setLegendPosition("s");
            chart.setShowDataLabels(true);
            chart.setMouseoverHighlight(true);
            chart.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
            chart.setLegendCols(5);
            chart.setLegendRows(4);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void fillProductList() {
        try {
            productList = new ArrayList<>();
            NegocioPhonesByProduct n = new NegocioPhonesByProduct();
            productList = n.getProductList(loginbean.getClient().getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void onProductChange() {
        try {
            FacesMessage msg = new FacesMessage("Selected", Integer.toString(product));
            FacesContext.getCurrentInstance().addMessage(null, msg);

            if (product >= 0) {
                createPieModel();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getMonth() {
        this.month = new DateFormatSymbols().getMonths()[Calendar.getInstance().get(Calendar.MONTH)];
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getProduct() {
        return this.product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public List<Producto> getProductList() {
        return productList;
    }

    public void setProductList(List<Producto> productList) {
        this.productList = productList;
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

}
