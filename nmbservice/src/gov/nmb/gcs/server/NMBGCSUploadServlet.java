package gov.nmb.gcs.server;

import gov.nmb.config.ConfigUtil;
import gov.nmb.gdrive.server.DriveImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.google.api.services.drive.model.File;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class NMBGCSUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String BUCKETNAME = ConfigUtil.getProperty("BUCKETNAME");
	private static final String OWNER_USER_NAME = ConfigUtil.getProperty("OWNER_USER_NAME");
	private DriveImpl driveImpl = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().write("In Get".getBytes());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		String target = "";
		String userEmail = "";
		File file = null;
	
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
				            if(item.getFieldName().equals("userEmail")){
				            	userEmail = pFieldValue;
				            	if(userEmail!=null && !userEmail.equals("") && file!=null){
				            		givePermissionsToDriveFile(file.getId(), userEmail, "reader");
								}
				                System.out.println("text Value : "+pFieldValue);
				            }
				            if(item.getFieldName().equals("target")){
				                System.out.println("text Value : "+pFieldValue);
				                target=pFieldValue;
				            }		
					} else {
						if(target.equals("GCS")){
							writeToBucket(item);
							System.out.println("Written to GCS");
						}else if(target.equals("DRIVE")){						
							file = writeToDrive(item, OWNER_USER_NAME);
							if(userEmail!=null && !userEmail.equals("") && file!=null){
								givePermissionsToDriveFile(file.getId(), userEmail, "reader");
							}
							System.out.println("Written to Drive");
						}						
					}
				}				
				resp.getOutputStream().write("UploadSuccess".getBytes());
				
			} catch (Exception ex) {
				throw new ServletException(ex);
			}

		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Request contents type is not supported by the servlet.");
		}
	}
  
  
  private boolean writeToBucket(FileItemStream item){
	  try{
		InputStream stream = item.openStream(); 
		byte[] bytes = IOUtils.toByteArray(stream);
		String fileName = item.getName();
		  
	    GcsService gcsService = GcsServiceFactory.createGcsService();
	    GcsFilename filename = new GcsFilename(BUCKETNAME, fileName);
	    GcsFileOptions options = new GcsFileOptions.Builder()
	        								.mimeType("text/html")
	        								.acl("public-read")
	        								.addUserMetadata("myfield1", "my field value")
	        								.build();
	    GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
	    writeChannel.write(ByteBuffer.wrap(bytes));
	    writeChannel.close();
	    
	  }catch(Exception e){
		  e.printStackTrace();
	  }	  
	  return true;
  }
  
  private File writeToDrive(FileItemStream item, String userEmail){
	  File file = null;
	  try {
		driveImpl = new DriveImpl();
		file = driveImpl.uploadFile(false, item, userEmail);
	} catch (IOException e) {
		e.printStackTrace();
	}
	  return file;
  }
  
  private void givePermissionsToDriveFile(String fileId, String user, String permission){
	  driveImpl.givePermissionsToDriveFile(fileId, user, permission);
	 }

}
