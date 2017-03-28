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

import cl.intelidata.jpa.Modelo;
import cl.intelidata.negocio.NegocioCatalog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class CatalogBean implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(CatalogBean.class);
    private List<Modelo> devicesList;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @PostConstruct
    public void init() {
        loadList();
    }

    private void loadList() {
        try {
            NegocioCatalog n = new NegocioCatalog();
            devicesList = n.getDevicesList();

            String[] i = new String[]{"/equipos/samsung galaxy s5/image.png", "/equipos/LG G Flex/image.png", "/equipos/nokia 220 white/image.png", "/equipos/samsung galaxy note 3/image.png", "/equipos/sony xperia C3 Selfie Pro/image.png"};
            // XXX: Delete when all the images are in the repository

            List<Modelo> devicesList2 = new ArrayList<>();
            for (Modelo modelo : devicesList) {
                String m = i[randomWithRange(0, i.length - 1)];
                modelo.setUrlImg(m);
                devicesList2.add(modelo);
            }

            devicesList = devicesList2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public List<Modelo> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(List<Modelo> devicesList) {
        this.devicesList = devicesList;
    }

    int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }
}
