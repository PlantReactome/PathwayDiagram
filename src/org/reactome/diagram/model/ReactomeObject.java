/*
 * Created on Sep 19, 2011
 *
 */
package org.reactome.diagram.model;

/**
 * The highest level of objects in Reactome's hiearchy.
 * @author gwu and weiserj
 *
 */
public class ReactomeObject {
    
    private String displayName;
    private Long reactomeId;
    private Integer id;
    
    public ReactomeObject() {
    }
        
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return this.id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getReactomeId() {
        return reactomeId;
    }

    public void setReactomeId(Long reactomeId) {
        this.reactomeId = reactomeId;
    }
}
