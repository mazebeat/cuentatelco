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

import cl.intelidata.jpa.HistoricalCategory;
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
public class NegocioHistoricalCategory {

    private static Logger logger = LoggerFactory.getLogger(NegocioHistoricalCategory.class);

    private List<HistoricalCategory> data(int idClient, Calendar date, String groupby) {
        List<HistoricalCategory> n = new ArrayList<>();
        EntityManager em = null;

        try {

            if (groupby.isEmpty()) {
                groupby = "te.id_producto";
            }

            String query = "SELECT p.id AS id, p.nombre AS name, SUM(t.monto_total) AS total FROM cliente c\n"
                    + "INNER JOIN telefono te ON c.id = te.id_cliente\n"
                    + "INNER JOIN total t ON te.id = t.id_telefono\n"
                    + "INNER JOIN producto p ON te.id_producto = p.id\n"
                    + "WHERE c.id = " + idClient + "\n"
                    + "AND YEAR(t.fecha) = " + date.get(Calendar.YEAR) + " \n"
                    + "AND MONTH(t.fecha) = " + (date.get(Calendar.MONTH) + 1) + " \n"
                    + "GROUP BY " + groupby + ";";

            em = EntityHelper.getInstance().getEntityManager();
            n = em.createNativeQuery(query, HistoricalCategory.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return n;
    }

    public List<HistoricalCategory> getData(Integer idCliente, Calendar date, String groupby) {
        List<HistoricalCategory> l = data(idCliente, date, groupby);

        if (l.size() <= 0) {
            date.add(Calendar.MONTH, -1);
            return getData(idCliente, date, groupby);
        }

        return l;
    }

}
