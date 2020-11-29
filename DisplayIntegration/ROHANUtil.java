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

import java.util.ArrayList;

import com.xyzcompany.cs.hcms.core.UnitManager;
/**
 * The Class ROHANUtil.
 *
 */
public final class ROHANUtil {
	/** MASK */
	private static final int MASK = 0xFF000000;
	
	/** Constant for inserting in log statements */
	private static final String TAG = "ROHANUtil::";
	
	/** Second MASK */
	private static final int COUNT = 4;
	/** COUNT */
	private static final int COND_2 = 8;
	/** Private Constructor */
	private ROHANUtil() {}
	
	
	/**
	 * Get Byte array is used to get the bytes out of the integer passed. If the
	 * bytes is any one of the special character, it also adds the delimiter
	 * character to it.
	 * 
	 * @param data
	 *            Data
	 * @param source
	 *            Source
	 */
	public static void getByteArray(final ArrayList<Byte> data, final int source) {
		int mask = MASK;

		for (byte count = 0; count < COUNT; count++) {
			byte tempbyte = (byte) (source & mask);
			if (tempbyte != ROHANConstants.STX && tempbyte != ROHANConstants.ETX
					&& tempbyte != ROHANConstants.DELI) {
				data.add(tempbyte);
			} else {
				data.add(tempbyte);
				data.add(ROHANConstants.DELI);
			}
			mask >>= COND_2;
		}
	}
	
	/**
	 * 
	 * Forming set frame packet for Rosen Gulfstream monitor
	 * @param rclCommand Command to be sent
	 * @param value Command parameter
	 * @param mHandler Handler instance
	 * 
	 */
	public static byte[] framePacket(final String rclCommand, final byte value, final ROHANHandler mHandler) {
		int loopCnt,crc;
		ArrayList<Byte> commandData = new ArrayList<Byte>();
		ArrayList<Byte> crcData = new ArrayList<Byte>();
		String fullCommand = new String();
		
		if (rclCommand.contains(ROHANConstants.SET_POWER_STATE_CMD)) {
		     fullCommand =  "{\"rcl\":\"" + rclCommand + " " + mHandler.getPowerStateSettingMap().get(Integer.toString((int) value)) + "\"}";
		} else if (rclCommand.contains(ROHANConstants.SET_ASPECT_CMD)) {
		     fullCommand =  "{\"rcl\":\"" + rclCommand + " " + mHandler.getAspectRatioSettingMap().get(Integer.toString((int) value)) + "\"}";
		} else {
		        fullCommand =  "{\"rcl\":\"" + rclCommand + " " + Integer.toString((int) value) + "\"}";
		}
		
		char[] commandInput = fullCommand.toCharArray();
		for (loopCnt = 0; loopCnt < fullCommand.length(); loopCnt++) {
			commandData.add((byte) commandInput[loopCnt]);
		}
		if (mHandler.isDebug()) {
			mHandler.logDebug(TAG + "framePacket(): Full comamnd string: " + fullCommand);
		}
		
		/* Convert to byte array (This is just the command string) */
		
		byte[] fullCommandFrame = new byte[commandData.size()];
		for (loopCnt = 0; loopCnt < commandData.size(); loopCnt++) {
			fullCommandFrame[loopCnt] = commandData.get(loopCnt);
		}
		
		/* Calculating CRC of the command string into a byte array*/
		
		crc = CRC16K.init();
		crc = CRC16K.update(crc, fullCommandFrame);
		crc = CRC16K.finalize(crc);
		String crcStr = Integer.toHexString(crc);
		String crcStrFull = String.format("%1$" + 4 + "s", crcStr).replace(' ', '0');
		char[] crcInput = crcStrFull.toCharArray();
		for (loopCnt = 0; loopCnt < crcStrFull.length(); loopCnt++) {
			crcData.add((byte) crcInput[loopCnt]);
		}
		
		if (mHandler.isDebug()) {
			mHandler.logDebug(TAG + "framePacket(): " + crcStrFull);
		}
		
		/* Putting the whole message together */
		
		byte[] finalCommandFrame = new byte[fullCommandFrame.length + 6];
		finalCommandFrame[0] = ROHANConstants.STX;
		
		/* Add Rosen Command */
		
		for (loopCnt = 0; loopCnt < fullCommandFrame.length; loopCnt++) {
			finalCommandFrame[loopCnt+1] = fullCommandFrame[loopCnt];
		}
		finalCommandFrame[fullCommandFrame.length+1] = ROHANConstants.ETX;
		
		/* Add CRC */
		
		for (loopCnt = 0; loopCnt < 4; loopCnt++) {
			finalCommandFrame[fullCommandFrame.length + 2 + loopCnt] = crcData.get(loopCnt);
		}
		
		return finalCommandFrame;
	}
	
