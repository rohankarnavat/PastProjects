/*
 * COSALTimer.h
 *
 *  Created on: May 4, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALTimer.h
* Purpose ::  for declaring Functions related to Timer in the Class CTimer

* Classes included ::
* Class Name : CTimer
* Purpose : for declaring Functions related to Timer

*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   4/MAY/2016
*/

#include "ROHAN_COSAL.h"
#include <signal.h>
#include <time.h>
#include <map>




#ifndef HEADERS_COSALTIMER_H_
#define HEADERS_COSALTIMER_H_


#ifdef _WIN32
typedef HWND COSALTIMERID;

#elif __unix__
typedef timer_t COSALTIMERID;

#endif

#ifdef _WIN32
typedef UINT COSALELAPSETIME;

#elif __unix__
typedef int COSALELAPSETIME;

#endif

#ifdef _WIN32
typedef TIMERPROC COSALTIMERFUNC;

#elif __unix__
typedef static void COSALTIMERFUNC;

#endif

//###################################################################  Class CTimer  ###############################################################
// Class Name	:: CTimer
//
// Purpose ::  class CTimer contains all the Timer related APIs
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

class CTimer {
private:
	std::map<COSALTIMERID, COSALTIMERFUNC> tIDFuncMap;
public:
	eReturnType Timer_create(COSALTIMERID, COSALELAPSETIME, COSALTIMERFUNC);
	eReturnType Timer_delete(COSALTIMERID);
	//eReturnType Timer_settime(COSALTIMERID);
};

#endif /* HEADERS_COSALTIMER_H_ */
