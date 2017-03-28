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

import cl.intelidata.jpa.Settings;
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
    private static Logger logger = LoggerFactory.getLogger(SettingsBean.class);
    public FacesMessage msg = null;

    private int columns;
    private String label1, label2, dimension1, dimension2, view;
    private List<Settings> configList;
    private List<String> dimensions1, dimensions2;
    public static Map<String, List<Settings>> settingsChart = new HashMap<>();
    private UploadedFile file;
    private File tempfile;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginbean;

    @PostConstruct
    public void init() {
        try {
            configList = new ArrayList();

            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            view = NegocioSettings.cleanURI(req.getHeader("Referer"));

            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            if (view.isEmpty() && params.containsKey("view")) {
                view = params.get("view");
            }

            if (settingsChart.isEmpty()) {
                settingsChart = NegocioSettings.getSettings(loginbean.getClient().getId());
            }

            if (!view.equals("")) {
                generateDimensions(view);

                if (!settingsChart.isEmpty()) {
                    configList = settingsChart.get(view);
                }
            } else {
                logger.info("Variable 'view' no encontrada");
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
        dimensions1 = new ArrayList<>();
        dimensions2 = new ArrayList<>();

        switch (view) {
            case "month_detail":
                dimensions1.add("t.monto_total");

                dimensions2.add("te.numero");
                dimensions2.add("p.id");
                break;
            case "monthly_evolution":
                dimensions1.add("*");

                dimensions2.add("id");
                break;
            case "historical_category":
                dimensions1.add("t.monto_total");
                dimensions2.add("te.id_producto");
                break;
            case "phones_product":
                // XXX: Add dimensions
                break;
            default:
//                System.out.println("cl.intelidata.beans.SettingsBean.init() CLASS");
                break;
        }
    }

    /**
     *
     */
    public void add() {
        try {
            configList = settingsChart.get(view);

            if (!configList.isEmpty() && configList.size() < 4) {
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
            msg = new FacesMessage("Item Edited", ((Settings) event.getObject()).getLabel1());
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
            msg = new FacesMessage("Item Cancelled");
            configList.remove((Settings) event.getObject());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     *
     * @param conf
     */
    public void delete(Settings conf) {
        try {
            configList.remove(conf);

            settingsChart.remove(view);
            settingsChart.put(view, configList);

            msg = new FacesMessage("Item Deleted", conf.getLabel1());

            if (columns > 1) {
                columns = configList.size();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     *
     * @param idCliente
     * @return
     */
    public static Map<String, List<Settings>> getSettings(int idCliente) {
        settingsChart = NegocioSettings.getSettings(idCliente);
        return settingsChart;
    }

    /**
     *
     * @param view
     * @return
     */
    public static List<Settings> getSettingByView(String view) {
        return NegocioSettings.getSettingByView(settingsChart, view);
    }

    /**
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        try {
            copyFile(event.getFile().getFileName() + "_" + view, event.getFile().getInputstream());
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
                tempfile = File.createTempFile(fileName, ".tmp");
                logger.info("Temp file : " + tempfile.getAbsolutePath());

                OutputStream out = new FileOutputStream(tempfile);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = in.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                in.close();
                out.flush();
                out.close();

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
                    Row row = rowIterator.next();

                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {

                        Cell cell = cellIterator.next();

                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                logger.info(cell.getStringCellValue() + "\t");
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                logger.info(cell.getNumericCellValue() + "\t");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                logger.info(cell.getBooleanCellValue() + "\t");
                                break;
                            default:
                                break;
                        }
                    }
                    logger.info("");
                }
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    //***///
    private void getVersion() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "assoc", ".xls"});
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String extensionType = input.readLine();
            input.close();
            // extract type
            if (extensionType == null) {
                System.out.println("no office installed ?");
                System.exit(1);
            }
            String fileType[] = extensionType.split("=");

            p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "ftype", fileType[1]});
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String fileAssociation = input.readLine();
            // extract path
            String officePath = fileAssociation.split("=")[1];
            System.out.println(officePath);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

    public List<String> getDimensions1() {
        return dimensions1;
    }

    public void setDimensions1(List<String> dimensions1) {
        this.dimensions1 = dimensions1;
    }

    public List<String> getDimensions2() {
        return dimensions2;
    }

    public void setDimensions2(List<String> dimensions2) {
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
}
