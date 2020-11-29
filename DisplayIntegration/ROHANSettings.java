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
 * The Class ROHANSettings.
 */
public class ROHANSettings {

	/** Default Interval */
	private static final long DEFAULT_INTERVAL = 1000;
	/** Maximum Brightness */
	private byte maxBrightness;
	/** Maximum Red */
	private byte maxRed;
	/** Maximum Blue */
	private byte maxBlue;
	/** Maximum Green */
	private byte maxGreen;
	/** Maximum Contrast */
	private byte maxContrast;
	/** Maximum Saturation */
	private byte maxSaturation;
	/** Maximum Hue */
	private byte maxHue;
	/** Maximum Backlight Level */
	private byte maxBacklightLevel;
	/** Restore Video settings */
	private boolean restoreVideoSettings;
	/** Is new firmware */
	private boolean isNewFirmware;
	/** Response Timeout */
	private long responseTimeout;
	/** Command Processor Retry Interval */
	private long commandProcessorRetryInterval = DEFAULT_INTERVAL;
	
	/**
	 * Constructor
	 */
	public ROHANSettings() {

	}

	/**
	 * Sets the maximum brightness value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxBrightness(final byte value) {
		this.maxBrightness = value;
	}

	/**
	 * Sets the maximum Red value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxRed(final byte value) {
		this.maxRed = value;
	}

	/**
	 * Sets the maximum Blue value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxBlue(final byte value) {
		this.maxBlue = value;
	}

	/**
	 * Sets the maximum Green value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxGreen(final byte value) {
		this.maxGreen = value;
	}

	/**
	 * Sets the maximum Contrast value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxContrast(final byte value) {
		this.maxContrast = value;
	}

	/**
	 * Sets the maximum Saturation value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxSaturation(final byte value) {
		this.maxSaturation = value;
	}

	/**
	 * Sets the maximum Hue value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxHue(final byte value) {
		this.maxHue = value;
	}

	/**
	 * Sets the maximum BacklightLevel value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setMaxBacklightLevel(final byte value) {
		this.maxBacklightLevel = value;
	}

	/**
	 * Sets whether video setting allowed or not
	 * 
	 * @param value
	 *            Value
	 */
	public final void setVideoSettingsAllowed(final boolean value) {
		this.restoreVideoSettings = value;
	}
	
	/**
	 * Sets whether new firmware or not
	 * 
	 * @param value
	 *            Value
	 */
	public final void setNewFirmware(final boolean value) {
		this.isNewFirmware = value;
	}

	/**
	 * Sets the response timeout value
	 * 
	 * @param value
	 *            Value
	 */
	public final void setResponseTimeout(final long value) {
		this.responseTimeout = value;
	}

	/**
	 * Sets the command processor retry interval
	 * 
	 * @param value
	 *            Value
	 */
	public final void setCommandProcessorRetryInterval(final long value) {
		this.commandProcessorRetryInterval = value;
	}

	/**
	 * Returns the maximum brightness
	 * 
	 * @return byte
	 */
	public final byte getMaxBrightness() {
		return maxBrightness;
	}
	
	/**
	 * Returns is new firmware
	 * 
	 * @return boolean
	 */
	public final boolean getIsNewFirmware() {
		return isNewFirmware;
	}

	/**
	 * Returns the maximum red value
	 * 
	 * @return byte
	 */
	public final byte getMaxRed() {
		return maxRed;
	}

	/**
	 * Returns the maximum blue value
	 * 
	 * @return byte
	 */
	public final byte getMaxBlue() {
		return maxBlue;
	}

	/**
	 * Returns the maximum green value
	 * 
	 * @return byte
	 */
	public final byte getMaxGreen() {
		return maxGreen;
	}

	/**
	 * Returns the maximum contrast value
	 * 
	 * @return byte
	 */
	public final byte getMaxContrast() {
		return maxContrast;
	}

	/**
	 * Returns the maximum saturation value
	 * 
	 * @return byte
	 */
	public final byte getMaxSaturation() {
		return maxSaturation;
	}

	/**
	 * Returns the maximum hue value
	 * 
	 * @return byte
	 */
	public final byte getMaxHue() {
		return maxHue;
	}

	/**
	 * Returns the maximum backlight level value
	 * 
	 * @return byte
	 */
	public final byte getMaxBacklightLevel() {
		return maxBacklightLevel;
	}

	/**
	 * Returns whether restoring video settings allowed or not
	 * 
	 * @return boolean
	 */
	public final boolean isRestoreVideoSettingsAllowed() {
		return restoreVideoSettings;
	}

	/**
	 * Returns the response time out value
	 * 
	 * @return long
	 */
	public final long getResponseTimeout() {
		return responseTimeout;
	}

	/**
	 * Returns the command processing retry interval
	 * 
	 * @return long
	 */
	public final long getCommandProcessorRetryInterval() {
		return commandProcessorRetryInterval;
	}

}
