/*
 * CThread.h
 *
 *  Created on: Apr 7, 2016
 *      Author: F60759B
 */
/*
* File name:: CThread.h
* Purpose ::  for declaring Functions related to Thread in the Class CThread

* Classes included ::
* Class Name : CThread
* Purpose : for declaring Functions related to Thread

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   7/APR/2016
*/
#ifndef SRC_CTHREAD_H_
#define SRC_CTHREAD_H_

// to make the sleep in both OSs similar
#if __unix__
	#include <unistd.h>
#endif

#include<windows.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"

//Abstracted data types for unix and Win.

#ifdef _WIN32
		typedef HANDLE COSALTHREADID;
#elif __unix__
		typedef pthread_t COSALTHREADID;
#endif

#ifdef _WIN32
		typedef void (*COSALFUNCPTR)(LPVOID *);
#else
		typedef void *(*COSALFUNCPTR) (void *);
#endif


#ifdef _WIN32
		typedef SIZE_T COSALSTACKSIZE;
#else
		typedef size_t COSALSTACKSIZE;
#endif

#ifdef _WIN32
		typedef DWORD COSALSECONDS;
#else
		typedef  unsigned COSALSECONDS;
#endif

typedef bool COSALDETACHFLAG;


//#################################################################  class CThread  ########################################################
// Class Name	:: CThread
//
// Purpose ::  to have the Thread related APIs segregated here.
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

class CThread {
public:
	eReturnType Pthread_create(COSALFUNCPTR, COSALTHREADID*);
	eReturnType Pthread_create(COSALSTACKSIZE, COSALFUNCPTR, COSALTHREADID*);
	eReturnType Pthread_create(COSALSTACKSIZE, COSALFUNCPTR, COSALTHREADID*, COSALDETACHFLAG);
	eReturnType Pthread_cancel(COSALTHREADID);
	eReturnType Pthread_detach(COSALTHREADID);
	void COSALSleep(COSALSECONDS);
};

#endif /* SRC_CTHREAD_H_ */

