package com.eldonad.ccgalacticraftaddon.utils;

public class SyncTask <T> {
	public enum SyncTaskState{
		Requested,
		Processed
	};
	
	private SyncTaskState state;
	public SyncTaskState getState() {
		return state;
	}
	public void process(T data) {
		state = SyncTaskState.Processed;
		processedData = data;
	}
	
	public T getData(){
		if (state == SyncTaskState.Processed) {
			return processedData;
		}
		else throw new RuntimeException("SyncTask read withoud having been processed");
	}
	
	private T processedData;
	
	
}
