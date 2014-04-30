package org.reactome.analysis.model;

import java.util.List;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface EntityStatistics extends Statistics {

    Double getpValue();

    Double getFdr();

    List<Double> getExp();
}
