/* Copyright (c) 2010 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import org.reactome.gwt.client.widgets.panels.HorizontalInnerPanel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Generic popup for Reactome.
 *
 * @author David Croft
 */
public class ReactomePopup extends  PopupPanel {
	protected VerticalPanel widgetPanel = new VerticalPanel();
	private HTML textLabel = new HTML();
	protected Button closeButton;
	protected HorizontalInnerPanel sparePanel = new HorizontalInnerPanel();
	protected HorizontalInnerPanel buttonPanel = new HorizontalInnerPanel();
	
	public ReactomePopup() {
		// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
		// If this is set, the panel closes itself automatically when the user
		// clicks outside of it.
		super(true);

		this.setStyleName("popup"); // CSS

		widgetPanel.add(textLabel);

		widgetPanel.add(sparePanel);

		setWidth("200px");
		setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		ClickListener listener = new ClickListener()
		{
			public void onClick(Widget sender)
			{
				hide();
			}
		};
		closeButton = new Button("OK", listener);
		buttonPanel.add(closeButton);
		widgetPanel.add(buttonPanel);

		// PopupPanel is a SimplePanel, so you have to set it's widget property to
		// whatever you want its contents to be.
		setWidget(widgetPanel);
		
		// Place popup in center of browser window
		center();
	}
	
	public void setText(String text) {
		textLabel.setHTML(text);
	}

	@Override
	public void setWidth(String width) {
		widgetPanel.setWidth(width);
	}

	public void setHorizontalAlignment(HorizontalAlignmentConstant alignment) {
		widgetPanel.setHorizontalAlignment(alignment);
	}
}
