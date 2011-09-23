/*
 * Created on Sep 20, 2011
 *
 */
package org.reactome.diagram.model;

/**
 * This enum lists node types: e.g. protein, chemical, etc.
 * @author gwu
 *
 */
public enum GraphObjectType {
    // Node types
    RenderableProtein,
    RenderableChemical,
    RenderableEntity,
    RenderableCompartment,
    Note,
    ProcessNode,
    RenderableGene,
    RendearbleRNA,
    SourceOrSink,
    RenderableComplex,
    // Edge types
    RenderableReaction,
    RenderableIntearction,
    FlowLine
}
