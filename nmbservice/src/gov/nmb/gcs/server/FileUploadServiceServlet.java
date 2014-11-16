package gov.nmb.gcs.server;

import gov.nmb.server.gdrive.DriveImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.drive.model.File;

public class FileUploadServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DriveImpl driveImpl = null;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().write("In Get".getBytes());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		System.out.println("Request Reached Servlet =============================================   ");
		
		String fileName = req.getParameter("fileName");
		String fileType = req.getParameter("fileType");		
		InputStream inputStream = req.getInputStream();
		
		if(fileName!=null && fileType!=null && inputStream!=null) {
			writeToDrive(fileName, fileType, inputStream);	
			
			resp.setContentType("text/plain");
			resp.getWriter().println("Success ");
		}else {
			System.out.println("Either fileName or fileType or inputStream is not set ======================== ");
			
			resp.setContentType("text/plain");
			resp.getWriter().println("Fail to Upload");
		}
		
	}
	
	  private File writeToDrive(String fileName, String fileType, InputStream item){
		  File file = null;
		  try {
			driveImpl = new DriveImpl();
			file = driveImpl.serviceUploadFile(false, fileName, fileType, item);
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return file;
	  }
  
}
