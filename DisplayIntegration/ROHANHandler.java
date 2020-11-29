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

import java.util.Map;

import com.xyzcompany.cs.hcms.core.ComponentSetupArgs;
import com.xyzcompany.cs.hcms.core.ComponentSetupException;
import com.xyzcompany.cs.hcms.core.CoreThreadException;
import com.xyzcompany.cs.hcms.core.UnitManager;
import com.xyzcompany.cs.hcms.core.components.SerialAdapter;
import com.xyzcompany.cs.hcms.core.services.ServiceStartArgs;
import com.xyzcompany.cs.hcms.core.services.ServiceStartException;
import com.xyzcompany.cs.hcms.core.services.ServiceStopArgs;
import com.xyzcompany.cs.hcms.core.services.ServiceStopException;
import com.xyzcompany.cs.hcms.core.services.handlers.Handler;
import com.xyzcompany.cs.hcms.core.services.handlers.HandlerPropertyNotFoundException;
import com.xyzcompany.cs.hcms.core.services.handlers.PropertyChangeTimeoutArgs;
import com.xyzcompany.cs.hcms.core.services.handlers.PropertyChangedArgs;
import com.xyzcompany.cs.hcms.core.services.handlers.PropertyMap;
import com.xyzcompany.cs.hcms.core.utils.TriStateConstants;


/**
 * The Class ROHANHandler.
 */
public class ROHANHandler extends Handler {

	/** Serial Version UID */
	private static final long serialVersionUID = 1L;
	/** Bit Strike Threshold Value */
	private static final int MAX_BIT_FAULT_THRESHOLD = 3;
	/** Local copy of BIT Strike Counter state */
	private int mNoOfBITFaults = 0;
	/** Default Poll time */
	private static final int DEFAULT_POLL_TIME = 2000;
	/** Instance variable for the serial adapter this RGMM uses. */
	private SerialAdapter serialAdapter;
	/** RGM Command Processor Reference */
	private transient ROHANCommandProcessor mRGMCommandProcessor;
	/** Thread Reference for RGM Command Processor */
	private transient Thread mRGMCommandProcessingThread;
	/** Holds the RGM Settings read from LCP */
	private transient ROHANSettings rgmSettings;
	/** Indicates whether RGM is online */
	private String rgmBITStatus = TriStateConstants.UNKNOWN.getValue();
	/** Handle Queue timeout value */
	private int mHandlerQueueTimeout;
	/** Handler state value for online status */
	private String onlineStateValue;
	
	/** Constant for inserting in log statements */
	private static final String TAG = "ROHANHandler::"; 
	
	/** Make sure the initialization commands are sent */
	private boolean isRestore = false;
	
	
	/* Valid command value bool for sync logic between Req and Act States*/
	
