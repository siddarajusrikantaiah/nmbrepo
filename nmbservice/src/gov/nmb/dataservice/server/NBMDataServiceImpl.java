package gov.nmb.dataservice.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

public class NBMDataServiceImpl {
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public static void storeLatestMessage(String messageId, String from, String date, String subject){
		Long lastMessageKey = getLastMessageKey();
		Entity latestMessageEntity = null;
		if(lastMessageKey!=null){
			latestMessageEntity = new Entity("LatestMessageStored", lastMessageKey);
		}else {
			latestMessageEntity = new Entity("LatestMessageStored");			
		}		
		latestMessageEntity.setProperty("messageId", messageId);
		latestMessageEntity.setProperty("from", from);
		latestMessageEntity.setProperty("date", date);
		latestMessageEntity.setProperty("subject", subject);
		latestMessageEntity.setProperty("noOfDaysToSearch", "1");
		
		datastore.put(latestMessageEntity);
	}
	
	public static Entity getLastMessageEntity(){
		Entity latestMessageStored = null;
		try {
			Query query = new Query("LatestMessageStored");
			FetchOptions fetchOptions = FetchOptions.Builder.withPrefetchSize(1).chunkSize(1);			
			QueryResultList<Entity> returned_entities = datastore.prepare(query).asQueryResultList(fetchOptions);
			if(returned_entities.size()>0){
				for (Entity result : returned_entities) {
					latestMessageStored = result;				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return latestMessageStored;
	}
	public static String getLastMessageId(){
		String lastMessageId = "";
		Entity lastMessageEntity = getLastMessageEntity();
		if(lastMessageEntity!=null&&lastMessageEntity.getProperty("messageId")!=null){
			lastMessageId = (String)lastMessageEntity.getProperty("messageId");
		}
		return lastMessageId;
	}
		
	public static String getNoOfDaysToSearch(){
		String currentWindow = "1";
		Entity lastMessageEntity = getLastMessageEntity();
		if(lastMessageEntity!=null && lastMessageEntity.getProperty("noOfDaysToSearch")!=null){
			try{
			currentWindow = (String)lastMessageEntity.getProperty("noOfDaysToSearch");
			}catch(Exception e){}
		}
		return currentWindow;
	}
	
	private static Long getLastMessageKey(){
		Long lastMessageKey = null;
		Entity lastMessageEntity = getLastMessageEntity();
		if(lastMessageEntity!=null){
			Key latestMessageKey = lastMessageEntity.getKey();
			lastMessageKey = latestMessageKey.getId();
		}
		return lastMessageKey;
	}
	
	
}
