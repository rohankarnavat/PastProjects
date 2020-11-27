/*
 * COSALFile.h
 *
 *  Created on: Apr 27, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALFile.h
* Purpose ::  for declaring Functions related to File in the Class CFile

* Classes included ::
* Class Name : CFile
* Purpose : for declaring Functions related to File

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   27/APR/2016
*/
#ifndef HEADERS_COSALFILE_H_
#define HEADERS_COSALFILE_H_

#include<windows.h>
#include<iostream>
#include <pthread.h>
#include "ROHANEnums.h"
#include<stdio.h>

//Abstracted data types for unix and Win.

#ifdef _WIN32
		typedef void* COSALVOIDPTR;
#elif __unix__
		typedef void*  COSALVOIDPTR;
#endif

#ifdef _WIN32
		typedef const void* COSALCVOIDPTR;
#elif __unix__
		typedef const void*  COSALCVOIDPTR;
#endif

//###################################################################  Class CFile  ###############################################################
// Class Name	:: CFile
//
// Purpose ::  to have the File system related APIs segregated here.
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

class CFile {
public:
	FILE *Fopen(const char*, const char*);
	eReturnType Fclose(FILE*);
	eReturnType Fread(COSALVOIDPTR, size_t, size_t, FILE*);
	eReturnType Fwrite(COSALCVOIDPTR, size_t, size_t, FILE*);


};

#endif /* HEADERS_COSALFILE_H_ */
