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

import cl.intelidata.jpa.ResumenAnualCliente;
import cl.intelidata.utils.EntityHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioMonthlyEvolution {

    private static Logger logger = LoggerFactory.getLogger(NegocioMonthlyEvolution.class);

    /**
     *
     * @param idCliente
     * @return
     */
    public List<ResumenAnualCliente> getDataChart(int idCliente, String groupby) {
        return dataChart(idCliente, groupby);
    }

    /**
     *
     * @param idCliente
     * @return
     */
    private List<ResumenAnualCliente> dataChart(int idCliente, String groupby) {
        List<ResumenAnualCliente> n = new ArrayList<>();
        EntityManager em = null;

        try {
            String query = "SELECT * FROM resumen_anual_cliente\n"
                    + "WHERE id_cliente = " + idCliente + "\n";
            if (!groupby.isEmpty()) {
                query += "GROUP BY " + groupby + "\n";
            }
            query += "ORDER BY id DESC;";

            em = EntityHelper.getInstance().getEntityManager();
            n = em.createNativeQuery(query, ResumenAnualCliente.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return n;
    }

    /**
     *
     * @param idMes
     * @return
     */
    public Calendar getMesValido(int idMes) {
        Calendar a = Calendar.getInstance();
        a.set(a.get(Calendar.YEAR), idMes - 1, 1, 0, 0);
        return a;
    }

}
