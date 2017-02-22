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

import cl.intelidata.jpa.PhonesByProduct;
import cl.intelidata.jpa.Producto;
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
public class NegocioPhonesByProduct {

    private static Logger logger = LoggerFactory.getLogger(NegocioPhonesByProduct.class);

    public List<PhonesByProduct> getData(int idCliente, Calendar date) {
        List<Producto> l = new ArrayList<>();
        List<PhonesByProduct> ll = new ArrayList<>();
        int idProducto = 0;
        try {
            l = getProductList(idCliente);

            for (Producto p : l) {
                idProducto = p.getId();
                break;
            }

            if (date == null) {
                date = Calendar.getInstance();
            }

            ll = data(idCliente, idProducto, date);
            if (ll.size() <= 0) {
                date.add(Calendar.MONTH, -1);
                return getData(idCliente, idProducto, date);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return ll;
    }

    public List<PhonesByProduct> getData(int idCliente, int idProducto, Calendar date) {
        List<Producto> l = new ArrayList<>();
        List<PhonesByProduct> ll = new ArrayList<>();
        try {
            if (idProducto <= 0) {
                l = getProductList(idCliente);

                for (Producto p : l) {
                    idProducto = p.getId();
                    break;
                }
            }

            if (date == null) {
                date = Calendar.getInstance();
            }

            ll = data(idCliente, idProducto, date);
            if (ll.size() <= 0) {
                date.add(Calendar.MONTH, -1);
                return getData(idCliente, idProducto, date);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return ll;
    }

    private List<PhonesByProduct> data(int idCliente, int idProducto, Calendar date) {
        List<PhonesByProduct> l = new ArrayList<>();
        EntityManager em = null;

        try {
            String query = "SELECT p.id AS id, t.fecha AS fecha, te.id_producto as id_producto, p.nombre AS nombre, te.numero AS numero, "
                    + "t.monto_total AS monto_total FROM telefono te\n"
                    + "INNER JOIN producto p ON te.id_producto = p.id\n"
                    + "INNER JOIN total t ON te.id = t.id_telefono\n"
                    + "WHERE te.id_cliente = " + idCliente + "\n"
                    + "AND te.id_producto = " + idProducto + "\n"
                    + "AND YEAR(t.fecha) = " + date.get(Calendar.YEAR) + " \n"
                    + "AND MONTH(t.fecha) = " + (date.get(Calendar.MONTH) + 1) + " \n"
                    + "ORDER BY t.monto_total DESC\n"
                    + "LIMIT 20";
            em = EntityHelper.getInstance().getEntityManager();            
            l = em.createNativeQuery(query, PhonesByProduct.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return l;
    }

    public List<Producto> getProductList(int idCliente) {
        List<Producto> p = new ArrayList<>();
        EntityManager em = null;

        try {
            String query = "SELECT p.id AS id, p.nombre AS nombre FROM producto p\n"
                    + "INNER JOIN telefono t ON  p.id = t.id_producto\n"
                    + "WHERE t.id_cliente = " + idCliente + "\n"
                    + "GROUP BY p.id;";
            em = EntityHelper.getInstance().getEntityManager();
            p = em.createNativeQuery(query, Producto.class).getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return p;
    }

}
