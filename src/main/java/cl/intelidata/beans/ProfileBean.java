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

import cl.intelidata.jpa.Persona;
import cl.intelidata.negocio.NegocioProfile;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@ViewScoped
public class ProfileBean implements Serializable {

    private static long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(ProfileBean.class);

    private String address;
    private String phone;
    private String celphone;
    private String email;
    private String twitter;
    private String facebook;
    private String skype;
    private Persona per;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    public ProfileBean() {
    }

    @PostConstruct
    public void init() {
        if (per == null) {
            load();
        }
    }

    public String load() {
        NegocioProfile np = new NegocioProfile();
        per = np.load(loginbean.getUser());

        if (per != null) {
            address = per.getDireccionPersonal();
            phone = per.getTelefonoFijoPersonal();
            celphone = per.getCelularPersonal();
            email = per.getEmailPersonal();
            facebook = per.getFacebook();
            skype = per.getSkype();
            twitter = per.getTwitter();
        }

        return "profile?faces-redirect=true";
    }

    public void save() {
        FacesMessage msg = null;

        try {
            NegocioProfile np = new NegocioProfile();
            per.setDireccionPersonal(address);
            per.setTelefonoFijoPersonal(phone);
            per.setCelularPersonal(celphone);
            per.setEmailPersonal(email);
            per.setFacebook(facebook);
            per.setTwitter(twitter);
            per.setSkype(skype);

            np.save(per);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@", "");
        } catch (Exception e) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar", "");
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCelphone() {
        return celphone;
    }

    public void setCelphone(String celphone) {
        this.celphone = celphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public Persona getPer() {
        return per;
    }

    public void setPer(Persona per) {
        this.per = per;
    }
}
