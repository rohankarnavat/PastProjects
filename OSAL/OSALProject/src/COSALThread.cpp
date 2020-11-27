/*
 * CThread.cpp
 *
 *  Created on: Apr 7, 2016
 *      Author: F60759B
 */
/*
* File name:: CThread.cpp
* Purpose ::  for defining all the functions declared in the Class CThread

* Classes included ::
* Class Name : CFile.
* Purpose : for defining all the functions (APIs) declared
*
*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   7/APR/2016
*/

#include "ROHAN_COSAL.h"
#include "COSALThread.h"
#include <iostream>


using namespace std;

//########################################################  Pthread_create with Function call and Thread ID  #####################################
// Name :: Pthread_create
//
// Purpose  :: Creating a thread with default values
//
// Input :: Function call and Thread ID
//
// Output :: Thread Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CThread::Pthread_create(COSALFUNCPTR funcPtr, COSALTHREADID *threadID)
{

#ifdef _WIN32

	DWORD ErrorCode = eSUCCESS, threadID1;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Windows");

//		Calling Win API to Creating Thread and check for success or Failure
		*threadID = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)funcPtr, NULL, 0, &threadID1);

		if(threadID == NULL)
		{
			ErrorCode = GetLastError();
			OSAL_debug(eERROR, "Thread Creation failed! Error Code : ");
			return ((eReturnType)ErrorCode);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Thread Creation Successful!");
			return eSUCCESS;
		}


#elif __unix__

	pthread_attr_t attr;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Posix");

//		Calling Posix API to Creating Thread and check for success or Failure
	int createRet = pthread_create(threadID, NULL, FuncPtr, NULL);

		if(createRet != 0)
		{
			OSAL_debug(eERROR, "Thread Creation failed!");
			return((eReturnType)createRet);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Thread Creation Successful! ");
			return eSUCCESS;
		}
#endif
}



//#############################################  Pthread_create with Stack Size, Function call and Thread ID  ################################
// Name :: Pthread_create
//
// Purpose  :: Creating a thread with Custom Stack Size
//
// Input :: Function call, Stack Size and Thread ID
//
// Output :: Thread Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CThread::Pthread_create(COSALSTACKSIZE sizeOfStack, COSALFUNCPTR FuncPtr, COSALTHREADID *threadID)
{

#ifdef _WIN32

	DWORD ErrorCode = eSUCCESS, threadID1;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Windows");

//		Calling Win API to Creating Thread and check for success or Failure
	*threadID = CreateThread(NULL, sizeOfStack, (LPTHREAD_START_ROUTINE)FuncPtr, NULL, 0, &threadID1);

		if(threadID == NULL)
		{
			ErrorCode = GetLastError();
			OSAL_debug(eERROR, "Thread Creation failed! Error Code : ");
			return ((eReturnType)ErrorCode);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Thread Creation Successful!");
			return eSUCCESS;
		}


#elif __unix__

	pthread_attr_t attr;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Posix");

//		Calling Posix API to Creating Thread and check for success or Failure
	int createRet = pthread_create(threadID, NULL, FuncPtr, NULL);

		if(createRet != 0)
		{
			OSAL_debug(eERROR, "Thread Creation failed!");
			return((eReturnType)createRet);
		}
		else
		{
//			Calling API to Set Stack Size
			int stackSizeReturn = pthread_attr_setstacksize(&attr, sizeOfStack);

				if(stackSizeReturn != 0)
				{
					OSAL_debug(eINFORMATION, "Thread Creation Successful! ");
					return eSUCCESS;
				}
				else
				{
					OSAL_debug(eERROR, "Error in setting stack size. Terminating Thread...");

//					If Setting Stack size fails, cancel thread and return appropriate error
					int stackRet = pthread_cancel(*threadID);

						if(stackRet != 0)
							OSAL_debug(eERROR, "Error in setting stack size. Thread Terminated!!");
						else
						{
							OSAL_debug(eERROR, "Error in setting stack size. Thread could not be terminate...");
							return((eReturnType)stackRet);
						}
				}

			}
#endif
}



