/*
 *
 * Copyright 2020 xyz company, Inc. All Rights Reserved
 * NOTICE: The contents of this medium are proprietary to Rockwell
 * Collins, Inc. and shall not be disclosed, disseminated, copied,
 * or used except for purposes expressly authorized in written by
 * xyz company, Inc.
 *
 */

package com.xyzcompany.cs.hcms.core.components.rglfm;

/**
 * ROHAN Properties
 */
public enum ROHANProperties {
	
	/** Ping command*/
	RGM_PING("Ping") {
	    /** {@inheritDoc} */
	    public byte[] getBytes(final ROHANHandler rgmHandler,
		final String propValue) {
	        return ROHANUtil.framePacket(ROHANConstants.PING_CMD);
	    }

	},
	


    /** Get Firmware Part number command */
	FW_PART_NUMBER("FirmwarePartNumber") { // previously ProductTypeNo

		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			return ROHANUtil.framePacket(ROHANConstants.GET_FW_PART_NUMBER_CMD);
		}
	},
	
	/** Get Hardware Part number command */
	HW_PART_NUMBER("HardwarePartNumber") {
		
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			return ROHANUtil.framePacket(ROHANConstants.GET_HW_PART_NUMBER_CMD);
		}
	},
	
	/** Get Serial number command */
	SERIAL_NUMBER("SerialNumber") {
		
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			return ROHANUtil.framePacket(ROHANConstants.GET_SERIAL_NUMBER_CMD);
		}
	},
    
    /** Actual Power State command*/
    POWER_STATE("PowerStateAct") {},
    
    

    /** Actual Aspect command*/
    ASPECT_RATIO("AspectRatioAct") {},
  
 
    
    /** Actual Pixel Brightness */
    PIXEL_BRIGHTNESS("PixelBrightnessAct") {},
    
    /** Actual Backlight Brightness */
    BACKLIGHT_BRIGHTNESS("BacklightBrightnessAct") {},
    
  
	
    /** Actual Hue */
    HUE("HueAct") {},
    
    /** Actual Saturation */
    SATURATION("SaturationAct") {},
    
    
    
    /** Actual Contrast */
    CONTRAST("ContrastAct") {},
   
	
	/** Is  Online command */
	IS_RGM_ONLINE("IsRGMOnline"),
	
	
	/** Power State ON = Screen is ON and set to show video; 
	 * 	Power State Standby = Screen is OFf */
	RGM_POWER_STATE("PowerStateReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			
			return ROHANUtil.framePacket(ROHANConstants.SET_POWER_STATE_CMD, Byte.parseByte(propValue), rgmHandler);
		}
	},

		

	/** Aspect Ratio 
	 * Aspect Ratio normal
	 * Aspect Ratio fullscreen
	 * Aspect Ratio expanded letterbox
	 * Aspect Ratio pillarbox
	 * Aspect Ratio stretch
	 * */
	RGM_ASPECT_RATIO("AspectRatioReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			return ROHANUtil.framePacket(ROHANConstants.SET_ASPECT_CMD, Byte.parseByte(propValue), rgmHandler);
		}
	},
	
	
	/** Pixel Brightness level 
	 * 
	 * Pixel Brightness level 0 to 100
	 * */
	RGM_PIXEL_BRIGHTNESS("PixelBrightnessReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			byte inputValue = calcActualValueFromPercentage(Byte
					.parseByte(propValue), rgmHandler.getRGMSettings()
					.getMaxBrightness());
			return ROHANUtil.framePacket(ROHANConstants.SET_PIX_BRIGHTNESS_CMD, inputValue, rgmHandler);
		}
	},
	
	
	/** Backlight Brightness level
	 * 
	 * Backlight Brightness level 0 to 100
	 * */
	RGM_BACKLIGHT_BRIGHTNESS("BacklightBrightnessReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			byte inputValue = calcActualValueFromPercentage(Byte
					.parseByte(propValue), rgmHandler.getRGMSettings()
					.getMaxBacklightLevel());
			return ROHANUtil.framePacket(ROHANConstants.SET_BACKLIGHT_CMD, inputValue, rgmHandler);
		}
	},
	
	
	
	/** Hue 
	 * 
	 * Hue 0 to 100
	 * */
	RGM_HUE("HueReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			byte inputValue = calcActualValueFromPercentage(Byte
					.parseByte(propValue), rgmHandler.getRGMSettings()
					.getMaxHue());
			return ROHANUtil.framePacket(ROHANConstants.SET_HUE_CMD, inputValue, rgmHandler);
		}
	},
	

	
	
	/** Saturation 
	 * 
	 * Saturation 0 to 100
	 * */
	RGM_SATURATION("SaturationReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			byte inputValue = calcActualValueFromPercentage(Byte
					.parseByte(propValue), rgmHandler.getRGMSettings()
					.getMaxSaturation());
			return ROHANUtil.framePacket(ROHANConstants.SET_SATURATION_CMD, inputValue, rgmHandler);
		}
	},
	

	
	
	/** Contrast 
	 * 
	 * Contrast 0 to 100
	 * */
	RGM_CONTRAST("ContrastReq") {
		/** {@inheritDoc} */
		public byte[] getBytes(final ROHANHandler rgmHandler,
				final String propValue) {
			byte inputValue = calcActualValueFromPercentage(Byte
					.parseByte(propValue), rgmHandler.getRGMSettings()
					.getMaxContrast());
			return ROHANUtil.framePacket(ROHANConstants.SET_CONTRAST_CMD, inputValue, rgmHandler);
		}
	},
	
	
	 
		/** Restore to Factory settings command*/
		RGM_RESTORE_SETTINGS("RestoreFactorySettings") {},
		
		
	
	/** ThirdParty RGM Video Input Selection Status */
	RGM_CURR_VIDEO_INPUT_SELECTION_STATUS("CurrentRGMVideoMode");

	/** The property name */
	private String propertyName;
	/** Command Byte */
	private int command;
	/** Sub Command Byte */
	private int subCommand;
	/** Indicates whether the command executed or not */
	private boolean commandExecuted;
	/** Max Percentile */
	private static final double MAX_PERCENT = 100.0;

	/**
	 * Instantiates a new RGM property map.
	 * 
	 * @param propName
	 *            property name
	 */
	ROHANProperties(final String propName) {
		this.propertyName = propName;
	}

	/**
	 * Instantiates a new RGM property map.
	 * 
	 * @param cmd
	 *            Command
	 * @param propName
	 *            property name
	 */
	ROHANProperties(final int cmd, final String propName) {
		this.command = cmd;
		this.propertyName = propName;
	}

	/**
	 * Instantiates a new RGM property map.
	 * 
	 * @param cmd
	 *            Command
	 * @param subCmd
	 *            Sub command
	 * @param propName
	 *            Property Name
	 */
	ROHANProperties(final int cmd, final int subCmd, final String propName) {
		this.command = cmd;
		this.subCommand = subCmd;
		this.propertyName = propName;
	}

	/**
	 * Returns the command value
	 * 
	 * @return int
	 */
	public final int getCommand() {
		return this.command;
	}

	/**
	 * Returns the sub command value
	 * 
	 * @return int
	 */
	public final int getSubCommand() {
		return this.subCommand;
	}

	/**
	 * Indicates whether this command is executable or not
	 * 
	 * @return boolean
	 */
	public boolean isExecutableProperty() {
		return true;
	}

	/**
	 * Get the Instance from the Property name
	 * 
	 * @param propName
	 *            Property Name
	 * @return the property
	 */
	public static ROHANProperties getInstanceFromPropertyName(
			final String propName) {
		for (ROHANProperties input : ROHANProperties.values()) {
			if (input.getPropertyName().equalsIgnoreCase(propName)) {
				return input;
			}
		}
		return null;
	}

	/**
	 * Returns the ROHANProperties enum value for the given command
	 * 
	 * @param command
	 *            Command Value of ROHANProperties
	 * @return ROHANProperties
	 */
	public static ROHANProperties commandToEnum(final int command) {
		for (ROHANProperties input : ROHANProperties.values()) {
			if (input.getCommand() == command) {
				return input;
			}
		}
		return null;
	}

	/**
	 * Calculates the given percentage into actual value
	 * 
	 * @param percentage
	 *            Percentage
	 * @param maxValue
	 *            Max Value
	 * @return byte
	 */
	private static byte calcActualValueFromPercentage(final byte percentage,
			final byte maxValue) {

		return (byte) ((percentage / MAX_PERCENT) * maxValue);
	}

	/**
	 * Gets the property.
	 * 
	 * @return the property
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * *
	 * 
	 * Returns the bytes of the command
	 * 
	 * @param rgmHandler
	 *            Handler Object
	 * @param propValue
	 *            Property Value
	 * @return byte[]
	 */

	public byte[] getBytes(final ROHANHandler rgmHandler, final String propValue) {
		return ROHANUtil.framePacket(command);
	}

	/** Updates the command as executed	 */
	public void commandExecuted() {
		commandExecuted = true;
	}

	/**
	 * Indicates whether the command executed or not
	 * 
	 * @return boolean
	 */
	public final boolean isCommandExecuted() {
		return commandExecuted;
	}

	/**
	 * Main Test execution
	 */
	public static void main() {
		for (ROHANProperties prop : ROHANProperties.values()) {
			System.out.println(prop.toString() + " - " + prop.ordinal());
		}
	}
}
