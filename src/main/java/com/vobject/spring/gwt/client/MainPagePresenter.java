/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vobject.spring.gwt.client;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.vobject.spring.gwt.shared.FieldVerifier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

/**
 * @author Philippe Beaudoin
 */
public class MainPagePresenter extends
    Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy> {
  /**
   * {@link com.vobject.spring.gwt.client.MainPagePresenter}'s proxy.
   */
  @ProxyStandard
  @NameToken(nameToken)
  public interface MyProxy extends Proxy<MainPagePresenter>, Place {
  }

  /**
   * {@link com.vobject.spring.gwt.client.MainPagePresenter}'s view.
   */
  public interface MyView extends View {
    String getName();

    Button getSendButton();

    void resetAndFocus();

    void setError(String errorText);
  }

  public static final String nameToken = "main";

  private final PlaceManager placeManager;
  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
  
  private static final String SERVER_ERROR = "An error occurred while "
	      + "attempting to contact the server. Please check your network "
	      + "connection and try again.";  
  

  @Inject
  public MainPagePresenter(EventBus eventBus, MyView view, MyProxy proxy,
      PlaceManager placeManager) {
    super(eventBus, view, proxy);
    this.placeManager = placeManager;
  }

  @Override
  protected void onBind() {
    super.onBind();
    registerHandler(getView().getSendButton().addClickHandler(
        new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            sendNameToServer();
          }
        }));
  }

  @Override
  protected void onReset() {
    super.onReset();
    getView().resetAndFocus();
  }

  @Override
  protected void revealInParent() {
    RevealRootContentEvent.fire(this, this);
  }

  /**
   * Send the name from the nameField to the server and wait for a response.
   
  private void sendNameToServer() {
    // First, we validate the input.
    getView().setError("");
    String textToServer = getView().getName();
    if (!FieldVerifier.isValidName(textToServer)) {
      getView().setError("Please enter at least four characters");
      return;
    }

    // Then, we transmit it to the ResponsePresenter, which will do the server
    // call
    //placeManager.revealPlace(new PlaceRequest(ResponsePresenter.nameToken).with(
      //  ResponsePresenter.textToServerParam, textToServer)); }*/
  /**
   * Send the name from the nameField to the server and wait for a response.
   */
  private void sendNameToServer() {
    // First, we validate the input.
	  getView().setError("");
    String textToServer = getView().getName();
    if (!FieldVerifier.isValidName(textToServer)) {
    	 getView().setError("Please enter at least four characters");
      return;
    }

    // Then, we send the input to the server.
    getView().getSendButton().setEnabled(false);
    // Create the popup dialog box
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Remote Procedure Call");
    dialogBox.setAnimationEnabled(true);
    final Button closeButton = new Button("Close");
    // We can set the id of a widget by accessing its Element
    closeButton.getElement().setId("closeButton");
    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
    dialogVPanel.add(textToServerLabel);
    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    dialogVPanel.add(serverResponseLabel);
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(closeButton);
    dialogBox.setWidget(dialogVPanel);    
    // Add a handler to close the DialogBox
    closeButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
        getView().getSendButton().setEnabled(true);
        getView().getSendButton().setFocus(true);
          }
      });    
    
    textToServerLabel.setText(textToServer);
    serverResponseLabel.setText("");
    greetingService.greetServer(textToServer, new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        // Show the RPC error message to the user
        dialogBox.setText("Remote Procedure Call - Failure");
        serverResponseLabel.addStyleName("serverResponseLabelError");
        serverResponseLabel.setHTML(SERVER_ERROR);
        dialogBox.center();
        closeButton.setFocus(true);
      }

      public void onSuccess(String result) {
        dialogBox.setText("Remote Procedure Call");
        serverResponseLabel.removeStyleName("serverResponseLabelError");
        serverResponseLabel.setHTML(result);
        dialogBox.center();
        closeButton.setFocus(true);
      }
    });
  }

}
