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

import cl.intelidata.jpa.Usuarios;
import cl.intelidata.negocio.NegocioTelegram;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dev-DFeliu
 */
@ManagedBean
@SessionScoped
public class TelegramBean implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(TelegramBean.class);
    private NegocioTelegram ctrl;
    private int code;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public NegocioTelegram getCtrl() {
        return ctrl;
    }

    public void setCtrl(NegocioTelegram ctrl) {
        this.ctrl = ctrl;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public TelegramBean() {
        this.ctrl = new NegocioTelegram();
    }

    @PostConstruct
    public void init() {
        System.out.println("Init" + TelegramBean.class);
    }

    public String load() {
        Usuarios user = loginbean.getUser();

        if (user != null) {
            setCode(getCtrl().getCodigoIntegracion(user));
        }

        return "telegram?faces-redirect=true";
    }

    /**
     * @return the loginbean
     */
    public LoginBean getLoginbean() {
        return loginbean;
    }

    /**
     * @param loginbean the loginbean to set
     */
    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }
}
