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

import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.Persona;
import cl.intelidata.jpa.Usuarios;
import cl.intelidata.negocio.NegocioClient;
import cl.intelidata.negocio.NegocioLogin;
import cl.intelidata.utils.Hash;
import java.io.Serializable;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static Logger logger = LoggerFactory.getLogger(LoginBean.class);

    private Usuarios user;
    private Cliente client;
    private Persona person;
    private String username, password;
    private boolean loggedin = false;

    public boolean isLogged() {
        return loggedin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuarios getUser() {
        return user;
    }

    public void setUser(Usuarios user) {
        this.user = user;
    }

    public Cliente getClient() {
        return client;
    }

    public void setClient(Cliente client) {
        this.client = client;
    }

    public Persona getPerson() {
        return person;
    }

    public void setPerson(Persona person) {
        this.person = person;
    }

    public void login(ActionEvent actionEvent) {
        FacesMessage msg = null;
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            NegocioLogin nl = new NegocioLogin();
            user = nl.validLogin(username);

            if (user != null && Hash.checkPassword(password, user.getPassword())) {
                clientData();
                if (client == null || person == null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", "Debe registrarse primero");
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", username.toUpperCase());
                }
                loggedin = true;
            } else {
                loggedin = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Usuario o credenciales no válidas");
            }

            if (loggedin) {
                if (nl.gotRegister(person)) {
                    if (nl.gotAnswers(client)) {
                        context.addCallbackParam("view", "contact2.xhtml");
                    } else {
                        context.addCallbackParam("view", "dashboard.xhtml");
                    }
                } else {
                    context.addCallbackParam("view", "profile2.xhtml");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("isLogged", loggedin);
        }
    }

    public void clientData() {
        NegocioClient nc = new NegocioClient();
        client = nc.findById(user.getIdCliente());
        person = nc.findPersonaByCliente(user.getIdCliente());
    }

    public void logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        loggedin = false;
    }

    public void isAdmin(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        if (getUser() == null) {
            ConfigurableNavigationHandler nav = (ConfigurableNavigationHandler) fc.getApplication().getNavigationHandler();
            nav.performNavigation("access-denied");
        }
    }

}
