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

import cl.intelidata.negocio.NegocioContact;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@SessionScoped
public class ContactBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(ContactBean.class);

    private String html;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public ContactBean() {
    }

    @PostConstruct
    public void init() {
        getQuestions();
    }

    public void getQuestions() {
        try {
            NegocioContact nc = new NegocioContact();
            html = nc.macroQuestions(nc.getQuestions(), loginbean.getClient().getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void save() {
        try {
            int idClient = loginbean.getClient().getId();
            NegocioContact nc = new NegocioContact();
            HashMap<Integer, Integer> oldAnsw = nc.checkList(idClient);
            HashMap<Integer, Integer> answ = new HashMap<>();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

            if (oldAnsw != null && !oldAnsw.isEmpty()) {
                for (Integer key : oldAnsw.keySet()) {
                    int val = Integer.parseInt(ec.getRequestParameterMap().get("formContact:radio" + key));
                    answ.put(key, val);
                }
            } else {
                Map<String, String> params = ec.getRequestParameterMap();
                for (Map.Entry<String, String> p : params.entrySet()) {
                    String key = p.getKey();
                    String value = p.getValue();
                    System.out.println(key + " " + value);
                }
            }

            nc.save(oldAnsw, answ, idClient);
            init();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
