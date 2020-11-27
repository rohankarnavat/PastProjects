/*
 * Mutex.cpp
 *
 *  Created on: Apr 25, 2016
 *      Author: F60759B
 */

#include <ROHAN_COSAL.h>
#include "COSALMutex.h"
#include "COSALThread.h"
#include<iostream>


using namespace std;


//###########################################################  Pthread_mutex_init  #############################################################
// Name :: Pthread_mutex_init
//
// Purpose  :: Initializing/Creating a Mutex
//
// Input :: Pointer to Mutex type/ void pointer
//
// Output :: Mutex Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMutex::Pthread_mutex_init(COSALMUTEX *mutexCreate)
{

#ifdef _WIN32

	OSAL_debug(eINFORMATION, "Entered in Pthread_mutex_init for Windows");
	DWORD mutexCreateErr = 0;

//	Calling Win API to Create Mutex
	*mutexCreate = CreateMutex(NULL, TRUE, NULL);

//	Checking for Success or Failure
	if(mutexCreate == NULL)
	{
		OSAL_debug(eERROR, "Mutex Creation failed!");
		mutexCreateErr = GetLastError();
		return ((eReturnType)mutexCreateErr);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Mutex Creation Successful!");
		return eSUCCESS;
	}




#elif __unix__

	OSAL_debug(eINFORMATION, "Entered in Pthread_mutex_init for Posix");

	//	Calling Posix API to Create Mutex
		int mutexCreateRet = pthread_mutex_init(mutexCreate, NULL);

		//	Checking for Success or Failure
		if(mutexCreateRet != 0)
		{
			OSAL_debug(eERROR, "Mutex Creation failed!");
			return ((eReturnType)mutexCreateRet);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Creation Successful!");
			return eSUCCESS;
		}

#endif


}



//###########################################################  Pthread_mutex_destroy  #############################################################
// Name :: Pthread_mutex_destroy
//
// Purpose  :: Destroy a Mutex
//
// Input :: Pointer to Mutex type/ void pointer
//
// Output :: Mutex Destroyed
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMutex::Pthread_mutex_destroy(COSALMUTEX *mutexDestroy)
{

#ifdef _WIN32
	DWORD mutexDestroyErr = 0;

//		Calling Win API to Destroy Mutex and check for success or Failure
		if(CloseHandle(*mutexDestroy) == 0)
		{
			OSAL_debug(eERROR, "Mutex Destruction failed!");
			mutexDestroyErr = GetLastError();
			return ((eReturnType)mutexDestroyErr);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Destroyed Successful!");
			return eSUCCESS;
		}

#elif __unix__

//		Calling Posix API to Destroy Mutex
		int mutexDestroyRet = pthread_mutex_destroy(mutexDestroy);
//		Checking for success or Failure
		if(mutexDestroyRet != 0)
		{
			OSAL_debug(eERROR, "Mutex Destruction failed!");
			return ((eReturnType)mutexDestroyRet);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Destroyed Successfully!");
			return eSUCCESS;
		}
#endif


}


//###########################################################  Pthread_mutex_lock  #############################################################
// Name :: Pthread_mutex_lock
//
// Purpose  :: Lock a Mutex
//
// Input :: Pointer to Mutex type/ void pointer
//
// Output :: Mutex Locked
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMutex::Pthread_mutex_lock(COSALMUTEX *mutexLock)
{

#ifdef _WIN32
	DWORD mutexLockErr = 0;

//		Calling Win API to Lock Mutex and check for success or Failure
		if(WaitForSingleObject(*mutexLock, INFINITE) == WAIT_FAILED)
		{
			OSAL_debug(eERROR, "Mutex Lock failed!");
			mutexLockErr = GetLastError();
			return ((eReturnType)mutexLockErr);
		}
		else if(WaitForSingleObject(*mutexLock, INFINITE) == WAIT_OBJECT_0)
		{
			OSAL_debug(eINFORMATION, "Mutex Locked Successfully!");
			return eSUCCESS;
		}
		return eSUCCESS;



#elif __unix__

//		Calling Posix API to Lock Mutex and check for success or Failure
		int mutexLockRet = pthread_mutex_lock(mutexLock);
		if(mutexLockRet != 0)
		{
			OSAL_debug(eERROR, "Mutex Lock failed!");
			return ((eReturnType)mutexLockRet);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Locked Successfully!");
			return eSUCCESS;
		}
#endif
}



//###########################################################  Pthread_mutex_unlock  #############################################################
// Name :: Pthread_mutex_unlock
//
// Purpose  :: Unlock a Mutex
//
// Input :: Pointer to Mutex type/ void pointer
//
// Output :: Mutex Unlocked
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMutex::Pthread_mutex_unlock(COSALMUTEX *mutexUnlock)
{

#ifdef _WIN32
	DWORD mutexUnlockErr = 0;

//		Calling Win API to Unlock Mutex and check for success or Failure
		if(ReleaseMutex(*mutexUnlock) == 0)
		{
			OSAL_debug(eERROR, "Mutex Lock failed!");
			mutexUnlockErr = GetLastError();
			return ((eReturnType)mutexUnlockErr);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Unlocked Successfully!");
			return eSUCCESS;
		}

#elif __unix__

//		Calling Posix API to Unlock Mutex and check for success or Failure
		int mutexUnlockRet = pthread_mutex_unlock(mutexUnlock);
		if(mutexUnlockRet != 0)
		{
			OSAL_debug(eERROR, "Mutex Lock failed!");
			return ((eReturnType)mutexUnlockRet);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Mutex Unlocked Successfully!");
			return eSUCCESS;
		}
#endif


}
