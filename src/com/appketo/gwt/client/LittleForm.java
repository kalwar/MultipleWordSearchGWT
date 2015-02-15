package com.appketo.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LittleForm extends Composite {

	private static LittleFormUiBinder uiBinder = GWT
			.create(LittleFormUiBinder.class);

	interface LittleFormUiBinder extends UiBinder<Widget, LittleForm> {
	}
	@UiField
	HorizontalPanel panel;
	
	String uniqueId = DOM.createUniqueId();
	List<String> itemsSelected = new ArrayList();
	List<ListItem> itemsHighlighted = new ArrayList();

	public LittleForm() {
		
		final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.add("Tunisia");
		oracle.add("France");
		oracle.add("Belgium");
		oracle.add("England");
		oracle.add("Turkey");
	//	final HorizontalPanel panel = new HorizontalPanel();
	//	initWidget(panel);
		initWidget(uiBinder.createAndBindUi(this));
		final BulletList list = new BulletList();
	//	list.setStyleName("multiValueSuggestBox-list");
		final ListItem item = new ListItem();
	//	item.setStyleName("multiValueSuggestBox-input-token");
		final TextBox itemBox = new TextBox();
		//itemBox.getElement()
		//		.setAttribute(
		//				"style",
		//				"outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");
		final SuggestBox box = new SuggestBox(oracle, itemBox);
		box.getElement().setId("suggestion_box" + uniqueId);
		item.add(box);
		list.add(item);

		// this needs to be on the itemBox rather than box, or backspace will
		// get executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			// handle key events on the suggest box
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				switch (event.getNativeKeyCode()) {
				case KeyCodes.KEY_ENTER:
					// only allow manual entries with @ signs (assumed email
					// addresses)
					if (itemBox.getValue().contains("@")) {
						deselectItem(itemBox, list);
					}
					break;
				// handle backspace
				case KeyCodes.KEY_BACKSPACE:
					if (itemBox.getValue().trim().isEmpty()) {
						if (itemsHighlighted.isEmpty()) {
							if (itemsSelected.size() > 0) {
								final ListItem li = (ListItem) list
										.getWidget(list.getWidgetCount() - 2);
								final Paragraph p = (Paragraph) li.getWidget(0);
								if (itemsSelected.contains(p.getText())) {
									// remove selected item
									itemsSelected.remove(p.getText());
								}
								list.remove(li);
							}
						}
					}
					// continue to delete
					// handle delete
				case KeyCodes.KEY_DELETE:
					if (itemBox.getValue().trim().isEmpty()) {
						for (ListItem li : itemsHighlighted) {
							list.remove(li);
							final Paragraph p = (Paragraph) li.getWidget(0);
							itemsSelected.remove(p.getText());
						}
						itemsHighlighted.clear();
					}
					itemBox.setFocus(true);
					break;
				}
			}
		});

		box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			// called when an item is selected from list of suggestions
			@Override
			public void onSelection(
					final SelectionEvent<SuggestOracle.Suggestion> selectionEvent) {
				deselectItem(itemBox, list);
			}
		});
		panel.add(list);
	//	panel.getElement().setAttribute(
	//		"onclick",
	//		"document.getElementById('suggestion_box" + uniqueId
	//				+ "').focus()");
		box.setFocus(true);
	}

	private void deselectItem(final TextBox itemBox, final BulletList list) {
		if (itemBox.getValue() != null && !"".equals(itemBox.getValue().trim())) {
			final ListItem displayItem = new ListItem();
		//	displayItem.setStyleName("multiValueSuggestBox-token");
			final Paragraph p = new Paragraph(itemBox.getValue());

			displayItem.addClickHandler(new ClickHandler() {
				// called when a list item is clicked on
				@Override
				public void onClick(final ClickEvent clickEvent) {
					if (itemsHighlighted.contains(displayItem)) {
						displayItem.removeStyleDependentName("selected");
						itemsHighlighted.remove(displayItem);
					} else {
						displayItem.addStyleDependentName("selected");
						itemsHighlighted.add(displayItem);
					}
				}
			});
			final Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent clickEvent) {
					removeListItem(displayItem, list);
				}
			});

			displayItem.add(p);
			displayItem.add(span);
			// hold the original value of the item selected
			GWT.log("Adding selected item '" + itemBox.getValue() + "'", null);
			itemsSelected.add(itemBox.getValue());
			GWT.log("Total: " + itemsSelected, null);

			list.insert(displayItem, list.getWidgetCount() - 1);
			itemBox.setValue("");
			itemBox.setFocus(true);
		}
	}

	private void removeListItem(final ListItem displayItem,
			final BulletList list) {
		GWT.log("Removing: "
				+ displayItem.getWidget(0).getElement().getInnerHTML(), null);
		itemsSelected.remove(displayItem.getWidget(0).getElement()
				.getInnerHTML());
		list.remove(displayItem);
	}

}

class ListItem extends ComplexPanel implements HasText, HasHTML,
		HasClickHandlers, HasKeyDownHandlers, HasBlurHandlers {
	HandlerRegistration clickHandler;

	public ListItem() {
		setElement(DOM.createElement("LI"));
	}

	public void add(Widget w) {
		super.add(w, getElement());
	}

	public void insert(Widget w, int beforeIndex) {
		super.insert(w, getElement(), beforeIndex, true);
	}

	public String getText() {
		return DOM.getInnerText(getElement());
	}

	public void setText(String text) {
		DOM.setInnerText(getElement(), (text == null) ? "" : text);
	}

	public void setId(String id) {
		DOM.setElementAttribute(getElement(), "id", id);
	}

	public String getHTML() {
		return DOM.getInnerHTML(getElement());
	}

	public void setHTML(String html) {
		DOM.setInnerHTML(getElement(), (html == null) ? "" : html);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addDomHandler(handler, BlurEvent.getType());
	}
}

class BulletList extends ComplexPanel {
	public BulletList() {
		setElement(DOM.createElement("UL"));
	}

	public void add(Widget w) {
		super.add(w, getElement());
	}

	public void insert(Widget w, int beforeIndex) {
		super.insert(w, getElement(), beforeIndex, true);
	}
}

class Paragraph extends Widget implements HasText {

	public Paragraph() {
		setElement(DOM.createElement("p"));
	}

	public Paragraph(String text) {
		this();
		setText(text);
	}

	public String getText() {
		return getElement().getInnerText();
	}

	public void setText(String text) {
		getElement().setInnerText(text);
	}
}

class Span extends HTML implements HasText {

	public Span() {
		super(DOM.createElement("span"));
	}

	public Span(String text) {
		this();
		setText(text);
	}
}
