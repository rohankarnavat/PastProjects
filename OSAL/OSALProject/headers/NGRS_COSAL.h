/*
 * ROHAN_COSAL.h
 *
 *  Created on: Apr 6, 2016
 *      Author: F60759B
 */
/*
* File name:: ROHAN_COSAL.h
* Purpose ::  for declaring all the headers used across the project and the Class header files

* Classes included :: None
* Class Name : None
* Purpose : None

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   06/APR/2016
*/
#ifndef HEADERS_ROHAN_COSAL_H_
#define HEADERS_ROHAN_COSAL_H_

//##################### Here are all the .h files for platform, standards, compilation, etc.  are included (e.g. windows.h, iostream.h, etc.) ####
#ifdef _WIN32
#include<windows.h>
#include<iostream>
#include<stdio.h>
#elif __unix
#include <pthread.h>
#include <time.h>
#include <signal.h>
#endif


//##################### Here are all the .h files of the different classes are included (e.g. Thread, Mutex, etc.) #######################

#include "COSALCondition.h"
#include "COSALMutex.h"
#include "COSALThread.h"
#include "ROHANEnums.h"
#include "COSALFile.h"
#include "COSALMemMap.h"
#include "COSALMutex.h"
#include "COSALTimer.h"
#include "COSALMsgQ.h"


void OSAL_debug(msgType, const char*);


//###################################################################  Class COSAL  ###############################################################
// Class Name	:: COSAL
//
// Purpose ::  This Class will be a child to all the classes that will define all the APIs that are abstracted and used across the entire project.
//
// Base Class ::
//
// Operators Overloaded ::    None
//
//  friend classes ::    None
//
// friend functions ::    None
//
// Exceptions thrown  ::

class COSAL {
};

#endif /* HEADERS_ROHAN_COSAL_H_ */
