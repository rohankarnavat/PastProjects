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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import com.xyzcompany.cs.hcms.core.UnitManager;
import com.xyzcompany.cs.hcms.core.components.SerialAdapter;
import com.xyzcompany.cs.hcms.core.services.handlers.HandlerPropertyNotFoundException;
import com.xyzcompany.cs.hcms.core.services.handlers.PropertyMap;
import com.xyzcompany.cs.hcms.core.utils.TriStateConstants;

/**
 * The Class ROHANDataProcessor.
 */
public final class ROHANDataProcessor implements Runnable {

	/** Max Buffer Size */
	public static final int MAX_FRAME_SIZE = 1024;
	
	/** Max Buffer Size */
	public static final int MAX_BUFFER_SIZE = 256;
	
	/** Read Interval */
	public static final int READ_INTERVAL = 50;
	
	/** Constant for inserting in log statements */
	private static final String TAG = "ROHANDataProcessor::";
	
	/** Print format */
	public static final String PRINT_DATA_FORMAT = "%02x ";
	
	/** Flag to not to send the commands that are not implemented in ACS Monitors */
	public static final boolean CMD_SUPPORTED = false;
	
	/** ROHANHandler Reference */
	private ROHANHandler mHandler;
	
	/** Serial Adapter Reference */
	private SerialAdapter mSerialAdapter;
	
	/** ROHAN Command Processor Reference */
	private ROHANCommandProcessor mCmdProcessor;
	
	/** Boolean which intrudes the current thread */
	private boolean stopThread = false;
	
	/** Data Queue */
	private ArrayList<byte[]> commandsFromDeviceQ;
	
	/**
	 * Constructor
	 * 
	 * @param handler
	 *            RGM Handler
	 * @param serialAdapter
	 *            Serial Adapater
	 * @param cmdProcessor
	 *            Command Processor
	 */
	public ROHANDataProcessor(final ROHANHandler handler,
			final SerialAdapter serialAdapter,
			final ROHANCommandProcessor cmdProcessor) {
		mHandler = handler;
		mSerialAdapter = serialAdapter;
		mCmdProcessor = cmdProcessor;
		stopThread = false;
		commandsFromDeviceQ = new ArrayList<byte[]>();
	}

	/**
	 * Marks to stop the thread
	 */
	public void stopThread() {
		stopThread = true;
	}

	/** {@inheritDoc} */
	public void run() {
		byte[] buffer = new byte[MAX_BUFFER_SIZE];

		while (!stopThread) {

			int actualBytesRead = 0;
			actualBytesRead = mSerialAdapter.read(buffer);
			
			if (actualBytesRead > 0) {
				if(mHandler.isInfo()) {
					mHandler.logInfo(TAG + "run:" + Arrays.toString(buffer));
				}
			}
			if (actualBytesRead != ROHANConstants.ERROR) {
				boolean retVal = frameResponse(buffer, actualBytesRead);
				if (retVal) {
					if (mHandler.isInfo()) {
						mHandler.logInfo(TAG + "run: Response framed.."
								+ "Data Queue Size : "
								+ commandsFromDeviceQ.size());
					}
					processDeviceInput();
				} 
			}

			try {
				Thread.sleep(READ_INTERVAL);
			} catch (InterruptedException e) {
				UnitManager.Logging
						.logSevere(TAG + "run: Exception in the RGM Reader thread. Error Description : "
								+ e.getMessage());
			}
		}
	}

