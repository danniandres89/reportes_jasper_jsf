package com.core.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.j2ee.servlets.BaseHttpServlet;
import net.sf.jasperreports.view.JasperViewer;

import org.primefaces.model.StreamedContent;

import com.core.dao.PostgreSQL;
import com.itextpdf.text.DocumentException;

@SuppressWarnings({ "deprecation", "unused" })
@ManagedBean(name = "reportBean")
@ViewScoped
public class ReportBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StreamedContent media;
	private String dir;

	@PostConstruct
	public void init() {

	}

	// Metodo para crear y descargar el reporte
	public void printPDF() throws JRException, IOException {

		String fileName = "balanceGeneral.pdf";
		String jasperPath = "/resources/balanceGeneralActivo.jasper";
		this.PDF(null, jasperPath, null, fileName);

	}

	public void printXLS() throws JRException, IOException {

		String fileName = "balanceGeneral.xls";
		String jasperPath = "/resources/balanceGeneralActivo.jasper";
		this.XLS(null, jasperPath, null, fileName);

	}

	public void viewReport() throws JRException, IOException {

		String fileName = "balanceGeneral.pdf";
		String jasperPath = "/resources/balanceGeneralActivo.jasper";
		this.VIEW(null, jasperPath, null, fileName);

	}

	public void printHTML() throws JRException, IOException {

		String fileName = "balanceGeneral.xls";
		String jasperPath = "/resources/balanceGeneralActivo.jasper";
		this.viewHTML(null, jasperPath, null, fileName);

	}

	public String getPathFileJasper() {
		return "/resources/balanceGeneralActivo.jasper";
	}

	public String getNameFilePdf() {
		return "balanceGeneral.pdf";
	}

	public void XLS(Map<String, Object> params, String jasperPath,
			List<?> dataSource, String fileName) throws JRException,
			IOException {

		String relativeWebPath = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath(jasperPath);
		File file = new File(relativeWebPath);
		Connection conn = null;
		conn = PostgreSQL.GetConnection();

		// JRBeanCollectionDataSource source = new
		// JRBeanCollectionDataSource(dataSource,false);

		JasperPrint print = JasperFillManager.fillReport(file.getPath(),
				params, conn);
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.addHeader("Content-disposition", "attachment;filename="
				+ fileName);
		ServletOutputStream stream = response.getOutputStream();

		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		JRXlsExporter exporterXLS = new JRXlsExporter();

		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,
				arrayOutputStream);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
				Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
				Boolean.TRUE);
		exporterXLS.setParameter(
				JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporterXLS.setParameter(
				JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
				Boolean.TRUE);
		exporterXLS.exportReport();

		response.setContentType("application/vnd.ms-excel");
		response.setContentLength(arrayOutputStream.toByteArray().length);
		stream.write(arrayOutputStream.toByteArray());
		stream.flush();
		stream.close();

		FacesContext.getCurrentInstance().responseComplete();

	}

	public void VIEW(Map<String, Object> params, String jasperPath,
			List<?> dataSource, String fileName) throws JRException {

		try {

			String relativeWebPath = FacesContext.getCurrentInstance()
					.getExternalContext().getRealPath(jasperPath);
			File file = new File(relativeWebPath);
			Connection conn = null;
			conn = PostgreSQL.GetConnection();

			// File jasper = new
			// File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/rpJSF.jasper"));

			byte[] bytes = JasperRunManager.runReportToPdf(file.getPath(),
					params, conn);
			HttpServletResponse response = (HttpServletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);
			ServletOutputStream outStream;

			outStream = response.getOutputStream();

			outStream.write(bytes, 0, bytes.length);
			outStream.flush();
			outStream.close();

			FacesContext.getCurrentInstance().responseComplete();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// String relativeWebPath = FacesContext.getCurrentInstance()
	// .getExternalContext().getRealPath(jasperPath);
	// File file = new File(relativeWebPath);
	// Connection conn = null;
	// conn = PostgreSQL.GetConnection();
	// // JasperPrint imp = JasperFillManager.fillReport(file.getPath(), null,
	// // conn);
	// // JasperViewer ver = new JasperViewer(imp);
	// // ver.setTitle(fileName);
	// // ver.setVisible(true);
	//
	// HttpServletResponse response = (HttpServletResponse) FacesContext
	// .getCurrentInstance().getExternalContext().getResponse();
	// response.setContentType("application/pdf");
	// ServletOutputStream out;
	// try {
	// out = response.getOutputStream();
	// JasperPrint jasperPrint = JasperFillManager.fillReport(
	// file.getPath(), null, conn);
	//
	// JRExporter exporter = new JRPdfExporter();
	// exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	// exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
	// exporter.exportReport();
	//
	// FacesContext.getCurrentInstance().responseComplete();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }

	public void PDF(Map<String, Object> params, String jasperPath,
			List<?> dataSource, String fileName) throws JRException,
			IOException {

		String relativeWebPath = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath(jasperPath);
		File file = new File(relativeWebPath);
		Connection conn = null;
		conn = PostgreSQL.GetConnection();

		// JRBeanCollectionDataSource source = new
		// JRBeanCollectionDataSource(dataSource,false);

		JasperPrint print = JasperFillManager.fillReport(file.getPath(),
				params, conn);

		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.addHeader("Content-disposition", "attachment;filename="
				+ fileName);

		ServletOutputStream stream = response.getOutputStream();

		JasperExportManager.exportReportToPdfStream(print, stream);

		FacesContext.getCurrentInstance().responseComplete();

	}

	public void viewHTML(Map<String, Object> params, String jasperPath,
			List<?> dataSource, String fileName) throws JRException,
			IOException {

		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();

//		HttpServletRequest request = (HttpServletRequest) FacesContext
//				.getCurrentInstance().getExternalContext().getRequest();

		String relativeWebPath = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath(jasperPath);
		File file = new File(relativeWebPath);
		Connection conn = null;
		conn = PostgreSQL.GetConnection();

		JasperPrint jasperPrint = JasperFillManager.fillReport(file.getPath(),
				params, conn);

//		request.getSession().setAttribute(
//				BaseHttpServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,
//				jasperPrint);

		// Creation of the HTML Jasper Reports
		// JasperExportManager.exportReportToHtmlFile(jasperPrint,
		// "/vista.html");

		response.setContentType("text/html;charset=ISO-8859-1");

		JRHtmlExporter exporter = new JRHtmlExporter();
		exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
				Boolean.FALSE);
		exporter.setParameter(
				JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
				Boolean.TRUE);
		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING,
				"ISO-8859-9");

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

		OutputStream ouputStream = response.getOutputStream();
		
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);

		// exporter.setParameter(JRExporterParameter.OUTPUT_WRITER,
		// response.getWriter());

		exporter.exportReport();

		ouputStream.flush();
		ouputStream.close();
		FacesContext.getCurrentInstance().responseComplete();

	}

	public void DownloadPDF(Map<String, Object> params, String jasperPath,
			List<?> dataSource, File filePdf) throws JRException, IOException {

		String relativeWebPath = FacesContext.getCurrentInstance()
				.getExternalContext().getRealPath(jasperPath);
		File file = new File(relativeWebPath);
		Connection conn = null;
		conn = PostgreSQL.GetConnection();

		// JRBeanCollectionDataSource source = new
		// JRBeanCollectionDataSource(dataSource,false);

		JasperPrint print = JasperFillManager.fillReport(file.getPath(),
				params, conn);
		// HttpServletResponse response = (HttpServletResponse) FacesContext
		// .getCurrentInstance().getExternalContext().getResponse();
		// response.addHeader("Content-disposition", "attachment;filename="
		// + file);

		System.out.println(filePdf.getAbsolutePath());
		JasperExportManager.exportReportToPdfFile(print,
				filePdf.getAbsolutePath());

		FacesContext.getCurrentInstance().responseComplete();
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}
