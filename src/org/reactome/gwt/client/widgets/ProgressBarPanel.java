/* Copyright (c) 2009 European Bioinformatics Institute and Cold Spring Harbor Laboratory. */

package org.reactome.gwt.client.widgets;

import org.reactome.gwt.client.services.ProgressMonitorAsync;
import org.reactome.gwt.client.transport.ProgressInfo;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Creates a panel displaying a progress bar and possible also accompanying
 * comments.
 *
 * @author David Croft
 */
public class ProgressBarPanel extends VerticalPanel {
	private static double MAX = 2000.0;
	private static int DEFAULT_POLLING_DELAY = 500;
	private int pollingDelay = DEFAULT_POLLING_DELAY;
	private ProgressMonitorAsync progressMonitorAsync = null;
	private ProgressBar bar = new ProgressBar(0.0, MAX, 0.0);
	private Label progressCommentLabel = new Label();
	private double progress;
	
	public ProgressBarPanel() {
		super();
		add(progressCommentLabel);
		bar.setWidth("200px");
		setProgress(0.0);
		setProgressComment("Starting...");
		add(bar);
	}

	public void setProgressMonitorAsync(ProgressMonitorAsync progressMonitorAsync) {
		this.progressMonitorAsync = progressMonitorAsync;
	}

	public void setProgress(double progress) {
		// Prevent overshoot
		if (progress > MAX)
			progress = MAX;
		
		this.progress = progress;
		bar.setProgress(progress);
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void incrementProgress(double incrementalProgress) {
		setProgress(this.progress + incrementalProgress);
		bar.setProgress(progress);
	}

	public void setProgressComment(String comment) {
		progressCommentLabel.setText(comment);
	}

	public String getProgressComment() {
		return progressCommentLabel.getText();
	}

	public void finalize() {
		setProgress(MAX);
		progressCommentLabel.setText("Finished");
	}

	public void progressLooper(final double initialProgress, final double finalProgress) {
		Timer t = new Timer() {
			public void run() {
				progressMonitorAsync.getProgressInfo(
						new AsyncCallback<ProgressInfo>() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(ProgressInfo result) {
								if (getProgressComment() != null && getProgressComment().equals(result.comment)) {
									// Poll ever less frequently if the comment doesn't
									// change.  This is supposed to cut down the number
									// of requests and therefore speed things up a bit.
									pollingDelay *= 2;
								} else
									// Reset to default polling delay if comment changes
									pollingDelay = DEFAULT_POLLING_DELAY;
								
								setProgressComment(result.comment);
								setProgress(initialProgress + ((finalProgress - initialProgress) * result.progress.doubleValue())/100.0);
								if (result.progress.doubleValue() < 100.0)
									progressLooper(initialProgress, finalProgress);
							}
						});
			}
		};

		// Schedule the timer to run once.
		t.schedule(pollingDelay);
	}
}
