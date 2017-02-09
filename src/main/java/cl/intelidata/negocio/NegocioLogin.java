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
import cl.intelidata.jpa.Preguntas;
import cl.intelidata.jpa.Usuarios;
import cl.intelidata.utils.EntityHelper;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioLogin {

    private static Logger logger = LoggerFactory.getLogger(NegocioLogin.class);
    private Usuarios user;

    public Usuarios validLogin(String username) throws Exception {
        EntityManager em = null;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            user = em.createNamedQuery("Usuarios.validLogin", Usuarios.class)
                    .setParameter("username", username)
                    .getSingleResult();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return user;
    }

    public int gotAnswers(Cliente client) {
        EntityManager em = null;
        long answers = 0;
        String query = "SELECT count(id) FROM preguntas \n"
                + "WHERE id NOT IN (\n"
                + "\tSELECT id_pregunta FROM pregunta_respuesta pr\n"
                + "\tJOIN cliente_preguntas cp ON pr.id = cp.id_pregunta_respuesta\n"
                + "\tWHERE cp.id_cliente = " + client.getId()
                + ")\n"
                + "AND estado = 'A'";

        try {
            em = EntityHelper.getInstance().getEntityManager();
            answers = (long) em.createNativeQuery(query).getSingleResult();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return (int) answers;
    }

    public boolean gotRegister(Persona person) {
        boolean pass = false;
        boolean a, b, c, d, e, f, g;

        try {
            if (person != null) {
                a = (person.getEmailPersonal() != null && !person.getEmailPersonal().trim().equals(""));
                b = (person.getDireccionPersonal() != null && !person.getDireccionPersonal().trim().equals(""));
                c = (person.getTelefonoFijoPersonal() != null && !person.getTelefonoFijoPersonal().trim().equals(""));
                d = (person.getCelularPersonal() != null && !person.getCelularPersonal().trim().equals(""));
                e = (person.getFacebook() != null && !person.getFacebook().trim().equals(""));
                f = (person.getTwitter() != null && !person.getTwitter().trim().equals(""));
                g = (person.getSkype() != null && !person.getSkype().trim().equals(""));

                if (a || b || c || d || e || f || g) {
                    pass = true;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return pass;
    }

}
