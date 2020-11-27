/*
 * COSALTimer.cpp
 *
 *  Created on: May 4, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALTimer.cpp
* Purpose ::  for defining all the functions declared in the Class CTimer

* Classes included ::
* Class Name : CTimer.
* Purpose : for defining all the functions (APIs) declared
*
*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   4/MAY/2016
*/

#include "ROHAN_COSAL.h"
#include "COSALTimer.h"
#include<errno.h>
#include <iostream>
#include <signal.h>
#include <time.h>

using namespace std;


//##################################################################  OSAL_debug  ###############################################################
// Name :: timerHandler
//
// Purpose  :: Calling the functions that are assigned to specific timer IDs which need to get called at the event of time elapsed
//				(that is defined by the user)
// Input :: nothing from user. Just default arguments.
//
// Output :: Timer is Created
//
// Return Value :: static void
//
// Exceptions thrown

#ifdef __unix__
static void timerHandler( int sig, siginfo_t *si, void *uc )
	{
	    timer_t *tidp;
	    tidp = (timer_t*)(si->si_value.sival_ptr);
		(tIDFuncMap.find(*tidp)->second)();
	}
#endif


//########################################################  Timer_create  ########################################################
// Name :: Timer_create
//
// Purpose  :: Creating a Timer
//
// Input :: Timer ID, Function to be notified and Elapse time
//
// Output :: Timer Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CTimer::Timer_create(COSALTIMERID timerID, COSALELAPSETIME elapseTime, COSALTIMERFUNC timerFunc)
{
#ifdef  _WIN32
	timerID = ((COSALTIMERID)SetTimer(NULL, 0, elapseTime, timerFunc));

	if(timerID == 0)
	{
		OSAL_debug(eERROR, "Timer Creation Failed...");
		DWORD ErrorCode = GetLastError();
		return ((eReturnType)ErrorCode);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Timer Creation Successful!!");
		return eSUCCESS;
	}


#elif __unix__

	OSAL_debug(eINFORMATION, "Inside Timer_create");
	struct sigevent sSigEve;
	struct itimerspec sITimSpec;
	struct sigaction sSigAct;
	int iSigNo = SIGRTMIN;

//	Set up signal handler.
	sSigAct.sa_flags = SA_SIGINFO;
	sSigAct.sa_sigaction = timerHandler;
	sigemptyset(&sSigAct.sa_mask);
		if (sigaction(iSigNo, &sSigAct, NULL) == -1)
		{
		   OSAL_debug(eERROR, "Timer Creation Failed due to sigaction...");
		}

//	    Set and enable alarm
	sSigEve.sigev_notify = SIGEV_SIGNAL;
	sSigEve.sigev_signo = iSigNo;
	sSigEve.sigev_value.sival_ptr = timerID;
	timer_create(CLOCK_REALTIME, &sSigEve, timerID);

	sITimSpec.it_interval.tv_sec = 0;
	sITimSpec.it_interval.tv_nsec = elapseTime * 1;
	sITimSpec.it_value.tv_sec = 0;
	sITimSpec.it_value.tv_nsec = elapseTime * 1;
	timer_settime(*timerID, 0, &sITimSpec, NULL);

	tIDFuncMap.insert ( std::pair<COSALTIMERID,COSALTIMERFUNC>(*timerID,timerFunc));

	return eSUCCESS;

#endif
}

//########################################################  Timer_delete  ########################################################
// Name :: Timer_delete
//
// Purpose  :: Deleting a Timer
//
// Input :: Timer ID
//
// Output :: Timer Deleted
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CTimer::Timer_delete(COSALTIMERID timerID)
{
#ifdef _WIN32

	if(KillTimer(timerID, 0) == 0)
	{
		OSAL_debug(eERROR, "Timer Deletion failed!");
		DWORD ErrorCode = GetLastError();
		return((eReturnType) ErrorCode);
	}
	else
	{
		OSAL_debug(eINFORMATION, "Timer Deletion Successful!");
		return eSUCCESS;
	}

#elif __unix__

	if(timer_delete(timerID) == 0)
	{
		OSAL_debug(eINFORMATION, "Timer Deletion Successful!");
		return eSUCCESS;
	}
	else if(errno)
	{
		OSAL_debug(eERROR, "Timer Deletion failed!");
		return ((eReturnType)errno)
	}

#endif
}
