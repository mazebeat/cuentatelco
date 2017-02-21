package cl.intelidata.services;

import cl.intelidata.jpa.TelefonosServicios;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/*
 * Copyright (c) 2017, Intelidata S.A.
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
/**
 *
 * @author DFeliu
 */
@ManagedBean(name = "phoneDetailService")
@ApplicationScoped
public class PhoneDetailService {

    private String service;
    private double porcent;
    private BigInteger total;

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the porcent
     */
    public double getPorcent() {
        return porcent;
    }

    /**
     * @param porcent the porcent to set
     */
    public void setPorcent(double porcent) {
        this.porcent = porcent;
    }

    /**
     * @return the total
     */
    public BigInteger getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(BigInteger total) {
        this.total = total;
    }

    public PhoneDetailService() {
    }

    public PhoneDetailService(String service, double porcent, BigInteger total) {
        this.service = service;
        this.porcent = porcent;
        this.total = total;
    }

    public static List<PhoneDetailService> convertList(List<TelefonosServicios> list, int total) {
        List<PhoneDetailService> a = new ArrayList<>();
        for (TelefonosServicios ts : list) {
            a.add(new PhoneDetailService("Servicios Contratados", calculatePercentage(ts.getServiciosContratados(), total), ts.getServiciosContratados()));
            a.add(new PhoneDetailService("Servicios Consumidos", calculatePercentage(ts.getServiciosConsumidos(), total), ts.getServiciosConsumidos()));
            a.add(new PhoneDetailService("Servicios PCS no Incluidos", calculatePercentage(ts.getServiciosPcsNoIncluidos(), total), ts.getServiciosPcsNoIncluidos()));
            a.add(new PhoneDetailService("Servicios de Terceros", calculatePercentage(ts.getServiciosDeTerceros(), total), ts.getServiciosDeTerceros()));
            a.add(new PhoneDetailService("Cobros y Descuentos", calculatePercentage(ts.getCobrosYDescuentos(), total), ts.getCobrosYDescuentos()));
        }

        return a;
    }

    public static double calculatePercentage(BigInteger q, int total) {
        float proportion = ((float) q.intValue()) / ((float) total);
        return Math.round(proportion * 100 * 100) / 100D;
    }

}
