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

import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.Persona;
import cl.intelidata.utils.EntityHelper;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioDashboard {

    private static Logger logger = LoggerFactory.getLogger(NegocioDashboard.class);

    public boolean isRegister(int id) throws Exception {
        EntityManager em = null;
        Cliente c;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            c = em.createNamedQuery("Cliente.isRegister", Cliente.class)
                    .setParameter("id", id)
                    .getSingleResult();
            if (c == null) {
                return false;
            }

            Persona p = c.getPersonaId();
            boolean getEmail = (p.getEmailPersonal() != null && !p.getEmailPersonal().isEmpty());
            boolean getAddress = (p.getDireccionPersonal() != null && !p.getDireccionPersonal().isEmpty());
            boolean getPhone = (p.getTelefonoFijoPersonal() != null && !p.getTelefonoFijoPersonal().isEmpty());
            boolean getCelphone = (p.getCelularPersonal() != null && !p.getCelularPersonal().isEmpty());
            boolean getFacebook = (p.getFacebook() != null && !p.getFacebook().isEmpty());
            boolean getTwitter = (p.getTwitter() != null && !p.getTwitter().isEmpty());
            boolean getSkype = (p.getSkype() != null && !p.getSkype().isEmpty());

            return getEmail && getAddress && getPhone && getCelphone && getFacebook && getTwitter && getSkype;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
