package gov.nmb.gcs.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class NMBGCSUpload implements EntryPoint {
	final FormPanel form = new FormPanel();

	private static final String UPLOAD_ACTION_URL = GWT.getModuleBaseURL()+ "upload";
	TextBox targetTextBox;

	public void onModuleLoad() {

		RootPanel rootPanel = RootPanel.get();
		
				
//		rootPanel.setHeight("100%");
//		rootPanel.setWidth("100%");							
//		rootPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		rootPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		DecoratorPanel decoratorPanel = new DecoratorPanel();
//		rootPanel.add(decoratorPanel, 279, 224);
		
		System.out.println("rootPanel.getOffsetWidth()/2   "+rootPanel.getAbsoluteLeft());
		System.out.println("rootPanel.getOffsetHeight()/2   "+rootPanel.getAbsoluteTop());
	
		rootPanel.add(decoratorPanel, (rootPanel.getOffsetWidth()/2)-100, 250);
		
				decoratorPanel.setWidget(form);
		
		
		
				form.setAction(UPLOAD_ACTION_URL);
				
						form.setEncoding(FormPanel.ENCODING_MULTIPART);
						form.setMethod(FormPanel.METHOD_POST);
						
								VerticalPanel panel = new VerticalPanel();
								
								form.setWidget(panel);
								panel.setSize("263px", "153px");
								panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

								
								targetTextBox = new TextBox();
//								targetTextBox.setEnabled(false);
								targetTextBox.setVisible(false);
								targetTextBox.setName("target");
								panel.add(targetTextBox);
								
								FileUpload upload = new FileUpload();
								upload.setName("uploadFormElement");
								panel.add(upload);
								panel.setCellVerticalAlignment(upload, HasVerticalAlignment.ALIGN_MIDDLE);
								panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
								
								HorizontalPanel flowPanel = new HorizontalPanel();
								panel.add(flowPanel);
								panel.setCellVerticalAlignment(flowPanel, HasVerticalAlignment.ALIGN_MIDDLE);
								panel.setCellHorizontalAlignment(flowPanel, HasHorizontalAlignment.ALIGN_CENTER);
								flowPanel.setHeight("44px");
								
								Label lblNewLabel = new Label("User Email");
								flowPanel.add(lblNewLabel);
								flowPanel.setCellVerticalAlignment(lblNewLabel, HasVerticalAlignment.ALIGN_MIDDLE);
								lblNewLabel.setSize("68px", "18px");
								
								TextBox textBox = new TextBox();
								textBox.setName("userEmail");
								flowPanel.add(textBox);
								textBox.setWidth("177px");
								flowPanel.setCellVerticalAlignment(textBox, HasVerticalAlignment.ALIGN_MIDDLE);
								
								HorizontalPanel horizontalPanel = new HorizontalPanel();
								panel.add(horizontalPanel);
								panel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);
								panel.setCellVerticalAlignment(horizontalPanel, HasVerticalAlignment.ALIGN_BOTTOM);
								horizontalPanel.setSize("260px", "36px");
								
								Button gcsUploadButton = new Button("GCSUpload",	new GCSUploadButtonClickHandler());
								horizontalPanel.add(gcsUploadButton);
								horizontalPanel.setCellHorizontalAlignment(gcsUploadButton, HasHorizontalAlignment.ALIGN_CENTER);
								horizontalPanel.setCellVerticalAlignment(gcsUploadButton, HasVerticalAlignment.ALIGN_MIDDLE);
								gcsUploadButton.setText("Upload to GC");
								
								Button driveUploadButton = new Button("DriveUpload" , new DriveUploadButtonClickHandler());
								horizontalPanel.add(driveUploadButton);
								horizontalPanel.setCellHorizontalAlignment(driveUploadButton, HasHorizontalAlignment.ALIGN_CENTER);
								horizontalPanel.setCellVerticalAlignment(driveUploadButton, HasVerticalAlignment.ALIGN_MIDDLE);
								driveUploadButton.setText("Upload to Drive");
								
								
								
								form.addSubmitHandler(new SubmittFormHandler());
										
								form.addSubmitCompleteHandler(new SubmittFormCompleteHandler());
								form.setSize("263px", "153px");

	}
	
	private class GCSUploadButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			System.out.println("Submitting form  ");
			targetTextBox.setValue("GCS");
			form.submit();
		
		}
	}
	
	private class DriveUploadButtonClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			System.out.println("Submitting form  ");
			targetTextBox.setValue("DRIVE");
			form.submit();
		
		}
	}
	
	private class SubmittFormHandler implements FormPanel.SubmitHandler {
		@Override
		public void onSubmit(SubmitEvent event) {

		}
	}
	
	private class SubmittFormCompleteHandler implements	FormPanel.SubmitCompleteHandler {
		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			form.reset();
//			 System.out.println("Event Result is   "+event.getResults());
			 Window.alert(event.getResults());
		}
	}
}
