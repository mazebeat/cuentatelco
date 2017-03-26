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
package cl.intelidata.services;

/**
 *
 * @author Dev-DFeliu
 */
public class ConfigurationService {

    private int idClient;
    private String label1, label2, dimension1, dimension2, view;

    public ConfigurationService() {
    }

    /**
     *
     * @param label1
     * @param label2
     * @param dimesion1
     * @param dimesion2
     */
    public ConfigurationService(String label1, String label2, String dimesion1, String dimesion2) {
        this.label1 = label1;
        this.label2 = label2;
        this.dimension1 = dimesion1;
        this.dimension2 = dimesion2;
    }

    /**
     *
     * @param label1
     * @param label2
     * @param dimesion1
     * @param dimesion2
     * @param view
     */
    public ConfigurationService(String label1, String label2, String dimesion1, String dimesion2, String view) {
        this.label1 = label1;
        this.label2 = label2;
        this.dimension1 = dimesion1;
        this.dimension2 = dimesion2;
        this.view = view;
    }

    /**
     *
     * @param label1
     * @param label2
     * @param dimesion1
     * @param dimesion2
     * @param view
     * @param idClient
     */
    public ConfigurationService(String label1, String label2, String dimesion1, String dimesion2, String view, int idClient) {
        this.label1 = label1;
        this.label2 = label2;
        this.dimension1 = dimesion1;
        this.dimension2 = dimesion2;
        this.view = view;
        this.idClient = idClient;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
