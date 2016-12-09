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
import cl.intelidata.controllers.PersonaJpaController;
import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.Persona;
import cl.intelidata.jpa.Usuarios;
import cl.intelidata.utils.EntityHelper;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dev-DFeliu
 */
public class NegocioRegister {

    private static Logger logger = LoggerFactory.getLogger(NegocioRegister.class);

    public boolean validatePassword(String pass, String passconf) {
        return pass.equals(passconf);
    }

    public void register(String name, String lastname, String email, String rut, String password) {
        try {
            ClienteJpaController cctrl = new ClienteJpaController(EntityHelper.getInstance().getEntityManagerFactory());
            PersonaJpaController pctrl = new PersonaJpaController(EntityHelper.getInstance().getEntityManagerFactory());

            Persona p = new Persona();
            p.setNombre(name);
            p.setApellidos(lastname);
            p.setEmailPersonal(email);
            pctrl.create(p);

            int idClient = getIdClient(rut);
            Cliente cu = cctrl.findCliente(idClient);
            // TODO: Agregar hash a la password
            cu.setClave(password);
            cu.setPersonaId(p);
            cctrl.edit(cu);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public int getIdClient(String rut) throws Exception {
        EntityManager em = null;
        Cliente c;
        int id;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            c = em.createNamedQuery("Cliente.findByRut", Cliente.class)
                    .setParameter("rut", rut)
                    .getSingleResult();
            id = c.getId();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return id;
    }
}
