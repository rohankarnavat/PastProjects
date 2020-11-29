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

/**
 * The Class ROHANConstants.
 */
public final class ROHANConstants {


	/** Contant for offset in the command response received
	 * for Hue, Saturation, Contrast, Pixel Brightness and Backlight Brightness */
	static final int POWER_COMMAND_OFFSET_INDEX = 29;

	/** Contant for offset in the command response received
	 * for Hue, Saturation, Contrast, Pixel Brightness and Backlight Brightness */
	static final int ASPECT_COMMAND_OFFSET_INDEX = 24;

	/** Contant for offset in the command response received
	 * for Hue, Saturation, Contrast, Pixel Brightness and Backlight Brightness */
	static final int COMMAND_OFFSET_INDEX = 8;

	/** Constant that represents the number of characters in the end of the string
	 * that are delimited to extract info needed from the command response received
	 * for Hue, Saturation, Contrast, Pixel Brightness and Backlight Brightness */
	static final int COMMAND_DELIMIT_INDEX = 4;
	
	/** do_restore_video_settings constant */
	static final String DO_RESTORE_CMD = "do_restore_video_settings";

	/** ping constant */
	static final String PING_CMD = "ping";
	
	/** get_fw_part_number constant */
	static final String GET_FW_PART_NUMBER_CMD = "get_fw_part_number";
	
	/** get_hw_part_number constant */
	static final String GET_HW_PART_NUMBER_CMD = "get_hw_part_number";

	/** get_serial_number constant */
	static final String GET_SERIAL_NUMBER_CMD = "get_serial_number";
		
	/** set_aspect constant */
	static final String SET_ASPECT_CMD = "set_aspect";
	
	/** set_contrast constant */
	static final String SET_CONTRAST_CMD = "set_contrast";

	/** set_power_state constant */
	static final String SET_SATURATION_CMD = "set_saturation";

	/** set_hue constant */
	static final String SET_HUE_CMD = "set_hue";

	/** set_pix_brightness constant */
	static final String SET_PIX_BRIGHTNESS_CMD = "set_pix_brightness";

	/** set_backlight constant */
	static final String SET_BACKLIGHT_CMD = "set_backlight";

	/** set_power_state constant */
	static final String SET_POWER_STATE_CMD = "set_power_state";
	
	/** get_fw_part_number response constant */
	static final String GET_FW_PART_NUMBER_RESPONSE = "OKAY get_fw_part_number";

	/** get_hw_part_number response constant */
	static final String GET_HW_PART_NUMBER_RESPONSE = "OKAY get_hw_part_number";

	/** get_serial_number response constant */
	static final String GET_SERIAL_NUMBER_RESPONSE = "OKAY get_serial_number";
	
	/** set_aspect response constant */
	static final String SET_ASPECT_RESPONSE = "OKAY set_aspect";
	
	/** set_contrast response constant */
	static final String SET_CONTRAST_RESPONSE = "OKAY set_contrast";

	/** set_power_state response constant */
	static final String SET_SATURATION_RESPONSE = "OKAY set_saturation";

	/** set_hue response constant */
	static final String SET_HUE_RESPONSE = "OKAY set_hue";
			
	/** set_pix_brightness response constant */
	static final String SET_PIX_BRIGHTNESS_RESPONSE = "OKAY set_pix_brightness";

	/** set_backlight response constant */
	static final String SET_BACKLIGHT_RESPONSE = "OKAY set_backlight";

	/** set_power_state response constant */
	static final String SET_POWER_STATE_RESPONSE = "OKAY set_power_state";

	/** First key value of Aspect Ratio setting map. */
	static final int MIN_ASPECT_MAP_INDEX = 1;

	/** Last key value of Aspect Ratio setting map. */
	static final int MAX_ASPECT_MAP_INDEX = 5;

	/** First key value of Power State setting map. */
	static final int MIN_POWER_STATE_MAP_INDEX = 1;

	/** Last key value of Power State setting map. */
	static final int MAX_POWER_STATE_MAP_INDEX = 2;

	/** Minimum integer value for Set Commands */
	static final int MIN_SET_COMMAND_VAL = 0;

	/** Maximum integer value for Set Commands */
	static final int MAX_SET_COMMAND_VAL = 100;

	/** Constant for not sending a restore command. */
	static final int DONT_DO_RESTORE_SETTINGS = 1;

	/** Constant for sending a restore command. */
	static final int DO_RESTORE_SETTINGS = 2;

	/** The Constant MAX_BRIGHTNESS. */
	public static final byte MAX_BRIGHTNESS = 100;

	/** The Constant MAX_SATURATION. */
	public static final byte MAX_SATURATION = 100;

	/** The Constant MAX_HUE. */
	public static final byte MAX_HUE = 100;

	/** The Constant MAX_CONTRAST. */
	public static final byte MAX_CONTRAST = 100;

	/** The Constant MAX_BKLT_LEVEL. */
	public static final byte MAX_BKLT_LEVEL = 100;
	
	/** The Constant CMD_PROC_SLEEP_INTERVAL. */
	public static final int CMD_PROC_SLEEP_INTERVAL = 1000;

	/** The Constant DEFAULT_RESP_TIMEOUT. */
	public static final long DEFAULT_RESP_TIMEOUT = 1000;

	/** The Constant STX. */
	public static final byte STX = 0X02;

	/** The Constant ETX. */
	public static final byte ETX = 0X03;
	
	/** The Constant DELI. */
	public static final byte DELI = 0x10;

	/** The Constant STX_ETX_CHECKSUM_LEN. */
	public static final byte STX_ETX_CHECKSUM_LEN = 3;

	/** The Constant ERROR. */
	public static final byte ERROR = -1;

	/* Default setting constant */
	static final String DEFAULT_PIXEL_BRIGHTNESS_VALUE = "50";

	/* Default setting constant */
	static final String DEFAULT_CONTRAST_VALUE = "50";

	/* Default setting constant */
	static final String DEFAULT_BACKLIGHT_BRIGHTNESS_VALUE = "100";

	/* Default setting constant */
	static final String DEFAULT_SATURATION_VALUE = "100";

	/* Default setting constant */
	static final String DEFAULT_HUE_VALUE = "0";

	/* Default setting constant */
	static final String DEFAULT_ASPECT_RATIO_VALUE = "1";

	/* Default setting constant */
	static final String DEFAULT_POWER_STATE_VALUE = "1";

	/**
	 * Instantiates new RGM constants.
	 */
	private ROHANConstants() {

	}
}
