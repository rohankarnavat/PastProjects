/*
* ROHANEnums.h
*
*  Created on: Apr 18, 2016
*      Author: F60759B
*/
/*
* File name:: ROHANEnums.h
* Purpose ::  for defining enums used across the project

* Classes included :: None
* Class Name : None
* Purpose : None

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   18/APR/2016
*/


#ifndef HEADERS_ROHANENUMS_H_
#define HEADERS_ROHANENUMS_H_

#include<windows.h>
#include<iostream>
#include <pthread.h>

using namespace std;

// For indicating type of message being displayed
enum msgType
{
	eINFORMATION,
	eCHECK,
	eWARNING,
	eERROR
};


//Return Type for Member Functions
enum eReturnType
{
	eSUCCESS,
	eFAILURE
};


#endif /* HEADERS_ROHANENUMS_H_ */
