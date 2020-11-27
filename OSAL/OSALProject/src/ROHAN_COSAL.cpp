/*
 * ROHAN_COSAL.cpp
 *
 *  Created on: Apr 6, 2016
 *      Author: F60759B
 */
/*
* File name:: ROHAN_COSAL.cpp
* Purpose ::  for making COSAL Class the child of all classes

* Classes included :: None
* Class Name : None
* Purpose : None

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   06/APR/2016
*/
#include "ROHAN_COSAL.h"

//##################################################################  OSAL_debug  ###############################################################
// Name :: OSAL_debug
//
// Purpose  :: displaying using 'cout'
//
// Input :: string to be printed and Message type (defined in ROHANEnums.h)
//
// Output :: Printing
//
// Return Value :: void
//
// Exceptions thrown


void OSAL_debug(msgType eType, const char*str)
{
	//return;
	//if(eType == eERROR || eINFORMATION)
//	{
		cout << str <<endl;
		Sleep(2);
//	}

}

/* ########################## Making COSAL Class the child of all classes (e.g. Thread, Mutex, etc.) ########################## */


