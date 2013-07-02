/*
 * Created on Jul 1, 2013
 *
 */
package org.reactome.diagram.view;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.GraphObject;
import org.reactome.diagram.model.GraphObjectType;
import org.reactome.diagram.model.ModelHelper;
import org.reactome.diagram.model.Node;

/**
 * This class is used to generate the dafault color scheme for ELV.
 * @author gwu
 *
 */
public class DefaultColorScheme {
    private final String defaultColor = ModelHelper.makeColor("255 255 153".split(" "), 1.0d);
    private final String defaultProteinColor = ModelHelper.makeColor("165 215 145".split(" "), 1.0d);
    private final String oldComplexColor = ModelHelper.makeColor("204 255 255".split(" "), 1.0d);
    private final String defaultComplexColor = ModelHelper.makeColor("139 213 228".split(" "), 1.0d);
    private final String defaultChemicalColor = ModelHelper.makeColor("217 115 116".split(" "), 1.0d);
    private final String oldCompartmentLineColor = ModelHelper.makeColor("255 153 102".split(" "), 1.0d);
    private final String defaultNucleoplasmLineColor = ModelHelper.makeColor("60, 19, 120".split(", "), 1.0d);
    private final String defaultCytosolLineColor = ModelHelper.makeColor("255, 134, 55".split(", "), 1.0d);
    private final String oldCompartmentBgColor = ModelHelper.makeColor("250 240 240".split(" "), 0.5d);
    private final String defaultNucleoplasmBgColor = ModelHelper.makeColor("191, 169, 233".split(", "), 0.5d);
    private final String defaultCytosolBgColor = ModelHelper.makeColor("235, 178, 121".split(", "), 0.5d);
    private final String defaultCompartmentFillColor = ModelHelper.makeColor("255, 255, 255".split(", "), 0.6d);
    
    public DefaultColorScheme() {
    }
    
    public void applyScheme(CanvasPathway pathway) {
        if (pathway.getGraphObjects() == null)
            return;
        for (GraphObject obj : pathway.getGraphObjects()) {
            if (!(obj instanceof Node))
                continue;
            // Right now only need to handle nodes only
            Node node = (Node) obj;
            GraphObjectType type = obj.getType();
            switch (type) {
                case RenderableComplex :
                    applyComplexColor(node);
                    break;
                case RenderableProtein :
                    applyProteinColor(node);
                    break;
                case RenderableChemical :
                    applyChemicalColor(node);
                    break;
                case RenderableCompartment :
                    applyCompartmentColor(node);
                default :
                    if (node.getBgColor() == null)
                        node.setBgColor(defaultColor);
                    break;
            }
        }
    }
    
    private void applyCompartmentColor(Node compartment) {
        if (compartment.getDisplayName().equals("cytosol")) {
            if (compartment.getBgColor() == null || compartment.getBgColor().equals(oldCompartmentBgColor))
                compartment.setBgColor(defaultCytosolBgColor);
            if (compartment.getLineColor() == null || compartment.getLineColor().equals(oldCompartmentLineColor))
                compartment.setLineColor(defaultCytosolLineColor);
            compartment.setFillColor(defaultCompartmentFillColor);
        }
        else if (compartment.getDisplayName().equals("nucleoplasm")) {
            if (compartment.getBgColor() == null || compartment.getBgColor().equals(oldCompartmentBgColor))
                compartment.setBgColor(defaultNucleoplasmBgColor);
            if (compartment.getLineColor() == null || compartment.getLineColor().equals(oldCompartmentLineColor))
                compartment.setLineColor(defaultNucleoplasmLineColor);
            compartment.setFillColor(defaultCompartmentFillColor);
        }
    }
    
    private void applyComplexColor(Node complex) {
        if (complex.getBgColor() == null || complex.getBgColor().equals(oldComplexColor))
            complex.setBgColor(defaultComplexColor);
    }
    
    private void applyProteinColor(Node protein) {
        if (protein.getBgColor() == null)
            protein.setBgColor(defaultProteinColor);
    }
    
    private void applyChemicalColor(Node chemical) {
        if (chemical.getBgColor() == null)
            chemical.setBgColor(defaultChemicalColor);
    }
    
}