	/**
	 * Frames the bytes coming from the device into appropriate
	 * commands/responses so that the RGM handler or the RGM software downloader
	 * can process
	 * 
	 * @param buffer
	 *            Total Buffer
	 * @param actualBytesRead
	 *            Number of Actual Bytes Read
	 * @return boolean
	 */
	private boolean frameResponse(final byte[] buffer, final int actualBytesRead) {
		boolean isCommandFramed = false;
		boolean startOfFrameFound = false;
		boolean endOfFrameFound = false;
		byte[] dataFromDevice = new byte[MAX_FRAME_SIZE];
		int dataIndex = 0;
		int dataCount = 0;
		if (mHandler.isInfo()) {
			mHandler.logInfo(TAG + "frameResponse: "
					+ "Actual bytes Read -> " + actualBytesRead);
		}

		if (mHandler.isDebug()) {
			printBuffer(actualBytesRead, buffer);
		}

		try {
			
			for (int loopCnt = 0; loopCnt <= actualBytesRead; loopCnt++) {	
				if (buffer[loopCnt] == ROHANConstants.DELI) {
					if (buffer[loopCnt + 1] == ROHANConstants.STX || buffer[loopCnt + 1] == ROHANConstants.ETX
							|| buffer[loopCnt + 1] == ROHANConstants.DELI) {
						loopCnt = loopCnt + 1;
					} else {
						break;
					}
				} else {
					if (buffer[loopCnt] == ROHANConstants.STX) {
						startOfFrameFound = true;
					} else if (buffer[loopCnt] == ROHANConstants.ETX) {
						endOfFrameFound = true;
					}
				}
				dataFromDevice[dataIndex] = buffer[loopCnt];
				dataIndex++;
				if (startOfFrameFound && endOfFrameFound) {
					loopCnt = loopCnt + 1;
					dataFromDevice[dataIndex] = buffer[loopCnt]; // Checksum
					dataCount = dataIndex + 1;
					if (mHandler.isDebug()) {
						printBuffer(dataCount, dataFromDevice);
					}
					updateDataQueue(dataCount, dataFromDevice);
					dataIndex = 0;
					dataCount = 0;
					/*clear the dataFromDevice in case we receive multiple response in a single read*/
					startOfFrameFound = false;
					endOfFrameFound = false;
					isCommandFramed = true;
				}
				//dataLength++;
			} // end for loop
		} catch (Exception e) {
			UnitManager.Logging.logSevere(TAG + "frameResponse: Maximum data size reached..." + e.getMessage());
			
		}

		return isCommandFramed;
	}

	/**
	 * Prints the given buffer
	 * 
	 * @param actualBytesRead
	 *            Number of bytes read
	 * @param buffer
	 *            Buffer
	 */
	private void printBuffer(final int actualBytesRead, final byte[] buffer) {
		final StringBuilder printBuffer = new StringBuilder();
		final Formatter formatter = new Formatter(printBuffer);

		for (int byteIdx = 0; byteIdx < actualBytesRead; byteIdx++) {
			formatter.format(PRINT_DATA_FORMAT, buffer[byteIdx]);
		}
	

		if (mHandler.isInfo()) {
			mHandler.logInfo(TAG + "printBuffer: Data from Port: " + printBuffer.toString());
		}

		printBuffer.setLength(0);
		
		formatter.close();
	}

	/**
	 * The commands that are framed are placed in a Q by the following method.
	 * This method is used by the frameResponse method.
	 * 
	 * @param dataCount
	 *            Data Count
	 * @param dataRead
	 *            Buffer
	 */
	private void updateDataQueue(final int dataCount, final byte[] dataRead) {
		byte[] temp = new byte[dataCount];
		int loopCnt = 0;

		for (; loopCnt < dataCount; loopCnt++) {
			temp[loopCnt] = dataRead[loopCnt];
		}
		if (loopCnt >= 0) {
			commandsFromDeviceQ.add(temp);
		}
		final StringBuilder printBuffer = new StringBuilder();
		final Formatter formatter = new Formatter(printBuffer);

		for (int byteIdx = 0; byteIdx < dataCount; byteIdx++) {
			formatter.format(PRINT_DATA_FORMAT, dataRead[byteIdx]);
		}

		if (mHandler.isInfo()) {
			mHandler.logInfo(TAG + "updateDataQueue: Data from Port: " + printBuffer.toString());
		}

		printBuffer.setLength(0);
		
		formatter.close();
	}

