package gov.nmb.gcstore.server;

import gov.nmb.config.ConfigUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class CloudStorageImpl {
	
	public static final String BUCKETNAME = ConfigUtil.getProperty("BUCKETNAME");

	 public boolean writeToBucket(FileItemStream item, String user){
		  try{
			InputStream stream = item.openStream(); 
			byte[] bytes = IOUtils.toByteArray(stream);
			String fileName = item.getName();
			  
		    GcsService gcsService = GcsServiceFactory.createGcsService();
		    GcsFilename filename = new GcsFilename(BUCKETNAME, fileName);
		    GcsFileOptions options = new GcsFileOptions.Builder()
		        								.mimeType(item.getContentType())
		        								.acl("public-read")
		        								.addUserMetadata("userUploaded", user)
		        								.addUserMetadata("FileName", fileName)
		        								.addUserMetadata("ContentType", item.getContentType())
		        								.build();
		    GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
		    writeChannel.write(ByteBuffer.wrap(bytes));
		    writeChannel.close();
		    
		  }catch(Exception e){
			  e.printStackTrace();
		  }	  
		  return true;
	  }
	 
	 public boolean uploadToBucket(byte[] bytes, String user, String fileName, String contentType){
		  try{
 
		    GcsService gcsService = GcsServiceFactory.createGcsService();
		    GcsFilename filename = new GcsFilename(BUCKETNAME, fileName);
		    GcsFileOptions options = new GcsFileOptions.Builder()
		        								.mimeType(contentType)
		        								.acl("public-read")
		        								.addUserMetadata("userUploaded", user)
		        								.addUserMetadata("FileName", fileName)
		        								.addUserMetadata("ContentType", contentType)
		        								.build();
		    GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
		    writeChannel.write(ByteBuffer.wrap(bytes));
		    writeChannel.close();
		    
		  }catch(Exception e){
			  e.printStackTrace();
		  }	  
		  return true;
	  }
	
	
}
