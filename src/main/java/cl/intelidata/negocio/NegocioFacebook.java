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

import cl.intelidata.controllers.TelegramUsuarioIntegracionJpaController;
import cl.intelidata.jpa.FacebookUsuarioIntegracion;
import cl.intelidata.jpa.TelegramUsuarioIntegracion;
import cl.intelidata.jpa.Usuarios;
import cl.intelidata.utils.EntityHelper;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioFacebook {

    private static Logger logger = LoggerFactory.getLogger(NegocioFacebook.class);

    public NegocioFacebook() {
    }

    /**
     * 
     * @param user
     * @return 
     */
    public int getCodigoIntegracion(Usuarios user) {
        int code = 0;

        try {
            code = getUserCode(user);

            if (code == 0) {
                code = genCodeIntegration();

                TelegramUsuarioIntegracionJpaController tctrl = new TelegramUsuarioIntegracionJpaController(EntityHelper.getInstance().getEntityManagerFactory());
                TelegramUsuarioIntegracion tui = new TelegramUsuarioIntegracion();
                tui.setIdUsuario(user);
                tui.setCodigo(code);
                tctrl.create(tui);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return code;
    }

    /**
     * 
     * @param user
     * @return
     * @throws Exception 
     */
    public int getUserCode(Usuarios user) throws Exception {
        EntityManager em = null;
        FacebookUsuarioIntegracion t;
        int code = 0;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            t = em.createNamedQuery("FacebookUsuarioIntegracion.GetUserCode", FacebookUsuarioIntegracion.class)
                    .setParameter("idUsuario", user)
                    .getSingleResult();
            code = t.getCodigo();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return code;
    }

    /**
     * 
     * @return 
     */
    private int genCodeIntegration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
