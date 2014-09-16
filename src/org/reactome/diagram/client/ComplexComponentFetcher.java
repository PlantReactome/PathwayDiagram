/*
 * Created on Feb 2013
 *
 */
package org.reactome.diagram.client;

import java.util.ArrayList;
import java.util.List;

import org.reactome.diagram.model.CanvasPathway;
import org.reactome.diagram.model.CompositionalNode;
import org.reactome.diagram.model.GraphObject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * A specialized PlugInSupportCanvas that is used to overlay expression data on to a pathway.
 * @author gwu
 *
 */
public abstract class ComplexComponentFetcher {
    private ComplexComponentRequests complexComponentRequests;
    
    public ComplexComponentFetcher() {
    	complexComponentRequests = new ComplexComponentRequests();
    }
    
    public void getComplexNodeComponentData(CanvasPathway pathway) {
    	if (pathway == null) {
    		performActionAfterComponentsObtained();
    		return;
    	}
    		
    	List<CompositionalNode> complexes = new ArrayList<CompositionalNode>();
    	for (GraphObject entity : pathway.getGraphObjects()) {
    		if (isComplexWithoutComponentData(entity))
    			complexes.add((CompositionalNode) entity);
    	}
    	getComplexNodeComponentData(complexes);
    }
    
    public void getComplexNodeComponentData(List<CompositionalNode> complexes) {
    	complexComponentRequests.cancelAllRequestsInProgress();
    	
    	if (complexes == null || complexes.isEmpty()) {
    		performActionAfterComponentsObtained();
    		return;
    	}
    	
    	complexComponentRequests.setAllRequestsAdded(false);
    	for (CompositionalNode complex : complexes) {
    		if (!complex.participatingMoleculesObtained()) {
    			Request request = PathwayDiagramController.getInstance().getParticipatingMolecules(complex.getReactomeId(), 
    																 setParticipatingMolecules(complex));
    			complexComponentRequests.add(request);
    		}
    	}
    	complexComponentRequests.setAllRequestsAdded(true);
    	performActionIfReady();
    }
    
    private RequestCallback setParticipatingMolecules(final CompositionalNode complex) {
    	return new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				complex.setParticipatingMolecules().onResponseReceived(request,  response);
				removeComplexComponentRequest(request);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				complex.setParticipatingMolecules().onError(request, exception);
				removeComplexComponentRequest(request);
			}
    		
    	};
    }
    
    private void removeComplexComponentRequest(Request request) {
    	complexComponentRequests.remove(request);
    	performActionIfReady();
    }
    
    private void performActionIfReady() {
		if (complexComponentRequests.allComplexNodesReady())
			performActionAfterComponentsObtained();
	}
    
    protected abstract void performActionAfterComponentsObtained();

	private boolean isComplexWithoutComponentData(GraphObject entity) {
    	return (entity.isSetOrComplex() && !((CompositionalNode) entity).participatingMoleculesObtained());
    }
    
    private class ComplexComponentRequests {
    	private List<Request> requests;
    	private boolean allAdded;
    	
    	public ComplexComponentRequests() {
    		requests = new ArrayList<Request>();
    		allAdded = true;
    	}
    	
    	public void add(Request request) {
    		requests.add(request);
    	}
    	
		public void remove(Request request) {
			requests.remove(request);
		}

		public void setAllRequestsAdded(boolean allAdded) {
			this.allAdded = allAdded;
		}
		
		public boolean allRequestsAdded() {
			return allAdded;
			
		}
		
		public boolean allComplexNodesReady() {
			return allRequestsAdded() && requests.isEmpty();
		}
		
		public void cancelAllRequestsInProgress() {
			for (Request request : requests)
				request.cancel();
			
			setAllRequestsAdded(false);
			
			requests.clear();
		}
    }
}
