/*
 *
 * Copyright 2020 xyz company, Inc. All Rights Reserved
 * NOTICE: The contents of this medium are proprietary to xyz company,
 * xyz company,, Inc. and shall not be disclosed, disseminated, copied,
 * or used except for purposes expressly authorized in written by
 * xyz company, Inc.
 *
 */

package com.xyzcompany.cs.hcms.core.components.rglfm;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.xyzcompany.cs.hcms.core.CoreThreadException;
import com.xyzcompany.cs.hcms.core.UnitManager;
import com.xyzcompany.cs.hcms.core.components.SerialAdapter;
import com.xyzcompany.cs.hcms.core.services.handlers.PropertyChangedArgs;

/**
 * The Class ROHANCommandProcessor.
 */
public class ROHANCommandProcessor implements Runnable {

	/** Retry Count */
	private static final int RETRY_COUNT = 3;
	
	/** Constant for inserting in log statements */
	private static final String TAG = "ROHANCommandProcessor::"; 
	
	/** Default Sleep Interval */
	private static final long DEF_SLEEP_INTERVAL = 100;	
	/** ThirdParty RGM Handler Reference. */
	private ROHANHandler rgmHandler = null;
	/** Serial Adapter Reference */
	private SerialAdapter serialAdapter;
	/** ThirdParty RGM Data Processor. */
	private transient ROHANDataProcessor mDataProcessor;
	/** Thread Reference of RGM Data Processor */
	private transient Thread mDataProcessorThread;
	/** Command Queue */
	private Queue<PropertyChangedArgs> commandQ;
	/** Command Queue Semaphore */
	private Semaphore commandQueueSemaphore = new Semaphore(1);
	/** Response Queue Semaphore */
	private Semaphore responseQueueSemaphore = new Semaphore(1);
	/** To indicate whether controller is waiting for response */
	private boolean waitingForResponse;
	/** Sleep Interval */
	private long sleepInterval = DEF_SLEEP_INTERVAL;
	/** Current Command in Process */
	private int currentCommandInProcess;
	/** Indicates that response received */
	private boolean responseReceived = false;
	/** Retry count */
	private byte retryCount = 0;
	/** Indicates whether the thread is stopped */
	private boolean isthreadStopped = false;

	/**
	 * Constructor
	 * 
	 * @param serAdapter
	 *            Serial Adapter
	 * @param handler
	 *            RGM Handler
	 */
	public ROHANCommandProcessor(final SerialAdapter serAdapter,
			final ROHANHandler handler) {
		commandQ = new LinkedList<PropertyChangedArgs>();
		serialAdapter = serAdapter;
		this.rgmHandler = handler;
		this.sleepInterval = rgmHandler.getRGMSettings()
				.getCommandProcessorRetryInterval();
		responseQueueSemaphore.acquireUninterruptibly();
	}

	/**
	 * Creates data processor thread
	 */
	private void createDataProcessorThread() {
		mDataProcessor = new ROHANDataProcessor(rgmHandler, serialAdapter, this);
		try {
			mDataProcessorThread = UnitManager.Threading.createThread(this,
					mDataProcessor, "ROHAN Data Processor Thread");
			mDataProcessorThread.start();
		} catch (CoreThreadException e) {
			UnitManager.Logging.logSevere(e);
		}
	}

	/**
	 * This method is used to add the commands into the command Q to be
	 * processed by the Command Processor
	 * 
	 * @param stateChangeEvent
	 *            New Property Event
	 */
	public final synchronized void addCommand(
			final PropertyChangedArgs stateChangeEvent) {
		UnitManager.Logging.logInfo(TAG + " addCommand(): Command added for the property: " + stateChangeEvent.getPropertyName());
		if (getCommandQueue() != null) {
			getCommandQueue().add(stateChangeEvent);
			commandQueueSemaphore.release();
		} else {
			UnitManager.Logging
					.logSevere(TAG + " CommandQ is empty..");
		}
	}