//#############################################  Pthread_create with Stack Size, Function call, Thread ID and Set to detached state #########
// Name :: Pthread_create
//
// Purpose  :: Creating a thread with Custom Stack Size and detached state
//
// Input :: Function call, Stack Size, detached state(bool) and Thread ID
//
// Output :: Thread Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CThread::Pthread_create(COSALSTACKSIZE sizeOfStack, COSALFUNCPTR FuncPtr, COSALTHREADID *threadID, COSALDETACHFLAG detachState)
{

#ifdef _WIN32

	DWORD ErrorCode = eSUCCESS, threadid1;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Windows");

//		Calling Win API to Creating Thread and check for success or Failure
	*threadID = CreateThread(NULL, sizeOfStack, (LPTHREAD_START_ROUTINE)FuncPtr, NULL, 0, &threadid1);

		if(threadID == NULL)
		{
			ErrorCode = GetLastError();
			OSAL_debug(eERROR, "Thread Creation failed! Error Code : ");
			return ((eReturnType)ErrorCode);
		}
		else
		{
			OSAL_debug(eINFORMATION, "Thread Creation Successful!");
				if(detachState)
					CloseHandle(*threadID);
			return eSUCCESS;
		}


#elif __unix__

	pthread_attr_t attr;

	OSAL_debug(eCHECK, "Entered in Pthread_create for Posix");

	//			Calling API to Init Attribute
	int attrInitStatus = pthread_attr_init(&attr);

	//			Calling API to Set Stack Size
	int stackSizeStatus = pthread_attr_setstacksize(&attr, sizeOfStack);

//		Calling Posix API to Creating Thread and check for success or Failure
	int createStatus = pthread_create(threadID, &attr, FuncPtr, NULL);

		if(createStatus != 0)
		{
			OSAL_debug(eERROR, "Thread Creation failed!");
			return((eReturnType)createStatus);
		}
		else
		{
				if(stackSizeStatus != 0 && attrInitStatus != 0)
				{
//					Creating Thread in detached state
					if(detachState)
						if (pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED))
						{
							OSAL_debug(eINFORMATION, "Thread Creation Successful! ");
							return eSUCCESS;
						}
						else
						{
//							Cancelling Thread if error occurs while creating in detached state
							OSAL_debug(eERROR, "Error in setting detach state. Terminating Thread...");
							int detachErrorCancel = Pthread_cancel(*threadID);
							return ((eReturnType)detachErrorCancel);
						}
				}
				else
				{
//							Cancelling Thread if error occurs while Setting stack size
					int stackErrorCancel = Pthread_cancel(*threadID);
					return ((eReturnType)stackErrorCancel);

				}

		}
#endif
}



//#####################################################################  Pthread_cancel  #################################################
// Name :: Pthread_cancel
//
// Purpose  :: Canceling a thread
//
// Input :: Thread ID
//
// Output :: Thread Cancelled
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CThread::Pthread_cancel(COSALTHREADID threadID)
{
#ifdef _WIN32

	DWORD exitCode, errorCode;

//		Getting Exit Code for cancelling Thread
		if(GetExitCodeThread(threadID, &exitCode)!=0)
		{
			OSAL_debug(eCHECK, "Entered In GetExitCodeThread");

//				Calling Win API to Cancel Thread
				if(TerminateThread(threadID,exitCode)!=0)
				{
					OSAL_debug(eINFORMATION, "Thread Terminated!");
					return eSUCCESS;
				}

				else
				{
					OSAL_debug(eERROR, "Thread Termination failed!");
					errorCode = GetLastError();
					return ((eReturnType)errorCode);
				}

		}
	else
		{
				OSAL_debug(eERROR, "GetExitCodeThread failed!");
				errorCode = GetLastError();
				return ((eReturnType)errorCode);
		}

#elif __unix__

	OSAL_debug(eCHECK, "Entered in Pthread_cancel for Posix");

//	Calling Posix API for cancelling Thread and checking for Success or Failure
	int cancelRet = pthread_cancel(threadID);

		if(cancelRet != 0)
		{
			OSAL_debug(eERROR, "Thread Termination failed!");
			return((eReturnType)cancelRet);
		}
		else
		{

			OSAL_debug(eINFORMATION, "Thread Termination Successful!");
			return eSUCCESS;
		}


#endif

}


//#####################################################################  Pthread_detach  #################################################
// Name :: Pthread_detach
//
// Purpose  :: Detaching a thread
//
// Input :: Thread ID
//
// Output :: Thread Detached
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CThread::Pthread_detach(COSALTHREADID threadID)
{
#ifdef _WIN32

	DWORD ErrorCode;

//	Calling Win API for Detaching Thread and checking for Success or Failure
		if(CloseHandle(threadID) != 0)
		{
			OSAL_debug(eINFORMATION, "Thread Detached!!");
			return eSUCCESS;
		}

		else
		{
			OSAL_debug(eERROR, "Thread Detach failed!");
			ErrorCode = GetLastError();
			return ((eReturnType)ErrorCode);
		}


#elif __unix__

	//	Calling Win Posix for Detaching Thread and checking for Success or Failure
		int detachRet = pthread_detach(threadID);
		if(detachRet != 0)
		{
			OSAL_debug(eERROR, "Thread Detach failed!");
			return((eReturnType)detachRet);
		}
		else
		{

			OSAL_debug(eINFORMATION, "Thread Detached!");
			return eSUCCESS;
		}

#endif

}


//#####################################################################  COSALSleep  ######################################################
// Name :: COSALSleep
//
// Purpose  :: Time Delay
//
// Input :: number of seconds to be delayed
//
// Output :: Time Delayed
//
// Return Value :: void
//
// Exceptions thrown

void CThread::COSALSleep(COSALSECONDS SecVal)
{
#ifdef _WIN32
	SecVal = SecVal * 1000;
//	Calling Win Sleep with seconds value instead of Mili seconds value
	Sleep(SecVal);

#elif __unix__
//	Calling Posix sleep
	sleep(SecVal);

#endif
}


//##################################################################### Main Function #####################################################

