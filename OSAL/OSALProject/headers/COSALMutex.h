/*
 * Mutex.h
 *
 *  Created on: Apr 25, 2016
 *      Author: F60759B
 */
/*
* File name:: Mutex.h
* Purpose ::  for declaring Functions related to Mutex in the Class CMutex

* Classes included ::
* Class Name :
* Purpose :

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   25/APR/2016
*/

#ifndef HEADERS_COSALMUTEX_H_
#define HEADERS_COSALMUTEX_H_

#include<windows.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"

// Mutex Return type abstracted for unix and win types

#ifdef _WIN32
		typedef HANDLE COSALMUTEX;
#elif __unix__
		typedef pthread_mutex_t COSALMUTEX;
#endif

#ifdef _WIN32
		//typedef HANDLE COSALMUTEX;
#elif __unix__
		//typedef pthread_mutex_t  COSALMUTEX;
#endif

//######################################################################  Class CMutex  #######################################################

// Class Name	:: CMutex
//
// Purpose ::  to have the Mutex related APIs segregated here.
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

class CMutex
{
public:
	eReturnType Pthread_mutex_init(COSALMUTEX*);
	eReturnType Pthread_mutex_destroy(COSALMUTEX*);
	eReturnType Pthread_mutex_lock(COSALMUTEX*);
	eReturnType Pthread_mutex_unlock(COSALMUTEX*);
};

#endif /* HEADERS_COSALMUTEX_H_ */
