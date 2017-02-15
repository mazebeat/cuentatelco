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
package cl.intelidata.negocio;

import cl.intelidata.jpa.Preguntas;
import cl.intelidata.jpa.Telefono;
import cl.intelidata.utils.EntityHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioChart {

    private static Logger logger = LoggerFactory.getLogger(NegocioChart.class);

    public List<Telefono> postTelefonosConServicio(int idClient, Date date) {

        List<Telefono> altosGastos = new ArrayList<>();

        try {
            if (date == null) {
                date = new Date();
            }
            altosGastos = getAltosGastos(idClient);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return altosGastos;
    }

    public void postTelefonosConServicio(int idClient, Date date, String phone) {

        List<Telefono> altosGastos = new ArrayList<>();

        try {
            if (date == null) {
                date = new Date();
            }
            altosGastos = getAltosGastos(idClient);
            if (phone != null) {
            } else {
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private List<Telefono> getAltosGastos(int idClient) {
        List<Telefono> n = new ArrayList<>();
        EntityManager em = null;

        try {
            String query = "SELECT * FROM telefono te\n"
                    + "INNER JOIN total t ON te.id = t.id_telefono\n"
                    + "WHERE te.id_cliente = " + idClient + "\n"
                    + "ORDER BY t.monto_total DESC\n"
                    + "LIMIT 20;";

            em = EntityHelper.getInstance().getEntityManager();
            n = em.createNativeQuery(query, Telefono.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return n;
    }
}
