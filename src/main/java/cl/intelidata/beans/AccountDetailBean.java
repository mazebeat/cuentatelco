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

import cl.intelidata.jpa.SaldosAnteriores;
import cl.intelidata.negocio.NegocioAccountDetail;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@ViewScoped
public class AccountDetailBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(AccountDetailBean.class);
    private HashMap<String, String> totalServices;
    private List<SaldosAnteriores> lastValues;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public HashMap<String, String> getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(HashMap<String, String> totalServices) {
        this.totalServices = totalServices;
    }

    public List<SaldosAnteriores> getLastValues() {
        return lastValues;
    }

    public void setLastValues(List<SaldosAnteriores> lastValues) {
        this.lastValues = lastValues;
    }

    public AccountDetailBean() {
    }

    @PostConstruct
    public void init() {
        fillTotalServices();
        fillLastValues();
    }

    public void fillTotalServices() {
        try {
            totalServices = new HashMap<>();
            totalServices.put("servContra", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getServiciosContratados()));
            totalServices.put("servConsum", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getServiciosConsumidos()));
            totalServices.put("servPcsNI", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getServiciosPcsNoIncluidos()));
            totalServices.put("servTerc", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getServiciosDeTerceros()));
            totalServices.put("cobdesc", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getCobrosYDescuentos()));
            totalServices.put("totalAf", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getTotalAfecto()));
            totalServices.put("iva", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getIva()));
            totalServices.put("montoB", "$" + NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getMontoBruto()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void fillLastValues() {
        try {
            NegocioAccountDetail n = new NegocioAccountDetail();
            lastValues = new ArrayList<>();
            lastValues = n.fillLastValues(loginbean.getClient().getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
