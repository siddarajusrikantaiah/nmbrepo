package gov.nmb.gdrive.server;

import gov.nmb.config.ConfigUtil;
import gov.nmb.oauth.server.ServiceAuth;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.log4j.Logger;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public class DriveImpl {
	private Drive drive;
	private Logger logger = Logger.getLogger("AppLoger");
	private static final String OWNER_USER_NAME = ConfigUtil.getProperty("OWNER_USER_NAME");

	private void getDrive(String userEmail) {
		try {
			drive = ServiceAuth.getDriveService(userEmail);
		} catch (Exception e) {
			System.out.println("Error Message is == ==================   "
					+ e.getMessage());
		}
	}

/*	public File uploadFile(boolean useDirectUpload, FileItemStream item)
			throws IOException {
		getDrive(OWNER_USER_NAME);
		File fileMetadata = new File();
		fileMetadata.setTitle(item.getName());
		System.out.println("item.getContentType()      "+item.getContentType());
		
		InputStreamContent mediaContent = new InputStreamContent(item.getContentType(), item.openStream());
		Drive.Files.Insert insert = drive.files().insert(fileMetadata,
				mediaContent);
		MediaHttpUploader uploader = insert.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(useDirectUpload);
		// uploader.setProgressListener(new FileUploadProgressListener());
		return insert.execute();		
	}*/
	
	public File uploadFile(boolean useDirectUpload, FileItemStream item) throws IOException {
		return upload(useDirectUpload, item.getName(), item.getContentType(), item.openStream());		
	}
	
	
	public File serviceUploadFile(boolean useDirectUpload, String fileName,
			String fileType, InputStream inputStream) throws IOException {
		return upload(useDirectUpload, fileName, fileType, inputStream);
	}
	
	

	private File upload(boolean useDirectUpload, String fileName,
			String fileType, InputStream inputStream) throws IOException {
		getDrive(OWNER_USER_NAME);
		File fileMetadata = new File();
		fileMetadata.setTitle(fileName);
		InputStreamContent mediaContent = new InputStreamContent(fileType,inputStream);
		Drive.Files.Insert insert = drive.files().insert(fileMetadata,mediaContent);
		MediaHttpUploader uploader = insert.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(useDirectUpload);
		// uploader.setProgressListener(new FileUploadProgressListener());
		return insert.execute();
	}

	public void givePermissionsToDriveFile(String fileId, String user,
			String role) {
		try {
			Permission content = new Permission();
			content.setValue(user);
			content.setType("user");
			content.setRole(role);
			Permission id = drive.permissions().insert(fileId, content).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
