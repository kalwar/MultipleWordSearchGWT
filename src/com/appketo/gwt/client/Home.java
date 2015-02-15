package com.appketo.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;

public class Home implements EntryPoint {
	
	//private Label msg = new Label ("Hello, GWT");
	//private Button btn = new Button ("Click here");
	private LittleForm form = new LittleForm();

	@Override
	public void onModuleLoad() {

		final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.add("Nepal");
		oracle.add("France");
		oracle.add("Belgium");
		oracle.add("England");
		oracle.add("Turkey");
		final SuggestBox box = new SuggestBox(oracle);
		RootPanel.get().add(form);
		//RootPanel.get("root_panel").add(box);

	}

}
