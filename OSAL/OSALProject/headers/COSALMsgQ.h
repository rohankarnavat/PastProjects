/*
 * COSALMsgQ.h
 *
 *  Created on: Apr 28, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALMsgQ.h
* Purpose ::  for defining all the functions declared in the Class CMemMap

* Classes included ::
* Class Name :
* Purpose :

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   28/APR/2016
*/

#ifndef HEADERS_COSALMSGQ_H_
#define HEADERS_COSALMSGQ_H_

#include "ROHAN_COSAL.h"
#include<windows.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"
#include<stdio.h>

#ifdef __unix__
#include<mqueue.h>
#endif

// Abstracting the Return Type difference between win and unix platform

#ifdef _WIN32
		typedef HRESULT COSALMQRETURN;
#elif __unix__
		typedef mqd_t COSALMQRETURN;
#endif




//###################################################################  Class CMsgQ  ###############################################################
// Class Name	:: CMsgQ
//
// Purpose ::  class CMsgQ contains all the Message Queue related APIs
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

class CMsgQ {
public:
	eReturnType Mq_open(const char*, COSALMQRETURN);
	eReturnType Mq_unlink(const char*);
	eReturnType Mq_send();
	eReturnType Mq_receive();

};

#endif /* HEADERS_COSALMSGQ_H_ */
