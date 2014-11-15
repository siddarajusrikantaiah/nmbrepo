package gov.nmb.gcs.server;

import gov.nmb.gcstore.server.CloudStorageImpl;
import gov.nmb.gdrive.server.DriveImpl;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.api.services.drive.model.File;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class NMBGCSUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private DriveImpl driveImpl = null;
	private CloudStorageImpl cloudStorageImpl= null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().write("In Get".getBytes());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		String target = "";
		String userEmail = "";
		File file = null;
		
		 Principal userPrincipal = req.getUserPrincipal();

		 	userEmail = userPrincipal.getName();		    
		    System.out.println("User name from userPrincipal  ============================================== "+userEmail);
	
		if (ServletFileUpload.isMultipartContent(req)) {

			try {
				ServletFileUpload upload = new ServletFileUpload();
				resp.setContentType("text/plain");
				
				FileItemIterator iterator = upload.getItemIterator(req);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();					
					if (item.isFormField()) {
						InputStream stream = item.openStream();
						 byte[] str = new byte[stream.available()];
				            stream.read(str);
				            String pFieldValue = new String(str,"UTF8");
				            if(item.getFieldName().equals("target")){
				                System.out.println("text Value : "+pFieldValue);
				                target=pFieldValue;
				            }		
					} else {
						if(target.equals("GCS")){
							writeToBucket(item, userEmail);
							System.out.println("Written to GCS");
						}else if(target.equals("DRIVE")){						
							file = writeToDrive(item);
							if(userEmail!=null && !userEmail.equals("") && file!=null){
								givePermissionsToDriveFile(file.getId(), userEmail, "reader");
							}
							System.out.println("Written to Drive");
						}						
					}
				}	
			
				resp.getOutputStream().write("Success ".getBytes());
				
			} catch (Exception ex) {
				throw new ServletException(ex);
			}

		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Request contents type is not supported by the servlet.");
		}
		
	}
  
  
  private boolean writeToBucket(FileItemStream item, String mainEmail){
	  cloudStorageImpl = new CloudStorageImpl();
	  return  cloudStorageImpl.writeToBucket(item, mainEmail);
  }
  
  private File writeToDrive(FileItemStream item){
	  File file = null;
	  try {
		driveImpl = new DriveImpl();
		file = driveImpl.uploadFile(false, item);
	} catch (IOException e) {
		e.printStackTrace();
	}
	  return file;
  }
  
  private void givePermissionsToDriveFile(String fileId, String user, String permission){
	  driveImpl.givePermissionsToDriveFile(fileId, user, permission);
	 }

  
  	
	
	private String getLogoutUser(){
		UserService userService = UserServiceFactory.getUserService();
		String logoutURL = userService.createLogoutURL("/");
		return logoutURL;

		
	}
  
}
