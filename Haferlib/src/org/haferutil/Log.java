package org.haferutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Log {
	
	/**
	 * Default log for use singleton-style.
	 */

	private static Log defaultLog;
	
	public static Log getDefaultLog() {
		if (defaultLog == null) 
			initDefaultLog();
		return defaultLog;
	}
	
	private static void initDefaultLog() {
		defaultLog = new Log();
		defaultLog.addObserver(System.out);
	}
	
	/**
	 * Tags for different log calls.
	 */
	
	public static final String ERROR_TAG = "<ERROR> ";
	public static final String DEBUG_TAG = "<DEBUG> ";
	public static final String INFO_TAG = "<INFO> ";
	public static final String NOTIFY_FAIL_TAG = "<NOTIFY FAIL> ";
	
	/**
	 * The log observer class. Everything within is immutable.
	 */
	private final class LogObserver {
		
		private final Appendable observer;	// The observer.
		private final String[] tagFilters;	// The tags to filter by. If null, the observer receives all messages.
		private final boolean filterMode;	// How the tags are filtered. If true, then the observer is only notified of
											// messages tagged with an element of tagFilters. If false, then the observer
											// is only notified of messages tagged with a string that is not in tagFilters.
		
		private LogObserver(Appendable observer, String[] tagFilters, boolean filterMode) {
			this.observer = observer;
			this.tagFilters = tagFilters;
			this.filterMode = filterMode;
		}
		
		private boolean inclusiveCheck(String tag) {
			for (String filter : tagFilters) {
				if (tag.equals(filter))
					return true;
			}
			return false;
		}
		
		private boolean exclusiveCheck(String tag) {
			for (String filter : tagFilters) {
				if (tag.equals(filter))
					return false;
			}
			return true;
		}
		
		private boolean passesFilter(String tag) {
			if (tagFilters == null)
				return true;
			if (filterMode)
				return inclusiveCheck(tag);
			else
				return exclusiveCheck(tag);
		}
		
		private void append(String s, String tag) throws IOException {
			if (passesFilter(tag)) {
				observer.append(tag);
				observer.append(s);
				observer.append('\n');
			}
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			return observer.equals(o);
		}
		
		@Override
		public int hashCode() {
			return observer.hashCode();
		}
		
		@Override
		public String toString() {
			return "" + hashCode();
		}
	}
	
	/**
	 * The instance implementation.
	 */
	
	private List<String> contents;			// All of the contents of this log.
	private List<LogObserver> observers;	// The observers that this log pushes its output to.
	
	public Log() {
		contents = new LinkedList<>();
		observers = new ArrayList<>();
	}
	
	public void addObserver(Appendable o, String[] tagFilters, boolean filterMode) {
		if (observerIndex(o) == -1) {
			LogObserver lO = new LogObserver(o, tagFilters, filterMode);
			observers.add(lO);
		}
	}
	
	public void addObserver(Appendable o) {
		addObserver(o, null, false);
	}
	
	public void removeObserver(Appendable o) {
		int i = observerIndex(o);
		if (i != -1)
			observers.remove(i);
	}
	
	private int observerIndex(Appendable o) {
		int i = 0;
		for (LogObserver lO : observers) {
			if (lO.equals(o))
				return i;
			i++;
		}
		return -1;
	}
	
	private void append(String s, String tag) {
		// Add to the contents.
		contents.add(tag + s);
		
		// Notify the observers.
		for (LogObserver o : observers) {
			try {
				o.append(s, tag);
			}
			catch (IOException e) {
				contents.add(NOTIFY_FAIL_TAG + e.getMessage());
			}
		}
	}
	
	public void error(String s) {
		append(s, ERROR_TAG);
	}
	
	public void debug(String s) {
		append(s, DEBUG_TAG);
	}
	
	public void info(String s) {
		append(s, INFO_TAG);
	}
	
	public String getContents() {
		StringBuilder out = new StringBuilder();
		for (String s : contents) {
			out.append(s);
			out.append('\n');
		}
		return out.toString();
	}
}
