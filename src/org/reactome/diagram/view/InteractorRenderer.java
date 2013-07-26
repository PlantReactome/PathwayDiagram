/*
 * Created on Dec 13, 2012
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.Bounds;
import org.reactome.diagram.model.InteractorCanvasModel.InteractorConfidenceScoreColourModel;
import org.reactome.diagram.model.InteractorNode;
import org.reactome.diagram.model.Node;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author jweiser
 *
 */
public class InteractorRenderer extends NodeRenderer {
	private Context2DState contextState;
	
    public InteractorRenderer() {
        defaultLineWidth = 2.0d;
        defaultLineColor = CssColor.make("rgba(0, 0, 255, 1)");
    }

    public void setContextState(Context2DState contextState) {
    	this.contextState = contextState;
    }
    
    public void render(Context2d c2d, InteractorNode node, InteractorConfidenceScoreColourModel colouring) {
    	setNodeColour(node, colouring);
    	super.render(c2d, node); 
    }
    
    protected void drawNode(Context2d c2d, Node node) { 
    	if (((InteractorNode) node).getChemicalId() != null && !((InteractorNode) node).getChemicalId().isEmpty()) {
    	   	drawRectangle(node.getBounds(), c2d, node);
    		drawImage(c2d, (InteractorNode) node);
    	} else {
    		super.drawNode(c2d, node);
    	}
    }

    
    
    @Override
    protected void drawRectangle(Bounds bounds,
                                 Context2d context,
                                 Node node) {
        setStroke(context, node);
        int x = bounds.getX();
        int y = bounds.getY();
        int w = bounds.getWidth();
        int h = bounds.getHeight();
        context.beginPath();
        int x1 = x;
        int y1 = y;
        context.moveTo(x1, y1);
        x1 = x + w;
        context.lineTo(x1, y1);
        y1 = y + h;
        context.lineTo(x1, y1);
        x1 = x;
        context.lineTo(x1, y1);
        y1 = y;
        context.lineTo(x1, y1);
        context.closePath();
        double oldLineWidth = context.getLineWidth();
        context.fill();
        context.stroke();
        context.setLineWidth(oldLineWidth);
    }    
    
    protected void drawImage(final Context2d c2d, InteractorNode interactor) {
    	final Bounds bounds = interactor.getBounds();
    	
    	final Image image = new Image("http://www.ebi.ac.uk/chembldb/index.php/compound/displayimage/" + interactor.getChemicalId());
    	
    	image.addLoadHandler(new LoadHandler() {
    		
    		public void onLoad(LoadEvent event) {
    			c2d.save();

    			c2d.translate(contextState.getTranslateX(), contextState.getTranslateY());
    			c2d.scale(contextState.getScale(), contextState.getScale());
    			
    			ImageElement imgElement = ImageElement.as(image.getElement());
    			c2d.drawImage(imgElement, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    			RootPanel.get().remove(image);
    			
    			c2d.restore();
    		}
    	});
    	
    	image.setVisible(false);
    	RootPanel.get().add(image);
    }
    
    private void setNodeColour(InteractorNode node, InteractorConfidenceScoreColourModel colouring) {
    	String color;
    	
    	if (colouring != null && colouring.getColoringModeOn()) 
    		color = node.getScore() < colouring.getConfidenceLevelThreshold() ?
    				   colouring.getColourBelowThreshold() : colouring.getColourAboveThreshold();    
    	else
    		color = node.getDefaultColour();
    		
    	node.setBgColor(color);
    }
    
    public class Context2DState {
    	private Double translateX;
    	private Double translateY;
    	private Double scale;
    	
    	public Context2DState(Double translateX, Double translateY, Double scale) {
    		this.translateX = translateX;
    		this.translateY = translateY;
    		this.scale = scale;
    	}

		public Double getTranslateX() {
			return translateX;
		}

		public Double getTranslateY() {
			return translateY;
		}

		public Double getScale() {
			return scale;
		}    	
    }
}
