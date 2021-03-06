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
import cl.intelidata.negocio.NegocioSettings;
import cl.intelidata.utils.Utils;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@SessionScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(DashboardBean.class);
    private Map<String, List<Settings>> settingsChart;
    private Calendar date;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @ManagedProperty(value = "#{settingsBean}")
    private SettingsBean settingsBean;

    @PostConstruct
    public void init() {
        try {
            NegocioSettings ns = new NegocioSettings();
            settingsChart = new HashMap<>();
            settingsChart = ns.getSettings(loginbean.getClient());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @return
     */
    public String totalPay() {
        String total = "0";
        try {
            total = NumberFormat.getIntegerInstance().format(loginbean.getClient().getTotalServiciosList().get(0).getTotalAPagar());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return total;
    }

    public Map<String, List<Settings>> getSettingsChart() {
        return settingsChart;
    }

    public void setSettingsChart(Map<String, List<Settings>> settingsChart) {
        this.settingsChart = settingsChart;
    }

    public Calendar getDate() {
        Calendar c = GregorianCalendar.getInstance(TimeZone.getTimeZone("America/Santiago"));
        c.setTime(this.loginbean.getClient().getFechaVencimiento());
        return c;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public SettingsBean getSettingsBean() {
        return settingsBean;
    }

    public void setSettingsBean(SettingsBean settingsBean) {
        this.settingsBean = settingsBean;
    }

    public String getFormatDate() {
        return Utils.calendarToString(this.getDate());
    }
}
