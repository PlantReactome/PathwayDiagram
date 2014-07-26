package org.reactome.diagram.analysis.model;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface PathwaySummary {

    Long getStId();

    Long getDbId();

    String getName();

    SpeciesSummary getSpecies();

    EntityStatistics getEntities();

    ReactionStatistics getReactions();
}