	/**
	 * Waits for the response
	 * 
	 * @return boolean
	 */
	protected final boolean waitForResponse() {
		try {
			waitingForResponse = true;
			/** Drain all permits */
			return responseQueueSemaphore.tryAcquire(rgmHandler
					.getRGMSettings().getResponseTimeout(),
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * Indicates whether the controller is waiting for response
	 * 
	 * @return boolean
	 */
	public final boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	/** {@inheritDoc} */
	public final void run() {
		/** Create the data processor thread */
		createDataProcessorThread();

		while (!isthreadStopped) {
			commandQueueSemaphore.acquireUninterruptibly();

			while (getCommandQueue().size() > 0) {
				if (rgmHandler.isInfo()) {
					rgmHandler.logInfo(TAG + "run: In Command Processor Q"
							+ " instance " + rgmHandler.getName());
				}
				processCommand(getCommandQueue().poll());
			}
		}
	}

	/**
	 * The main method of the Command processor that processes the commands from
	 * the RGM handler.
	 * 
	 * @param eventToProcess
	 *            Event to Process
	 */
	private void processCommand(final PropertyChangedArgs eventToProcess) {

		ROHANProperties propertyRecord = ROHANProperties
				.getInstanceFromPropertyName(eventToProcess.getPropertyName());
		
		UnitManager.Logging.logInfo(TAG + "processCommand(): Processing command: " + eventToProcess.getPropertyName()
		                                                + " for value: " + eventToProcess.getPropertyValue());
		
		if (propertyRecord != null && propertyRecord.isExecutableProperty()) {

			if (retryCount >= RETRY_COUNT) {
				
				if (rgmHandler.isInfo()) {
					rgmHandler.logInfo(TAG + "processCommand: RetryCount > allowable value"
							+ " instance : " + rgmHandler.getName());
				}
				rgmHandler.setRGMStatus(false);
			}

			retryCount = 0;
			do {
				responseReceived = false;

				if (propertyRecord.getCommand() >= ROHANProperties.RGM_PING.getCommand()) {
					if (rgmHandler.isInfo()) {
						rgmHandler.logInfo(TAG + "processCommand: "
								+ "Sending command bytes for the command '"
								+ propertyRecord.getPropertyName() + "'...");
					}

					boolean commandSucceed = false;
					/** Writes the command bytes to the serial port */
					try {
					     sendCommand(propertyRecord.getBytes(rgmHandler, eventToProcess.getPropertyValue()));
		                        commandSucceed = true;
					} catch (NumberFormatException ne) {
						UnitManager.Logging
								.logSevere(TAG + "processCommand:NumberFormatException : Invalid Property Value : "
										+ eventToProcess.getPropertyValue()
										+ " Property Name: "
										+ eventToProcess.getPropertyName()
										+ "Exp. Message: " + ne.getMessage());

					} catch (ArrayIndexOutOfBoundsException ae) {
						UnitManager.Logging
								.logSevere(TAG + "processCommand: ArrayIndexOutOfBoundsException : Invalid Property Value : "
										+ eventToProcess.getPropertyValue()
										+ " Property Name: "
										+ eventToProcess.getPropertyName()
										+ "Exp. Message: " + ae.getMessage());

					} catch (Exception e) {
						UnitManager.Logging.logSevere(TAG + "processCommand: Generic Exception "
								+ e.getMessage());
						e.printStackTrace();
					}

					if (!commandSucceed) {
						retryCount = RETRY_COUNT;
					} else {
		                   /**
	                     * This will update the active overlay type & last processed
	                     * overlay type
	                     */
	                    propertyRecord.commandExecuted();
					}
				} else {
					retryCount = 0;
					break;
				}

				retryCount++;

				if (!responseReceived) {
					waitForResponse();
				}

				waitingForResponse = false;
				if (responseReceived) {

					if (rgmHandler.isInfo()) {
						rgmHandler.logInfo(TAG + "processCommand: Response Received for the command : "
										+ propertyRecord.toString()
										+ " instamce : " + rgmHandler.getName());
					}
				}

				if (retryCount < RETRY_COUNT && !responseReceived) {

					if (rgmHandler.isInfo()) {
						rgmHandler.logInfo(TAG + "processCommand: Retry command for : "
								+ propertyRecord.toString() + " instance : "
								+ rgmHandler.getName());
					}

					try {
						Thread.sleep(sleepInterval);
					} catch (InterruptedException e) {
						UnitManager.Logging.logSevere(TAG + "processCommand: Interrupted Exception");
					} catch (Exception e) {
						UnitManager.Logging.logSevere(e);
					}
				}

			} while (retryCount < RETRY_COUNT && !responseReceived);

			currentCommandInProcess = -1;
		} else {
			UnitManager.Logging.logWarning(TAG + "processCommand: propertyRecord is not executable -> "
						+ eventToProcess.getPropertyName());
		}
	}

	/**
	 * Writes the given bytes to the serial port
	 * 
	 * @param commandBytes
	 *            Command Bytes
	 */
	private void sendCommand(final byte[] commandBytes) {
	    
		serialAdapter.write(commandBytes, commandBytes.length);
	}


	/**
	 * Returns the command queue
	 * 
	 * @return Queue<PropertyChangedArgs>
	 */
	private synchronized Queue<PropertyChangedArgs> getCommandQueue() {
		return commandQ;
	}

	/**
	 * Indicates whether queue is empty or not
	 * 
	 * @return boolean
	 */
	public final synchronized boolean isQueueEmpty() {
		return getCommandQueue().isEmpty();
	}

	/**
	 * Returns the command which is currently being processed
	 * 
	 * @return int - Command Value
	 */
	public final int getCurrentCommandInProcess() {
		return currentCommandInProcess;
	}

	/**
	 * Method to set the response processed flag
	 */
	public final void notifyResponseReceived() {
		responseReceived = true;
		retryCount = 0;
		responseQueueSemaphore.release();
	}

	/**
	 * Notify the command processor that NAK received
	 */
	public final void notifyNAKReceived() {
		responseQueueSemaphore.release();
	}

	/**
	 * Requests to stop the current thread
	 */
	public final void stopThread() {
		isthreadStopped = true;
		final long threadJoinDuration = 2000;

		mDataProcessor.stopThread();

		if (mDataProcessorThread != null && mDataProcessorThread.isAlive()) {
			try {
				mDataProcessorThread.join(threadJoinDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				mDataProcessorThread.interrupt();
				mDataProcessorThread.join(threadJoinDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
