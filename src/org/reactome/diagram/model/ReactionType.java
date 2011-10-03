/*
 * Created on Oct 3, 2011
 *
 */
package org.reactome.diagram.model;

/**
 * Copied from the curator tool code base.
 * @TODO: Have to think out a way to use a common code based for two projects: gkdev and this canvas
 * based project. There are many classes are similar.
 * @author gwu
 *
 */
public enum ReactionType {
    
    TRANSITION  {// Default: should be used for types that are not one of the above four.
        public String toString() {return "Transition";}
    },    
    ASSOCIATION {
        public String toString() {return "Association";}
    },
    DISSOCIATION {
        public String toString() {return "Dissociation";}
    },
    OMITTED_PROCESS {
        public String toString() {return "Omitted Process";}
    },
    UNCERTAIN_PROCESS{
        public String toString() {return "Uncertain Process";}
    }
    
}