	/**
	 * This method handles all the input form the device.
	 */
	@SuppressWarnings("deprecation")
    private void processDeviceInput() {
		int electronicNampePlateCmdOffsetIndex = 32;
		int electronicNampePlateCmdDelimitIndex = 38;

		PropertyMap statesToLSM = new PropertyMap();
		for (int commandCount = 0; commandCount < commandsFromDeviceQ.size(); commandCount++) {

			byte[] commandToProcess = commandsFromDeviceQ.get(commandCount);
			DataInputStream dataStream = new DataInputStream(
					new ByteArrayInputStream(commandToProcess));
			try {

				byte tempData = dataStream.readByte();  // skip header
                String respReceived = dataStream.readLine();
				if (tempData == ROHANConstants.STX) { // Start processing a data
					if(mHandler.isInfo()) {
						mHandler.logInfo(TAG + "processDeviceInput(): Response Received: " + respReceived);
					}
					if (respReceived.contains("OKAY")) {
						handleACKResponse();
					} else if (respReceived.contains("ERROR")) {
						UnitManager.Logging.logWarning(TAG + "processDeviceInput(): ERROR Received!" + respReceived);
						if (mCmdProcessor.isWaitingForResponse()) {
							mCmdProcessor.notifyNAKReceived();
						}
					}
				}		
				

				/** Read Electronic Number Plate information */
                if (respReceived.contains(ROHANConstants.GET_HW_PART_NUMBER_RESPONSE)) {
                    if (mHandler.getProperty(ROHANProperties.HW_PART_NUMBER.getPropertyName()).equals(TriStateConstants.UNKNOWN.getValue())) {
                        String hwPartNumber = respReceived.substring(electronicNampePlateCmdOffsetIndex, 
                        		(electronicNampePlateCmdDelimitIndex+4));
                        if (mHandler.isDebug()) {
        					mHandler.logDebug(TAG + "processDeviceInput(): setting hwPartNumber as: " + hwPartNumber);
                        }
                        statesToLSM.put(ROHANProperties.HW_PART_NUMBER.getPropertyName(), hwPartNumber);
                    }
                } else if (respReceived.contains(ROHANConstants.GET_FW_PART_NUMBER_RESPONSE)) {
                    if (mHandler.getProperty(ROHANProperties.FW_PART_NUMBER.getPropertyName()).equals(TriStateConstants.UNKNOWN.getValue())) {
                        String fwPartNumber = respReceived.substring(electronicNampePlateCmdOffsetIndex, 
                        		electronicNampePlateCmdDelimitIndex);
                        if (mHandler.isDebug()) {
        					mHandler.logDebug(TAG + "processDeviceInput(): setting fwPartNumber as: " + fwPartNumber);
                        }
                        statesToLSM.put(ROHANProperties.FW_PART_NUMBER.getPropertyName(), fwPartNumber);
                    }
				} else if (respReceived.contains(ROHANConstants.GET_SERIAL_NUMBER_RESPONSE)) {
				    if (mHandler.getProperty(ROHANProperties.SERIAL_NUMBER.getPropertyName()).equals(TriStateConstants.UNKNOWN.getValue())) {
	                    String serialNumber = respReceived.substring((electronicNampePlateCmdOffsetIndex-1), 
	                    		(electronicNampePlateCmdDelimitIndex+2));
	                    if (mHandler.isDebug()) {
        					mHandler.logDebug(TAG + "processDeviceInput(): setting serialNumber as: " + serialNumber);
	                    }
	                    statesToLSM.put(ROHANProperties.SERIAL_NUMBER.getPropertyName(), serialNumber);
				    }
				}else if (respReceived.contains(ROHANConstants.SET_PIX_BRIGHTNESS_RESPONSE) ||
                        respReceived.contains(ROHANConstants.SET_BACKLIGHT_RESPONSE) || 
                        respReceived.contains(ROHANConstants.SET_CONTRAST_RESPONSE) || 
                        respReceived.contains(ROHANConstants.SET_HUE_RESPONSE) ||
                        respReceived.contains(ROHANConstants.SET_SATURATION_RESPONSE) ||
                        respReceived.contains(ROHANConstants.SET_ASPECT_RESPONSE) ||
                        respReceived.contains(ROHANConstants.SET_POWER_STATE_RESPONSE)
                        ) {
                    handleCommandResponse(respReceived, statesToLSM);
                } 

			} catch (Exception e) {
				UnitManager.Logging.logSevere(TAG + "processDeviceInput Exception in parsing the data from RGM" + e.getMessage());
			}
		}
			
		if (!statesToLSM.isEmpty()) {
			mHandler.setProperty(statesToLSM);
		}

		commandsFromDeviceQ.clear();
	}

