package gov.nmb.oauth.server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class ServiceAuth {
	/** Email of the Service Account */
	private static final String SERVICE_ACCOUNT_EMAIL = "995412434952-qocmadui1vhbsim4iqgkc3ec1ts204t9@developer.gserviceaccount.com";

	/** Path to the Service Account's Private Key file */
	private static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "os-testwidgetapp-a804834b6028.p12";

	public static Drive getDriveService(String userEmail) throws GeneralSecurityException,IOException, URISyntaxException {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		List<String> list = new ArrayList<String>();
		list.add(DriveScopes.DRIVE);
		GoogleCredential credential = new GoogleCredential.Builder()
				.setTransport(httpTransport)
				.setJsonFactory(jsonFactory)
				.setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
				.setServiceAccountScopes(list)
				.setServiceAccountPrivateKeyFromP12File(new java.io.File(SERVICE_ACCOUNT_PKCS12_FILE_PATH))
				.setServiceAccountUser(userEmail)
				.build();
		Drive service = new Drive.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(credential).build();
		
		return service;
	}
}
