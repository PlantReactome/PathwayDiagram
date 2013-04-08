/* Copyright (c) 2011 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import org.reactome.gwt.client.analysis.ClientAnalysisStatus;
import org.reactome.web.site.client.common.view.VerticalSpacePanel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates a panel displaying a percentage progress bar and accompanying
 * comments.
 *
 * @author David Croft
 */
public class NewProgressBarPanel extends VerticalPanel {
	private ProgressBar bar = new ProgressBar(0.0, 100.0, 0.0);
	private Label progressCommentLabel = new Label();
	
	public NewProgressBarPanel() {
		super();
		add(progressCommentLabel);
		bar.setWidth("200px");
		setProgress(0.0);
		setProgressComment("Starting...");
		add(bar);
		add(new VerticalSpacePanel(50));
	}

	public void setProgress(double progress) {
		// Prevent overshoot
		if (progress > 100.0)
			progress = 100.0;
		
		bar.setProgress(progress);
	}
	
	public void setProgressComment(String comment) {
		progressCommentLabel.setText(comment);
	}

	public void showStatus(ClientAnalysisStatus status) {
		if (status == null)
			return;
		
		String title = status.getTitle();
		if (title != null && !title.isEmpty())
			setProgressComment(title);
		else
			setProgressComment(status.getName());
		setProgress(status.getProgress());
	}
}
