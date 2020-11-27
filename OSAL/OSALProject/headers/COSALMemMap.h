/*
 * COSALMemMap.h
 *
 *  Created on: Apr 28, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALMemMap.h
* Purpose ::  for declaring all the functions to be included in the Class CMemMap

* Classes included ::
* Class Name :
* Purpose :

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   28/APR/2016
*/
#ifndef COSALMEMMAP_H_
#define COSALMEMMAP_H_

#include<windows.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"
#include<stdio.h>


#ifdef _WIN32
//		typedef HANDLE COSALMUTEX;
#elif __unix__
//		typedef pthread_mutex_t COSALMUTEX;
#endif

#ifdef _WIN32
//		typedef void* COSALVOIDPTR;
#elif __unix__
//		typedef void*  COSALVOIDPTR;
#endif

#ifdef _WIN32
//		typedef const void* COSALCVOIDPTR;
#elif __unix__
//		typedef const void*  COSALCVOIDPTR;
#endif


//###################################################################  Class CMemMap  ###############################################################
// Class Name	:: CMemMap
//
// Purpose ::  to have the Shared Memory related APIs segregated in the following class.
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

class CMemMap {
public:

};

#endif /* COSALMEMMAP_H_ */
