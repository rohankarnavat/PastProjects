/*
 * COSALMsgQ.cpp
 *
 *  Created on: Apr 28, 2016
 *      Author: F60759B
 */

#include "COSALMsgQ.h"

#include "COSALThread.h"
#include <iostream>
#include <stdio.h>
#include <windows.h>
#include <mq.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <errno.h>

//#include "ROHAN_COSAL.h"
//#include <mqueue.h>

using namespace std;





//##################################################################  Mq_open  ###############################################################
// Name :: Mq_open
//
// Purpose  :: Creating Message Queue
//
// Input :: Message Queue name and Return Handle
//
// Output :: Message Queue Created
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMsgQ::Mq_open(const char *MQName, COSALMQRETURN MQCreateRet)
{
#ifdef _WIN32
//	HRESULT hr;
//	  PSECURITY_DESCRIPTOR pSecurityDescriptor;
//	  MQQUEUEPROPS * pQueueProps;
//	  const char *MQName = "COMAUQ";
//	  LPDWORD lpdwFormatNameLength;

	// Calling Win API to create MQ
	MQCreateRet = MQCreateQueue(NULL, NULL,(LPWSTR)MQName , 0);

	// return error no. if failed and if not return eSUCCESS
		if(MQCreateRet == MQ_OK)
		{
			OSAL_debug(eINFORMATION, "MQ Created!");
			return eSUCCESS;
		}
		else
		{
			OSAL_debug(eERROR, "MQ Creat failed!");
			return((eReturnType)MQCreateRet);
		}

#elif __unix__

	// Calling Posix API to create MQ
	MQCreateRet = mq_open(MQName, O_RDWR);

		// return error no. if failed and if not return eSUCCESS
	if(MQReturn == -1)
	{
		OSAL_debug(eERROR, "MQ Creat failed!");
		if(errno)
			return((eReturnType)errno);
	}

	else
	{
		OSAL_debug(eINFORMATION, "MQ Created!");
		return eSUCCESS;
	}

#endif
}




//#################################################################################  Mq_unlink  ##################################################
// Name :: Mq_unlink
//
// Purpose  :: Deleting Message Queue
//
// Input :: Message Queue name
//
// Output :: Message Queue Deleted
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMsgQ::Mq_unlink(const char *MQName)
{
#ifdef _WIN32

	// Calling Win API to Delete MQ
	HRESULT MQDeleteRet = MQDeleteQueue((LPWSTR)MQName);

	// return error no. if failed and if not return eSUCCESS
		if(MQDeleteRet == MQ_OK)
		{
			OSAL_debug(eINFORMATION, "MQ Deleted!");
			return eSUCCESS;
		}
		else
		{
			OSAL_debug(eERROR, "MQ Delete failed!");
			return((eReturnType)MQDeleteRet);
		}
#elif __unix__

		// Calling Posix API to Delete MQ
		int MQDeleteRet = mq_unlink(MQName);

		// return error no. if failed and if not return eSUCCESS
			if(MQDeleteRet == -1)
				if(errno)
					return((eReturnType)errno);
			else
				return eSUCCESS;

#endif
}



//#################################################################################  Mq_send  ##################################################
// Name :: Mq_send
//
// Purpose  :: Sending Message on Queue
//
// Input :: Message Queue name
//
// Output :: Message sent
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMsgQ::Mq_send()
{

#ifdef _WIN32
return eSUCCESS;

#elif __unix__

#endif

}




//#################################################################################  Mq_receive  ##################################################
// Name :: Mq_receive
//
// Purpose  :: Receiving Message from Queue
//
// Input ::
//
// Output :: Message received
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CMsgQ::Mq_receive()
{
	return eSUCCESS;
#ifdef _WIN32


#elif __unix__

#endif

}


