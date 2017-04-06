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

import cl.intelidata.jpa.Preguntas;
import cl.intelidata.negocio.NegocioContact;
import cl.intelidata.negocio.NegocioLogin;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.context.RequestContext;
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

    private List<Preguntas> questionList;

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

    public List<Preguntas> getQuestionList() throws Exception {
        NegocioContact nc = new NegocioContact();
        return nc.getQuestions();
    }

    public void setQuestionList(List<Preguntas> questionList) {
        this.questionList = questionList;
    }

    @PostConstruct
    public void init() {
        getQuestions();
    }

    public void getQuestions() {
        try {
            NegocioContact nc = new NegocioContact();
            setQuestionList(nc.getQuestions());
            html = nc.macroQuestions(nc.getQuestions(), loginbean.getClient().getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void save(ActionEvent actionEvent) throws Exception {
        FacesMessage msg = null;
        RequestContext context = RequestContext.getCurrentInstance();

        try {
            NegocioContact nc = new NegocioContact();
            HashMap<Integer, Integer> oldAnsw = nc.checkList(loginbean.getClient().getId());
            HashMap<Integer, Integer> answ = new HashMap<>();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            int value = 0;

            if (oldAnsw != null && !oldAnsw.isEmpty()) {
                for (Integer key : oldAnsw.keySet()) {
                    value = Integer.parseInt(ec.getRequestParameterMap().get("formContact:radio" + key));
                    answ.put(key, value);
                    System.out.println(key + " " + value);
                }
            } else {
                Map<String, String> params = ec.getRequestParameterMap();
                for (Map.Entry<String, String> p : params.entrySet()) {
                    if (p.getKey().startsWith("formContact:radio")) {
                        value = Integer.parseInt(ec.getRequestParameterMap().get(p.getKey()));
                        int key = Integer.parseInt(p.getKey().replace("formContact:radio", ""));
                        answ.put(key, value);
                        System.out.println(key + " " + value);
                    }
                }
            }

            if (answ == null || answ.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No ha contestado\ntodas las preguntas");
            } else if (!answ.isEmpty()) {
                NegocioLogin nl = new NegocioLogin();
                int total = nl.gotAnswers(loginbean.getClient());
                if (total == answ.size()) {
                    nc.save(oldAnsw, answ, loginbean.getClient().getId());
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", "Datos guardados");
                    // init();
                    context.addCallbackParam("save", true);
                    context.addCallbackParam("view", "dashboard.xhtml");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No ha contestado\ntodas las preguntas");
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al procesar");
            }

            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            context.addCallbackParam("save", false);
        } finally {
        }
    }

    public void fillList(ValueChangeEvent e) {
        //assign new value to localeCode
        logger.info(e.getNewValue().toString(), e.getSource());
    }
}
