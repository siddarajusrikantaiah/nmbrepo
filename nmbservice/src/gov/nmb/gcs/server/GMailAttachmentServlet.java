package gov.nmb.gcs.server;

import gov.nmb.config.ConfigUtil;
import gov.nmb.dataservice.server.NBMDataServiceImpl;
import gov.nmb.gcstore.server.CloudStorageImpl;
import gov.nmb.oauth.server.ServiceAuth;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

public class GMailAttachmentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private CloudStorageImpl cloudStorageImpl= null;
	private static final String EMAIL_USER = ConfigUtil.getProperty("EMAIL_USER");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().write("In Get".getBytes());
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		Gmail service = null;
		String days = NBMDataServiceImpl.getNoOfDaysToSearch();	
		try {
			service = ServiceAuth.getGMailService(EMAIL_USER);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		ListMessagesResponse messagesResponse = service.users().messages().list(EMAIL_USER).setQ("has:attachment newer_than:"+days+"d").execute();
		if (messagesResponse.getResultSizeEstimate() > 0) {
			List<Message> messages = messagesResponse.getMessages();
			String lastMessageId = NBMDataServiceImpl.getLastMessageId();
			int messageCounter = 0;
			for (Message message : messages) {
				if (message.getId().equals(lastMessageId)) {
					break;
				}
				getAttachments(service, EMAIL_USER, message.getId(),messageCounter);
				messageCounter++;
			}
		}
		resp.setContentType("text/plain");
		resp.getWriter().println("Success ");
	}
	public void getAttachments(Gmail service, String userId, String messageId, int messageCounter) throws IOException {
		Message message = service.users().messages().get(userId, messageId).execute();
		    List<MessagePart> parts = message.getPayload().getParts();
		    
		    if(parts!=null){
		    for (MessagePart part : parts) {
		      if (part.getFilename() != null && part.getFilename().length() > 0) {
		        String filename = part.getFilename();
		        String contentType = part.getMimeType();
		        String attId = part.getBody().getAttachmentId();
		        MessagePartBody attachPart = service.users().messages().attachments().get(userId, messageId, attId).execute();
		        byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
		        
		        List<MessagePartHeader> headers = message.getPayload().getHeaders();
		        String subject = "";
		        String date = "";
		        String from = "";
		       
			    for (MessagePartHeader messagePartHeader : headers) {
			    	if(messagePartHeader.getName().equalsIgnoreCase("Subject")){
			    		subject = messagePartHeader.getValue();
			    	}				    	
			    	if(messagePartHeader.getName().equalsIgnoreCase("From")){
			    		from = messagePartHeader.getValue();
			    	}				    		
		    		if(messagePartHeader.getName().equalsIgnoreCase("Date")){
		    			date = messagePartHeader.getValue();
		    		}
				}		        
		        
		        uploadToCloudStorage(fileByteArray, userId, filename, contentType, date, from, subject, messageId);
		        if(messageCounter==0){
		        	NBMDataServiceImpl.storeLatestMessage(messageId, from, date, subject);
		        }
		      }
		    }
		    }
		  }
	
	
	  private boolean uploadToCloudStorage(byte[] bytes, String user, String fileName, String contentType, String date, String from, String subject,String messageId){
		  cloudStorageImpl = new CloudStorageImpl();
		  return  cloudStorageImpl.uploadToBucket(bytes, user, fileName, contentType, date, from, subject, messageId);

	  }
  
}
