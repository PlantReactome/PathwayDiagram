package org.reactome.diagram.analysis.model;

import java.util.Set;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface PathwayIdentifiers {

    Set<String> getResources();

    Set<PathwayIdentifier> getIdentifiers();
}