	/**
	 * Handles ACK Response
	 * 
	 * @param dataStream
	 *            Data Stream to read
	 */
    private void handleACKResponse() {
    	
    	if (mHandler.isInfo()) {
			mHandler.logInfo(TAG + "handleACKResponse() is called");
    	}

			/** If command processor is waiting for OKAY, then only notify it */
			if (mCmdProcessor.isWaitingForResponse()) {
				
				if (mHandler.isInfo()) {
					mHandler
							.logInfo(TAG + "handleACKResponse() CmdProc is waiting for Response, since notifying!");
				}
				mCmdProcessor.notifyResponseReceived();
			}
			
			if (!(mHandler.getrgmStatus().equalsIgnoreCase("true"))) {
			    mHandler.setRGMStatus(true);
			}
	}



	
    /**
     * Handles the get parameters response
     * 
     * @param respReceived
     *            Data Stream to read
     * @param statesToLSM
     *            Property Map to update
     */
    private void handleCommandResponse(String respReceived, final PropertyMap statesToLSM) {
    	
    	try {
    		
    		/* First read the values of the Req and Act states. 
    		 * Compare them, and if they don't match only then update the values in the Act states  */
    		
    
    		byte rgmPixelBrightness = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_PIXEL_BRIGHTNESS.getPropertyName()));
	        byte rgmBacklightBrightness = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_BACKLIGHT_BRIGHTNESS.getPropertyName()));
	        byte rgmContrast = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_CONTRAST.getPropertyName()));
	        byte rgmHue = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_HUE.getPropertyName()));
	        byte rgmSaturation = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_SATURATION.getPropertyName()));
	        byte rgmAspectRatio = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_ASPECT_RATIO.getPropertyName()));
	        byte rgmPowerState = Byte.parseByte(mHandler.getProperty(ROHANProperties.RGM_POWER_STATE.getPropertyName()));
	        
	        
	        byte pixelBrightness = Byte.parseByte(mHandler.getProperty(ROHANProperties.PIXEL_BRIGHTNESS.getPropertyName()));
	        byte backlightBrightness = Byte.parseByte(mHandler.getProperty(ROHANProperties.BACKLIGHT_BRIGHTNESS.getPropertyName()));
	        byte contrast = Byte.parseByte(mHandler.getProperty(ROHANProperties.CONTRAST.getPropertyName()));
	        byte hue = Byte.parseByte(mHandler.getProperty(ROHANProperties.HUE.getPropertyName()));
	        byte saturation = Byte.parseByte(mHandler.getProperty(ROHANProperties.SATURATION.getPropertyName()));
	        byte aspectRatio = Byte.parseByte(mHandler.getProperty(ROHANProperties.ASPECT_RATIO.getPropertyName()));
	        byte powerState = Byte.parseByte(mHandler.getProperty(ROHANProperties.POWER_STATE.getPropertyName()));
    		 
    		
    	if (respReceived.contains(ROHANConstants.SET_ASPECT_RESPONSE)) { 
    		if(rgmAspectRatio != aspectRatio)
    		statesToLSM.put(ROHANProperties.ASPECT_RATIO.getPropertyName(),Byte.toString(rgmAspectRatio));
        	
        } else if (respReceived.contains(ROHANConstants.SET_POWER_STATE_RESPONSE)) {
        	
        	if(rgmPowerState != powerState)
        		statesToLSM.put(ROHANProperties.POWER_STATE.getPropertyName(),Byte.toString(rgmPowerState));
        	
        }else if (respReceived.contains(ROHANConstants.SET_HUE_RESPONSE)) {
        	if(rgmHue != hue)
        		statesToLSM.put(ROHANProperties.HUE.getPropertyName(),Byte.toString(rgmHue));
            
        } else if (respReceived.contains(ROHANConstants.SET_SATURATION_RESPONSE)) {
        	if(rgmSaturation != saturation)
        		statesToLSM.put(ROHANProperties.SATURATION.getPropertyName(),Byte.toString(rgmSaturation));
            
        } else if (respReceived.contains(ROHANConstants.SET_BACKLIGHT_RESPONSE)) {
        	if(rgmBacklightBrightness != backlightBrightness)
        		statesToLSM.put(ROHANProperties.BACKLIGHT_BRIGHTNESS.getPropertyName(),Byte.toString(rgmBacklightBrightness));
                   
        } else if (respReceived.contains(ROHANConstants.SET_PIX_BRIGHTNESS_RESPONSE)) {
        	if(rgmPixelBrightness != pixelBrightness)
                statesToLSM.put(ROHANProperties.PIXEL_BRIGHTNESS.getPropertyName(),Byte.toString(rgmPixelBrightness));
        	
        } else if (respReceived.contains(ROHANConstants.SET_CONTRAST_RESPONSE)) {
        	if(rgmContrast != contrast)
        		statesToLSM.put(ROHANProperties.CONTRAST.getPropertyName(),Byte.toString(rgmContrast));
        	     
        }
   
    	} catch (HandlerPropertyNotFoundException hpne) {
        UnitManager.Logging.logSevere(TAG +
                	"handleCommandResponse(): Property was not found in the database -> " + hpne.toString());
    		}
    }
}