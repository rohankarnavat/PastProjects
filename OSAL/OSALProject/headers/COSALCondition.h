/*
 * COSALCondition.h
 *
 *  Created on: Apr 25, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALCondition.h
* Purpose ::  for declaring Functions related to Conditional Variables in the Class CCondition

* Classes included ::
* Class Name : CCondition
* Purpose : for declaring Functions related to Conditional Variables

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   25/APR/2016
*//*

#ifndef CONDITION_H_
#define CONDITION_H_

#include<windows.h>
#include<WinBase.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"
#include"COSALMutex.h"
#include <stdlib.h>
#include <stdio.h>

#ifdef _WIN32
	typedef CONDITION_VARIABLE  COSALCONDTYPE;
#elif __unix__
		typedef pthread_cond_t  COSALCONDTYPE;
#endif


//#################################################################  class CCondition  ########################################################
// Class Name	:: CCondition
//
// Purpose ::  to have the Conditional Variables related APIs segregated here in the following class.
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

class CCondition
{
public:
	void Pthread_cond_init(COSALCONDTYPE*);
	void Pthread_cond_destroy(COSALCONDTYPE*);
	eReturnType Pthread_cond_wait(COSALCONDTYPE*, COSALMUTEX*);
	eReturnType Pthread_cond_signal(COSALCONDTYPE*);
	eReturnType Pthread_cond_broadcast(COSALCONDTYPE*);

};

#endif // CONDITION_H_

*/
