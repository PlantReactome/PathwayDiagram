/* Copyright (c) 2012 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.panels;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Creates a little matrix of icons for the various social networks and other
 * Web 2.0 services that Reactome participates in.
 * 
 * @author David Croft <david.croft@ebi.ac.uk>
 */
public class SocialNetworkIconPanel extends FlowPanel {
    public SocialNetworkIconPanel() {
		setStyleName("textbox"); // CSS
		
		FlowPanel strip1 = new FlowPanel();
		SocialNetworkIcon facebookLabel = new SocialNetworkIcon("http://www.facebook.com/group.php?gid=244908260192&v=wall", "images/logos/socialMedia/facebook.png");
		strip1.add(facebookLabel);
		SocialNetworkIcon linkedInLabel = new SocialNetworkIcon("http://www.linkedin.com/groups?mostPopular=&gid=2118372", "images/logos/socialMedia/linkedin.png");
		strip1.add(linkedInLabel);
		SocialNetworkIcon rssLabel = new SocialNetworkIcon("http://www.reactome.org/static_wordpress/feed/", "images/logos/socialMedia/wordpress.png");
		strip1.add(rssLabel);
		add(strip1);
		
		FlowPanel strip2 = new FlowPanel();
		SocialNetworkIcon twitterLabel = new SocialNetworkIcon("http://twitter.com/reactome", "images/logos/socialMedia/twitter.png");
		strip2.add(twitterLabel);
		SocialNetworkIcon wordpressLabel = new SocialNetworkIcon("http://news.reactome.org/", "images/logos/socialMedia/rss.png");
		strip2.add(wordpressLabel);
		SocialNetworkIcon youTubeLabel = new SocialNetworkIcon("http://www.youtube.com/user/Reactome", "images/logos/socialMedia/youtube.png");
		strip2.add(youTubeLabel);
		add(strip2);
    }
    
    /**
     * Icon panel that plays nice with FlowPanels
     * 
     */
    private class SocialNetworkIcon extends SimplePanel {
    	public SocialNetworkIcon(String url, String imgSrc) {
    		super();
        	getElement().setAttribute("style", "float: left;");
    		HTML label = new HTML("<a href=\"" + url + "\"><img src=\"" + imgSrc + "\" /></a>");
    		add(label);
    	}
    }
}