	/** Make sure the initialization commands are sent */
	private boolean isMonitorInitialized = false;
	/** The AspectRatioSetting map. */
	private Map<String, String> aspectRatioSettingMap;
	/** The PowerStateSetting map. */
	private Map<String, String> powerStateSettingMap;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.handlers.Handler#onSetup(java.lang.
	 * Object, com.xyzcompany.cs.hcms.core.ComponentSetupArgs)
	 */
	@Override
	protected void onSetup(final Object source, final ComponentSetupArgs args) throws ComponentSetupException {
		super.onSetup(source, args);

		serialAdapter = (SerialAdapter) getComponents().getFirstByClass(SerialAdapter.class);

		if (serialAdapter != null && serialAdapter.isTXEControllable()) {
			serialAdapter.setTXE(true);
		}

		aspectRatioSettingMap = getSettingMap("AspectRatioSettingMap");
		powerStateSettingMap = getSettingMap("PowerStateSettingMap");
		importSettings();

		mRGMCommandProcessor = new ROHANCommandProcessor(serialAdapter, this);
		

		try {

			/** Create command processing thread */
			mRGMCommandProcessingThread = UnitManager.Threading.createThread(this, mRGMCommandProcessor,
					"ROHAN Command Processor");

			

		} catch (CoreThreadException e) {
			UnitManager.Logging.logSevere(e);
		}
	}

	/**
	 * Imports all settings
	 */
	private void importSettings() {

		mHandlerQueueTimeout = getSetting(SETTING_HANDLER_QUEUE_TIMEOUT, DEFAULT_POLL_TIME);
		if (mHandlerQueueTimeout == 0) {
			mHandlerQueueTimeout = DEFAULT_POLL_TIME;
		}

		rgmSettings = new ROHANSettings();
		rgmSettings.setCommandProcessorRetryInterval(
				getSetting("CommandProcessThreadInterval", ROHANConstants.CMD_PROC_SLEEP_INTERVAL));
		rgmSettings.setResponseTimeout(getSetting("ResponseTimeout", ROHANConstants.DEFAULT_RESP_TIMEOUT));
		rgmSettings.setMaxBrightness((byte) getSetting("MaxBrightness", ROHANConstants.MAX_BRIGHTNESS));
		rgmSettings.setMaxHue((byte) getSetting("MaxHue", ROHANConstants.MAX_HUE));
		rgmSettings.setMaxContrast((byte) getSetting("MaxContrast", ROHANConstants.MAX_CONTRAST));
		rgmSettings.setMaxSaturation((byte) getSetting("MaxSaturation", ROHANConstants.MAX_SATURATION));
		rgmSettings.setMaxBacklightLevel((byte) getSetting("MaxBackLight", ROHANConstants.MAX_BKLT_LEVEL));
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.handlers.Handler#onStarted(java.
	 * lang.Object, com.xyzcompany.cs.hcms.core.services.ServiceStartArgs)
	 */
	@Override
	protected void onStarted(final Object source, final ServiceStartArgs args) throws ServiceStartException {
		


		/** Start the threads */
		mRGMCommandProcessingThread.start();

		try {
	            /** Set the power state and backlight */
	            mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_POWER_STATE.getPropertyName(),
							getProperty(ROHANProperties.RGM_POWER_STATE.getPropertyName())));

		} catch (HandlerPropertyNotFoundException hpne) {
			UnitManager.Logging.logSevere(
					TAG + "onStarted(): Property was not found in the database -> " + hpne.toString());
		}

		super.onStarted(source, args);
	}
	


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.handlers.Handler#onPropertyChanged(
	 * java.lang.Object,
	 * com.xyzcompany.cs.hcms.core.services.handlers.PropertyChangedArgs)
	 */
	@Override
	protected void onPropertyChanged(final Object source, final PropertyChangedArgs args) {
		
	    if (mRGMCommandProcessingThread != null) {
	        if (rgmBITStatus.equals(Boolean.TRUE.toString())) {
	            String propName = args.getPropertyName();
	            int propValue = Integer.parseInt(args.getPropertyValue());
	            
	            if ((propName.equalsIgnoreCase(ROHANProperties.RGM_HUE.getPropertyName()) ||
	                    propName.equalsIgnoreCase(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName()) ||
	                    propName.equalsIgnoreCase(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName()) ||
	                    propName.equalsIgnoreCase(ROHANProperties.RGM_SATURATION.getPropertyName()) ||
	                    propName.equalsIgnoreCase(ROHANProperties.RGM_CONTRAST.getPropertyName())) && 
	            		((propValue >=ROHANConstants.MIN_SET_COMMAND_VAL) && (propValue <=ROHANConstants.MAX_SET_COMMAND_VAL))
	                    || propName.equalsIgnoreCase(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName()) && 
	                    ((propValue >= ROHANConstants.MIN_ASPECT_MAP_INDEX) && (propValue <= ROHANConstants.MAX_ASPECT_MAP_INDEX))
	                    || propName.equalsIgnoreCase(ROHANProperties.RGM_POWER_STATE.getPropertyName()) && 
	                    ((propValue == ROHANConstants.MIN_POWER_STATE_MAP_INDEX) || (propValue == ROHANConstants.
	                    																				MAX_POWER_STATE_MAP_INDEX))) {
	                if (isDebug()) {
	                    logDebug(TAG + "onPropertyChanged(): Adding the command to RGM processor Q");
	                    logDebug(TAG + "onPropertyChanged(): --onPropertyChanged.commandQ: " + args.getPropertyName() + "/"
	                            + args.getPropertyValue());
	                }
	                mRGMCommandProcessor.addCommand(args);
	                
	            } else if(propName.equalsIgnoreCase(ROHANProperties.RGM_RESTORE_SETTINGS.getPropertyName()) && 
	                    ((propValue == ROHANConstants.DONT_DO_RESTORE_SETTINGS) || (propValue == ROHANConstants.DO_RESTORE_SETTINGS))) {
	            	if(propValue == ROHANConstants.DO_RESTORE_SETTINGS)
	            	  setRestore(true);
	            }else {
	                UnitManager.Logging.logSevere(TAG + "onPropertyChanged(): Invalid command value");
	            }
            } else {
                    if (isDebug()) {
                        logDebug(TAG + "onPropertyChanged(): Rosen Gulfstream Monitor not online.");
                    } 
                }
	    } else {
            if (isDebug()) {
            logDebug(TAG + "onPropertyChanged(): CommandProcessorThread is null");
            }
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xyzcompany.cs.hcms.core.services.handlers.Handler#
	 * onPropertyChangedTimeout(java.lang.Object,
	 * com.xyzcompany.cs.hcms.core.services.handlers.PropertyChangeTimeoutArgs)
	 */
	@Override
	protected void onPropertyChangedTimeout(final Object source, final PropertyChangeTimeoutArgs args) {

		try {
	        mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_PING.getPropertyName(),
	                getProperty(ROHANProperties.RGM_PING.getPropertyName())));
		} catch (HandlerPropertyNotFoundException e) {
		    UnitManager.Logging.logSevere(TAG + " onPropertyChangedTimeout(): PING property not found " + e.getMessage());
		}
		
		/* Restore the monitor to its default values */
		if(isRestore) {
			restoreVideoSettings();
		}
			
		
		if (!isMonitorInitialized) {
            
            /* Initialize the ROHAN */
             initializeRGM();
		}

		super.onPropertyChangedTimeout(source, args);
	}
	
	/**
	 * Initializing the ROHAN Monitor
	 */
	
    public void initializeRGM() {
    	
    	
    	if (isInfo()) {
			logInfo(TAG + "initializeRGM(): Initializing the ROHAN Monitor");
    	}
        try {
            
            /** Read and populate Electronic Name Plate information */
        	
           if (getProperty(ROHANProperties.HW_PART_NUMBER.getPropertyName()).equalsIgnoreCase(TriStateConstants.UNKNOWN.getValue())) {
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.HW_PART_NUMBER.getPropertyName(),
                       getProperty(ROHANProperties.HW_PART_NUMBER.getPropertyName())));
           }
 
           if (getProperty(ROHANProperties.FW_PART_NUMBER.getPropertyName()).equalsIgnoreCase(TriStateConstants.UNKNOWN.getValue())) {
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.FW_PART_NUMBER.getPropertyName(),
                       getProperty(ROHANProperties.FW_PART_NUMBER.getPropertyName())));
           }
 
           if (getProperty(ROHANProperties.SERIAL_NUMBER.getPropertyName()).equalsIgnoreCase(TriStateConstants.UNKNOWN.getValue())) {
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.SERIAL_NUMBER.getPropertyName(),
                       getProperty(ROHANProperties.SERIAL_NUMBER.getPropertyName())));
           }

            /** Set monitor parameters to initial DB values */
           
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName(),
                       											getProperty(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName())));
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName(),
            		   											getProperty(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName())));
               
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_CONTRAST.getPropertyName(),
            		   											getProperty(ROHANProperties.RGM_CONTRAST.getPropertyName())));
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName(),
            		   											getProperty(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName())));
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_HUE.getPropertyName(),
            		   											getProperty(ROHANProperties.RGM_HUE.getPropertyName())));
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_SATURATION.getPropertyName(),
                       											getProperty(ROHANProperties.RGM_SATURATION.getPropertyName())));
           
               mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_POWER_STATE.getPropertyName(),
                       											getProperty(ROHANProperties.RGM_POWER_STATE.getPropertyName())));
         
			isMonitorInitialized = true;
           
        } catch (HandlerPropertyNotFoundException hpne) {
            UnitManager.Logging.logSevere(TAG +
                    "initializeRGM(): Property was not found in the database -> " + hpne.toString());
        }
       
    }
    
    
    
    /* Restore the monitor's video settings to their default values */
    
    public void restoreVideoSettings() {
    	
    	if (isInfo()) {
			logInfo(TAG + "restoreVideoSettings(): Restoring all video settings of the ROHAN Monitor");
    	}
    	
    	PropertyMap statesToLSM = new PropertyMap();
    	
    	/* Add all commands to queue with default values */
		
    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName(),
    			ROHANConstants.DEFAULT_ASPECT_RATIO_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName(),
    			ROHANConstants.DEFAULT_BACKLIGHT_BRIGHTNESS_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_CONTRAST.getPropertyName(),
    			ROHANConstants.DEFAULT_CONTRAST_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName(),
    			ROHANConstants.DEFAULT_PIXEL_BRIGHTNESS_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_HUE.getPropertyName(),
    			ROHANConstants.DEFAULT_HUE_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_SATURATION.getPropertyName(),
    			ROHANConstants.DEFAULT_SATURATION_VALUE));

    	mRGMCommandProcessor.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_POWER_STATE.getPropertyName(),
    			ROHANConstants.DEFAULT_POWER_STATE_VALUE));
    	
    	/* Set the states accordingly */

    	statesToLSM.put(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName(),ROHANConstants.DEFAULT_ASPECT_RATIO_VALUE);
    	statesToLSM.put(ROHANProperties.ASPECT_RATIO.getPropertyName(),ROHANConstants.DEFAULT_ASPECT_RATIO_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName(),ROHANConstants.DEFAULT_BACKLIGHT_BRIGHTNESS_VALUE);
    	statesToLSM.put(ROHANProperties.BACKLIGHT_BRIGHTNESS.getPropertyName(),ROHANConstants.DEFAULT_BACKLIGHT_BRIGHTNESS_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_CONTRAST.getPropertyName(),ROHANConstants.DEFAULT_CONTRAST_VALUE);
    	statesToLSM.put(ROHANProperties.CONTRAST.getPropertyName(),ROHANConstants.DEFAULT_CONTRAST_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName(),ROHANConstants.DEFAULT_PIXEL_BRIGHTNESS_VALUE);
    	statesToLSM.put(ROHANProperties.PIXEL_BRIGHTNESS.getPropertyName(),ROHANConstants.DEFAULT_PIXEL_BRIGHTNESS_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_HUE.getPropertyName(),ROHANConstants.DEFAULT_HUE_VALUE);
    	statesToLSM.put(ROHANProperties.HUE.getPropertyName(),ROHANConstants.DEFAULT_HUE_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_SATURATION.getPropertyName(),ROHANConstants.DEFAULT_SATURATION_VALUE);
    	statesToLSM.put(ROHANProperties.SATURATION.getPropertyName(),ROHANConstants.DEFAULT_SATURATION_VALUE);
    	
    	statesToLSM.put(ROHANProperties.RGM_POWER_STATE.getPropertyName(),ROHANConstants.DEFAULT_POWER_STATE_VALUE);
    	statesToLSM.put(ROHANProperties.POWER_STATE.getPropertyName(),ROHANConstants.DEFAULT_POWER_STATE_VALUE);

    	statesToLSM.put(ROHANProperties.RGM_RESTORE_SETTINGS.getPropertyName(),Integer.toString(ROHANConstants.DONT_DO_RESTORE_SETTINGS));
    	
    	if (!statesToLSM.isEmpty())
			setProperty(statesToLSM);
   
    	setRestore(false);
    }
    
    // if(rgmSaturation != saturation)
	//statesToLSM.put(ROHANProperties.SATURATION.getPropertyName(),Byte.toString(rgmSaturation));
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.handlers.Handler#onStopped(java.
	 * lang.Object, com.xyzcompany.cs.hcms.core.services.ServiceStopArgs)
	 */
	@Override
	protected void onStopped(final Object source, final ServiceStopArgs args) throws ServiceStopException {
		stopAllThreads();
		super.onStopped(source, args);
	}


	/**
	 * Updates the RGM Online Status
	 * 
	 * @param status Online Status
	 */
	public void setRGMStatus(final boolean status) {
		if (!status) {
			if (mNoOfBITFaults < MAX_BIT_FAULT_THRESHOLD) {
				mNoOfBITFaults++;
				return;
			} else {
				mNoOfBITFaults = 0;
			}
		} else {
			mNoOfBITFaults = 0;
		}

		try {
			onlineStateValue = getProperty(ROHANProperties.IS_RGM_ONLINE.getPropertyName());
		} catch (HandlerPropertyNotFoundException e) {
			UnitManager.Logging.logSevere(TAG + "setRGMStatus(): " + e.getMessage());
		}

		if (onlineStateValue.equalsIgnoreCase(TriStateConstants.UNKNOWN.getValue())
				|| Boolean.parseBoolean(onlineStateValue) != status) {
			rgmBITStatus = Boolean.toString(status);
			setProperty(ROHANProperties.IS_RGM_ONLINE.getPropertyName(), rgmBITStatus);
			
			if(isDebug()) {
			logDebug(TAG + "setRGMStatus(): set to: " + rgmBITStatus);
			}
			if (status) {
				/**
				 * Set the power state and Backlight as mentioned in the SD as RGM recovered its online
				 * status
				 */
				try {
					mRGMCommandProcessor
							.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_POWER_STATE.getPropertyName(),
									getProperty(ROHANProperties.RGM_POWER_STATE.getPropertyName())));
					mRGMCommandProcessor
					.addCommand(new PropertyChangedArgs(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName(),
							getProperty(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName())));
					
				} catch (HandlerPropertyNotFoundException e) {
					UnitManager.Logging.logSevere(TAG + "setRGMStatus(): " + e.getMessage());
				}

			}
		}
	}

	/**
	 * Returns the RGM Online Status
	 * 
	 * @return boolean
	 */
	public String getrgmStatus() {
		return rgmBITStatus;
	}

	/**
	 * Returns the AspectRatioSettingMap
	 * 
	 * @return AspectRatioSettingMap
	 */
	public Map<String, String> getAspectRatioSettingMap() {
		return aspectRatioSettingMap;
	}

	/**
	 * Returns the PowerStateSettingMap
	 * 
	 * @return PowerStateSettingMap
	 */
	public Map<String, String> getPowerStateSettingMap() {
		return powerStateSettingMap;
	}

	/**
	 * Returns the RGM Settings
	 * 
	 * @return RGMSettings
	 */
	public ROHANSettings getRGMSettings() {
		return rgmSettings;
	}

	/**
	 * Returns the property value
	 * 
	 * @param propertyName Property Name
	 * @param defaultValue Default Property Value
	 * @return Property Value
	 */
	public String getPropertyValue(final String propertyName, final String defaultValue) {

		try {
			return getProperty(propertyName);
		} catch (HandlerPropertyNotFoundException e) {
			UnitManager.Logging.logSevere("ROHANHandler: PropertyNotFound Exception " + e.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Gets the m RGM command processor.
	 *
	 * @return the m RGM command processor
	 */
	public ROHANCommandProcessor getmRGMCommandProcessor() {
		return mRGMCommandProcessor;
	}

	/**
	 * Gets the m RGM command processing thread.
	 *
	 * @return the m RGM command processing thread
	 */
	public Thread getmRGMCommandProcessingThread() {
		return mRGMCommandProcessingThread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xyzcompany.cs.hcms.core.services.Service#isInfo()
	 */
	@Override
	public boolean isInfo() {
		return super.isInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xyzcompany.cs.hcms.core.services.Service#isDebug()
	 */
	@Override
	public boolean isDebug() {
		return super.isDebug();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.Service#logInfo(java.lang.String)
	 */
	@Override
	public void logInfo(final String message) {
		super.logInfo(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xyzcompany.cs.hcms.core.services.Service#logDebug(java.lang.String)
	 */
	@Override
	public void logDebug(final String message) {
		super.logDebug(message);
	}

	/**
	 * Updates the handler queue timeout value
	 * 
	 * @param timeout Timeout Value
	 */
	public void changeHandlerQueueTimeout(final int timeout) {
		setPollTimeout(timeout);
	}

	/**
	 * Reverts to the original poll time out value
	 */
	public void revertToOriginalPollTimeout() {
		setPollTimeout(mHandlerQueueTimeout);
	}

	/**
	 * Stops all the running threads
	 */
	private void stopAllThreads() {
		final long threadJoinDuration = 2000;


		if (mRGMCommandProcessor != null) {
			mRGMCommandProcessor.stopThread();
		}

		if (mRGMCommandProcessingThread != null && mRGMCommandProcessingThread.isAlive()) {
			try {
				mRGMCommandProcessingThread.join(threadJoinDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				mRGMCommandProcessingThread.interrupt();
				mRGMCommandProcessingThread.join(threadJoinDuration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isRestore() {
		return isRestore;
	}

	public void setRestore(boolean isRestore) {
		this.isRestore = isRestore;
	}

}
