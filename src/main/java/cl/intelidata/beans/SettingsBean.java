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
package cl.intelidata.beans;

import cl.intelidata.controllers.GlosaJpaController;
import cl.intelidata.jpa.Cliente;
import cl.intelidata.jpa.Glosa;
import cl.intelidata.jpa.Settings;
import cl.intelidata.negocio.NegocioGlosa;
import cl.intelidata.negocio.NegocioSettings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author DFeliu
 */
@ManagedBean
@ViewScoped
public class SettingsBean implements Serializable {

    private static final long serialVersionUID = -2152389656664659476L;
    private static final Logger logger = LoggerFactory.getLogger(SettingsBean.class);
    public FacesMessage msg = null;

    private NegocioSettings ns;
    private int columns, idSetting;
    private String label1, label2, dimension1, dimension2, view;
    private List<Settings> configList;
    private Map<String, Object> dimensions1, dimensions2;
    public static Map<String, List<Settings>> settingsChart = new HashMap<>();
    private UploadedFile file;
    private File tempfile;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @PostConstruct
    public void init() {
        try {
            view = "dashboard";
            ns = new NegocioSettings();

            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            view = ns.cleanURI(req.getHeader("Referer"));

            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            if (params.containsKey("view")) {
                view = params.get("view");
            }

            if (!view.isEmpty()) {
                generateDimensions(view);

                if (settingsChart.isEmpty()) {
                    settingsChart = ns.getSettings(loginbean.getClient());
                }

                if (!settingsChart.isEmpty()) {
                    configList = (List<Settings>) settingsChart.get(view);
                }
            }
            columns = 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param view
     */
    private void generateDimensions(String view) {
        dimensions1 = new LinkedHashMap<>();
        dimensions2 = new LinkedHashMap<>();

        // XXX: Agregar carga por BBDD        
        // XXX: Add id into the list
        switch (view) {
            case "month_detail":
                dimensions1.put("Monto Total", "t.monto_total");
                dimensions1.put("Monto Total", "Monto Total");

                dimensions2.put("Teléfono", "te.numero");
                dimensions2.put("Servicio", "p.id");
                dimensions2.put("Centro de Costo", "Centro de Costo");
                dimensions2.put("Número", "Número");
                break;
            case "monthly_evolution":
                dimensions1.put("Todos", "*");

                dimensions2.put("", "id");
                break;
            case "historical_category":
                dimensions1.put("Monto Total", "t.monto_total");

                dimensions2.put("Producto", "te.id_producto");
                break;
            case "phones_product":
                // XXX: Add dimensions by Database
                dimensions1.put("Monto Total", "t.monto_total");

                dimensions2.put("Producto", "te.id_producto");
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    public void add() {
        try {
            configList = settingsChart.get(view);

            if (configList.isEmpty() || configList.size() < 4) {
                Settings con = new Settings();
                con.setLabel1(label1);
                con.setLabel2(label2);
                con.setDimension1(dimension1);
                con.setDimension2(dimension2);
                con.setView(view);
                con.setIdCliente(loginbean.getClient());
                configList.add(con);

                settingsChart.remove(view);
                settingsChart.put(view, configList);

                ns.save(con);

                msg = new FacesMessage("Item Added", label1);
                RequestContext.getCurrentInstance().execute("PF('addConfigDlg').hide()");
                columns = configList.size();
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "No more", null);
            }

            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param event
     */
    public void edit(RowEditEvent event) {
        try {
            Settings s = ((Settings) event.getObject());
            ns.edit(s);
            msg = new FacesMessage("Item Edited", s.getLabel1());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param event
     */
    public void cancel(RowEditEvent event) {
        try {
            Settings s = ((Settings) event.getObject());
            msg = new FacesMessage("Item Cancelled");
//            configList.remove((Settings) event.getObject());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     *
     * @param s
     */
    public void delete(Settings s) {
        try {
            configList.remove(s);
            settingsChart.remove(view);
            settingsChart.put(view, configList);

            ns.delete(s);

            msg = new FacesMessage("Item Deleted", s.getLabel1());

            if (columns > 1) {
                columns = configList.size();
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param client
     * @return
     */
    public Map<String, List<Settings>> getSettings(Cliente client) {
        settingsChart = ns.getSettings(client);
        return settingsChart;
    }

    /**
     *
     * @param view
     * @return
     */
    public List<Settings> getSettingByView(String view) {
        return ns.getSettingByView(settingsChart, view);
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        try {
            configList = settingsChart.get(view);
            copyFile(view + "_" + event.getFile().getFileName(), event.getFile().getInputstream());
            if (tempfile.exists()) {
                readFile(tempfile);
                msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is loaded.");
            } else {
                msg = new FacesMessage("Process Error", event.getFile().getFileName());
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param fileName
     * @param in
     */
    private void copyFile(String fileName, InputStream in) {
        try {
            if (view != null) {
                tempfile = File.createTempFile(fileName, "");
                logger.info("Temp file : " + tempfile.getAbsolutePath());

                try (OutputStream out = new FileOutputStream(tempfile)) {
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = in.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }

                    in.close();
                    out.flush();
                }

                logger.info("New file created! (" + tempfile.getAbsolutePath() + ")");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param file
     */
    private void readFile(File file) {
        try {
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {
                    Glosa g = new Glosa();
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();

                    if (row.getRowNum() == 0) {
                        List<String> titles = getHeaderFile(cellIterator);
                        if (!titles.isEmpty()) {
                            for (String title : titles) {
                                Settings s = new Settings();
                                s.setLabel1("Monto Total");
                                s.setLabel2(title);
                                s.setDimension1("Monto Total");
                                s.setDimension2(title);
                                s.setUploaded(new Short("1"));
                                s.setView(view);
                                s.setIdCliente(loginbean.getClient());

                                configList.add(s);
                                ns.save(s);
                            }
                        }
                    } else {
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            String value = getCellValue(cell);

                            switch (cell.getColumnIndex()) {
                                case 0:
                                    g.setTelephone(value);
                                    break;
                                case 1:
                                    g.setMontoTotal(Integer.parseInt(value));
                                    break;
                                case 2:
                                    g.setColumnaA(value);
                                    break;
                                case 3:
                                    g.setColumnaB(value);
                                    break;
                            }
                        }

                        if (row.getRowNum() != 0) {
                            NegocioGlosa ng = new NegocioGlosa();
                            g.setView(view);
                            g.setIdCliente(loginbean.getClient());
                            ng.save(g);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param cellIterator
     * @return
     */
    public List<String> getHeaderFile(Iterator<Cell> cellIterator) {
        List<String> result = new ArrayList<>();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String value = getCellValue(cell);
            result.add(value);
        }

        return result;
    }

    /**
     *
     * @param cell
     * @return
     */
    public String getCellValue(Cell cell) {
        String value = "";

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                logger.info(cell.getStringCellValue() + "\t");
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                logger.info(new Double(cell.getNumericCellValue()).intValue() + "\t");
                value = String.valueOf(new Double(cell.getNumericCellValue()).intValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                logger.info(cell.getBooleanCellValue() + "\t");
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                break;
        }

        return value;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public List<Settings> getConfigList() {
        return configList;
    }

    public void setConfigList(List<Settings> configList) {
        this.configList = configList;
    }

    public Map<String, Object> getDimensions1() {
        return dimensions1;
    }

    public void setDimensions1(Map<String, Object> dimensions1) {
        this.dimensions1 = dimensions1;
    }

    public Map<String, Object> getDimensions2() {
        return dimensions2;
    }

    public void setDimensions2(Map<String, Object> dimensions2) {
        this.dimensions2 = dimensions2;
    }

    public LoginBean getLoginbean() {
        return loginbean;
    }

    public void setLoginbean(LoginBean loginbean) {
        this.loginbean = loginbean;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public File getTempfile() {
        return tempfile;
    }

    public void setTempfile(File tempfile) {
        this.tempfile = tempfile;
    }

    public NegocioSettings getNs() {
        return ns;
    }

    public void setNs(NegocioSettings ns) {
        this.ns = ns;
    }
    
    public int getIdSetting() {
        return idSetting;
    }

    public void setIdSetting(int idSetting) {
        this.idSetting = idSetting;
    }
}
