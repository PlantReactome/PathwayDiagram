/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import org.reactome.web.site.client.common.view.ReactomeImages;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Creates a banner that will expand to fill the width of the enclosing
 * panel.  Based on the "half height" Reactome banner.
 * 
 * @author David Croft <david.croft@ebi.ac.uk>
 */
public class ExpandingBannerPanel extends FlowPanel {
	Image halfHeightBanner1Image = null;
	SimplePanel fillerPanel = null;
	SimplePanel imagePanel = null;
	
    public ExpandingBannerPanel() {
    	setWidth("100%");
    	
    	ImageResource halfHeightBanner1ImageResource = ReactomeImages.INSTANCE.halfHeightBanner1();
    	halfHeightBanner1Image = new Image(halfHeightBanner1ImageResource);
    	imagePanel = new SimplePanel();
    	imagePanel.getElement().setAttribute("style", "float: left;");
    	imagePanel.add(halfHeightBanner1Image);
    	
    	fillerPanel = new SimplePanel();
    	fillerPanel.setHeight(halfHeightBanner1Image.getHeight() + "px");
    	fillerPanel.setStyleName("half_height_banner_filler"); // CSS
    	fillerPanel.setWidth("100%");
    	
    	add(imagePanel);
    	add(fillerPanel);
    }
}