	/**
	 * 
	 * Forming get and debug command frame packet for Rosen Gulfstream monitor
	 * @param rclCommand Command to be sent
	 * 
	 */
	public static byte[] framePacket(final String rclCommand) {
		int loopCnt,crc;
		ArrayList<Byte> commandData = new ArrayList<Byte>();
		ArrayList<Byte> crcData = new ArrayList<Byte>();
		String fullCommand = new String();
		
        fullCommand =  "{\"rcl\":\"" + rclCommand + "\"}";

		char[] commandInput = fullCommand.toCharArray();
		for (loopCnt = 0; loopCnt < fullCommand.length(); loopCnt++) {
			commandData.add((byte) commandInput[loopCnt]);
		}
		
		if (UnitManager.Logging.isDebug()) {
			UnitManager.Logging.logDebug(TAG + "framePacket: " + fullCommand);
		}
		
		/* Convert to byte array (This is just the command string) */
		
		byte[] fullCommandFrame = new byte[commandData.size()];
		for (loopCnt = 0; loopCnt < commandData.size(); loopCnt++) {
			fullCommandFrame[loopCnt] = commandData.get(loopCnt);
		}
		
		/* Calculating CRC of the command string into a byte array */
		
		crc = CRC16K.init();
		crc = CRC16K.update(crc, fullCommandFrame);
		crc = CRC16K.finalize(crc);
		String crcStr = Integer.toHexString(crc);
		String crcStrFull = String.format("%1$" + 4 + "s", crcStr).replace(' ', '0');
		char[] crcInput = crcStrFull.toCharArray();
		for (loopCnt = 0; loopCnt < crcStrFull.length(); loopCnt++) {
			crcData.add((byte) crcInput[loopCnt]);
		}
		
		
		/* Putting the whole message together */
		
		byte[] finalCommandFrame = new byte[fullCommandFrame.length + 6];
		finalCommandFrame[0] = ROHANConstants.STX;
		
		/* Add Rosen Command */
		
		for (loopCnt = 0; loopCnt < fullCommandFrame.length; loopCnt++) {
			finalCommandFrame[loopCnt+1] = fullCommandFrame[loopCnt];
		}
		finalCommandFrame[fullCommandFrame.length+1] = ROHANConstants.ETX;
		
		/* Add CRC */
		
		for (loopCnt = 0; loopCnt < 4; loopCnt++) {
			finalCommandFrame[fullCommandFrame.length + 2 + loopCnt] = crcData.get(loopCnt);
		}
		
		return finalCommandFrame;
	}
	
	
	/**
	 * Overloaded method to frame command
	 * 
	 * @param command
	 *            Command
	 * @return - framed command byte array
	 */
	
	public static byte[] framePacket(final int command) {
		int loopCnt = 0;
		ArrayList<Byte> tempData = new ArrayList<Byte>();
		getByteArray(tempData, command);
		byte[] tempCommandFrame = new byte[ROHANConstants.STX_ETX_CHECKSUM_LEN
				+ tempData.size()];
		tempCommandFrame[0] = ROHANConstants.STX;
		for (loopCnt = 1; loopCnt <= tempData.size(); loopCnt++) {
			tempCommandFrame[loopCnt] = tempData.get(loopCnt - 1);
		}
		tempCommandFrame[loopCnt] = ROHANConstants.ETX;
		calculateChecksum(tempCommandFrame);
		return tempCommandFrame;
	} 
	
	
	/**
	 * Calculates Checksum for the given data
	 * 
	 * @param data
	 *            Data
	 */
	public static void calculateChecksum(final byte[] data) {
		int sum = 0;

		for (int count = 1; count < data.length - 2; count++) {
			sum += data[count];
		}
		data[data.length - 1] = (byte) sum;
	}
	

	
}
