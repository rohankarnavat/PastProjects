/*
 * COSALCondition.cpp
 *
 *  Created on: Apr 25, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALCondition.cpp
* Purpose ::  for defining all the functions declared in the Class CCondition

* Classes included :: None
* Class Name : None
* Purpose : None

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   25/APR/2016
*//*

#include "COSALCondition.h"
#include "ROHAN_COSAL.h"
#include "COSALThread.h"
#include "COSALCondition.h"
#include <iostream>
#include <windows.h>
#include <pthread.h>


using namespace std;


//###########################################################  Pthread_cond_init  #############################################################
// Name :: Pthread_cond_init
//
// Purpose  :: Initializing Conditional variable
//
// Input :: Condition Variable type
//
// Output :: Condition variable initialized
//
// Return Value :: void
//
// Exceptions thrown

void CCondition::Pthread_cond_init(COSALCONDTYPE *condVariable)
{

#ifdef _WIN32




#elif __unix__

	pthread_cond_init(condVariable, NULL);


#endif

}


//###########################################################  Pthread_cond_destroy  #############################################################
// Name :: Pthread_cond_destroy
//
// Purpose  :: Deleting Conditional variable
//
// Input :: Condition Variable type
//
// Output :: Condition variable Deleted
//
// Return Value :: void
//
// Exceptions thrown

void CCondition::Pthread_cond_destroy(COSALCONDTYPE *condVariableDestroy)
{

#ifdef _WIN32




#elif __unix__

	pthread_cond_init(condVariableDestroy, NULL);


#endif
}



//###########################################################  Pthread_cond_wait  #############################################################
// Name :: Pthread_cond_wait
//
// Purpose  :: Waiting on a Condition
//
// Input :: Condition Variable type, Mutex handle/type pointer
//
// Output :: Condition variable waiting
//
// Return Value :: eReturnType
//
// Exceptions thrown
eReturnType CCondition::Pthread_cond_wait(COSALCONDTYPE *condVariableWait, COSALMUTEX *mutexPointer)
{

#ifdef _WIN32
	return eSUCCESS;



#elif __unix__

//	Calling the Posix API to wait on condition
	int condWaitRet = pthread_cond_wait(condVariableWait, mutexPointer);

//	Checking for return value and returning error if API fails or eSUCCESS if API is successful.

	if(condWaitRet != 0)
	{
		OSAL_debug(eERROR, "Conditional wait faliled...");
		return ((eReturnType)condWaitRet);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Conditional wait Successful!");
		return eSUCCESS;
	}


#endif


}



//###########################################################  Pthread_cond_signal  #############################################################
// Name :: Pthread_cond_signal
//
// Purpose  :: This function is used to unblock thread blocked on a condition variable.

//
// Input :: Condition Variable type.
//
// Output :: unblocked thread
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CCondition::Pthread_cond_signal(COSALCONDTYPE *condVariableSignal)
{

#ifdef _WIN32


	return eSUCCESS;

#elif __unix__

//	Calling the Posix API to Signal for condition

	int condSignalRet = pthread_cond_signal(condVariableSignal);

	//	Checking for return value and returning error if API fails or eSUCCESS if API is successful.
	if(condSignalRet != 0)
	{
		OSAL_debug(eERROR, "Conditional Signal faliled...");
		return ((eReturnType)condSignalRet);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Conditional Signal Successful!");
		return eSUCCESS;
	}


#endif


}



//###########################################################  Pthread_cond_broadcast  #############################################################
// Name :: Pthread_cond_broadcast
//
// Purpose  :: This function is used to unblock all threads blocked on a condition variable.

//
// Input :: Condition Variable type.
//
// Output :: unblocked threads
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CCondition::Pthread_cond_broadcast(COSALCONDTYPE *condVariableBroadcast)
{

#ifdef _WIN32


	return eSUCCESS;

#elif __unix__

	//	Calling the Posix API to Broadcast Signal for condition
	int condBroadcastRet = Pthread_cond_broadcast(condVariableBroadcast);

	//	Checking for return value and returning error if API fails or eSUCCESS if API is successful.
	if(condBroadcastRet != 0)
	{
		OSAL_debug(eERROR, "Conditional Broadcast faliled...");
		return ((eReturnType)condBroadcastRet);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Conditional Broadcast Successful!");
		return eSUCCESS;
	}


#endif


}

*/
