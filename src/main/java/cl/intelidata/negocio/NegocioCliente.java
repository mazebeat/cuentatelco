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
package cl.intelidata.negocio;

import cl.intelidata.controllers.ClienteJpaController;
import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.Persona;
import cl.intelidata.utils.EntityHelper;

/**
 *
 * @author DFeliu
 */
public class NegocioCliente {

    private ClienteJpaController clientCtrl;
    private Cliente client;

    public NegocioCliente() {
        clientCtrl = new ClienteJpaController(EntityHelper.getInstance().getEntityManagerFactory());
        client = null;
    }

    public NegocioCliente(int idCliente) {
        clientCtrl = new ClienteJpaController(EntityHelper.getInstance().getEntityManagerFactory());
        client = clientCtrl.findCliente(idCliente);
    }

    public String companyName() {
        return client.getEmpresaId().getNombre();
    }

    public String companyName(int idClient) {
        return clientCtrl.findCliente(idClient).getEmpresaId().getNombre();
    }

    public String customerName() {
        Persona p = client.getPersonaId();
        return p.getNombre() + " " + p.getApellidos();
    }

    public String customerName(int idClient) {
        Persona p = clientCtrl.findCliente(idClient).getPersonaId();
        return p.getNombre() + " " + p.getApellidos();
    }

    public String customerEmail(int idClient) {
        return clientCtrl.findCliente(idClient).getPersonaId().getEmailPersonal();
    }

    public String address(int idClient) {
        return clientCtrl.findCliente(idClient).getPersonaId().getDireccionPersonal();
    }
    
    public String rut() {
        return client.getRut();
    }

    public String rut(int idClient) {
        return clientCtrl.findCliente(idClient).getRut();
    }
    
    public String celphone() {
        return client.getPersonaId().getCelularPersonal();
    }

    public String celphone(int idClient) {
        return clientCtrl.findCliente(idClient).getPersonaId().getCelularPersonal();
    }

    public Cliente findById(int idClient) {
        return clientCtrl.findCliente(idClient);
    }
    
    public Persona findPersonaByCliente(int idClient) {
        return clientCtrl.findCliente(idClient).getPersonaId();
    }
}
