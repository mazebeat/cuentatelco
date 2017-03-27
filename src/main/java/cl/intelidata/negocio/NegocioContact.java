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
import cl.intelidata.controllers.ClientePreguntasJpaController;
import cl.intelidata.controllers.PreguntaRespuestaJpaController;
import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.ClientePreguntas;
import cl.intelidata.jpa.PreguntaRespuesta;
import cl.intelidata.jpa.Preguntas;
import cl.intelidata.utils.EntityHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DFeliu
 */
public class NegocioContact {

    private static Logger logger = LoggerFactory.getLogger(NegocioContact.class);

    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<Preguntas> getQuestions() throws Exception {
        EntityManager em = null;
        List<Preguntas> p = new ArrayList<>();

        try {
            em = EntityHelper.getInstance().getEntityManager();
            p = em.createNamedQuery("Preguntas.findByEstado", Preguntas.class)
                    .setParameter("estado", 'A')
                    .getResultList();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return p;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public int countQuestions() throws Exception {
        EntityManager em = null;
        List<Preguntas> p = new ArrayList<>();
        int count = 0;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            p = em.createNamedQuery("Preguntas.findByEstado", Preguntas.class)
                    .setParameter("estado", 'A')
                    .getResultList();

            count = p.size();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return count;
    }

    /**
     * 
     * @param lq
     * @param idClient
     * @return 
     */
    public String macroQuestions(List<Preguntas> lq, int idClient) {
        int cont = 1;
        String out = "";

        try {
            HashMap<Integer, Integer> userAnswers = checkList(idClient);

            for (Preguntas q : lq) {
                out += "<div class='radio-group'>";

                List<PreguntaRespuesta> questAnswers = q.getPreguntaRespuestaList();
                out += "<h4>" + cont++ + " - " + q.getPregunta() + "</h4>";

                for (PreguntaRespuesta qa : questAnswers) {
                    out += "<span class='choice'>";
                    out += "<input type='radio' name='formContact:radio" + q.getId() + "' value='" + qa.getIdRespuesta().getId() + "' " + isChecked(userAnswers, q.getId(), qa.getIdRespuesta().getId()) + " required='true'>";
                    out += "<label type='text' class=''>" + qa.getIdRespuesta().getRespuesta() + "</label></br>";
                    out += "</span>";
                }
                out += "<h:message for='formContact:radio" + q.getId() + "' style='color: red;' />";
                out += "</div>";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return out;
    }

    /**
     * 
     * @param idCliente
     * @return
     * @throws Exception 
     */
    public HashMap<Integer, Integer> checkList(int idCliente) throws Exception {
        EntityManager em = null;
        HashMap<Integer, Integer> cklist = new HashMap<Integer, Integer>();
        List<PreguntaRespuesta> qa = new ArrayList<>();

        try {
            String query = "SELECT * FROM pregunta_respuesta pr \n"
                    + "INNER JOIN cliente_preguntas cp ON cp.id_pregunta_respuesta = pr.id \n"
                    + "INNER JOIN cliente c ON c.id = cp.id_cliente \n"
                    + "WHERE c.id = " + idCliente + " \n"
                    + "AND cp.estado = 'A'";

            em = EntityHelper.getInstance().getEntityManager();
            qa = em.createNativeQuery(query, PreguntaRespuesta.class).getResultList();

            if (qa.isEmpty()) {
                return null;
            }

            for (int i = 0; i < qa.size(); i++) {
                PreguntaRespuesta o = qa.get(i);
                cklist.put(o.getIdPregunta().getId(), o.getIdRespuesta().getId());
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return cklist;
    }

    /**
     * 
     * @param userAnswers
     * @param idQuestion
     * @param idAnswer
     * @return 
     */
    private String isChecked(HashMap<Integer, Integer> userAnswers, int idQuestion, int idAnswer) {
        try {
            if (userAnswers != null && userAnswers.get(idQuestion) == idAnswer) {
                return "checked";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return "";
    }

    /**
     * 
     * @param oldAnsw
     * @param newAnsw
     * @param idClient 
     */
    public void save(HashMap<Integer, Integer> oldAnsw, HashMap<Integer, Integer> newAnsw, int idClient) {
        try {
            if (oldAnsw != null && oldAnsw.equals(newAnsw)) {
                return;
            }

            ClienteJpaController ctrl = new ClienteJpaController(EntityHelper.getInstance().getEntityManagerFactory());
            ClientePreguntasJpaController ctrl2 = new ClientePreguntasJpaController(EntityHelper.getInstance().getEntityManagerFactory());
            PreguntaRespuestaJpaController ctrl3 = new PreguntaRespuestaJpaController(EntityHelper.getInstance().getEntityManagerFactory());
            Cliente c = ctrl.findCliente(idClient);
            if (c != null) {
                List<ClientePreguntas> cp = c.getClientePreguntasList();

                for (ClientePreguntas clientePreguntas : cp) {
                    clientePreguntas.setEstado("B");
                    ctrl2.edit(clientePreguntas);
                }

                for (Map.Entry<Integer, Integer> answ : newAnsw.entrySet()) {
                    int key = answ.getKey();
                    int value = answ.getValue();

                    int idPr = getPreguntaRespuesta(key, value);

                    ClientePreguntas cn = new ClientePreguntas();
                    cn.setIdCliente(c);
                    cn.setIdPreguntaRespuesta(ctrl3.findPreguntaRespuesta(idPr));
                    cn.setCreatedAt(new Date());
                    cn.setEstado("A");
                    ctrl2.create(cn);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param idQuestion
     * @param idAnswer
     * @return 
     */
    public int getPreguntaRespuesta(int idQuestion, int idAnswer) {
        int id = 0;
        EntityManager em = null;

        try {
            em = EntityHelper.getInstance().getEntityManager();
            id = (int) em.createNativeQuery("SELECT id FROM pregunta_respuesta p WHERE p.id_pregunta = " + idQuestion + " AND p.id_respuesta = " + idAnswer)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return id;
    }
}
