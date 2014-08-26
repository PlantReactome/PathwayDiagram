package org.reactome.diagram.analysis.model;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface PathwaySummary {

    Long getStId();

    Long getDbId();

    String getName();

    boolean getLlp(); //get whether this pathway is lower level pathway

    SpeciesSummary getSpecies();

    EntityStatistics getEntities();

    ReactionStatistics getReactions();
}
