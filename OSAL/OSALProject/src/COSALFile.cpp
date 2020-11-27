/*
 * COSALFile.cpp
 *
 *  Created on: Apr 27, 2016
 *      Author: F60759B
 */
/*
* File name:: COSALFile.cpp
* Purpose ::  for defining all the functions declared in the Class CFile

* Classes included ::
* Class Name : CFile.
* Purpose : for defining all the functions (APIs) declared
*
*  Development History ::
*  Author Name: Rohan Karnavat (F60759B)
*  Reason
*  Description
*  Date   25/APR/2016
*/

#include <ROHAN_COSAL.h>
#include "COSALFile.h"

#include "COSALThread.h"
#include <iostream>
#include <stdio.h>


using namespace std;






//##################################################################  Fopen  ###############################################################
// Name :: Fopen
//
// Purpose  :: Opening File in specified mode
//
// Input :: File name and mode
//
// Output :: Opened file
//
// Return Value :: FILE*
//
// Exceptions thrown

FILE* CFile::Fopen(const char *fileName, const char *mode)
{
#ifdef _WIN32

//	Calling Win API for opening file
	FILE *filename = fopen(fileName, mode);
	return filename;


#elif __unix__

//	Calling Posix API for opening file
	fopen(fileStream);

#endif
}



//##################################################################  Fclose  ###############################################################
// Name :: Fclose
//
// Purpose  :: Closing File
//
// Input :: file name pointer to stuct of FILE type
//
// Output :: File Closed
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CFile::Fclose(FILE *fileStream)
{
#ifdef _WIN32

//	Calling Win API for Closing file
	int fileCloseRet = fclose(fileStream);

//	Checking for Success or Failure
		if(fileCloseRet != 0)
		{
			//cout<<"Success"<<endl;
			return eSUCCESS;
		}
		else
			return ((eReturnType)fileCloseRet);
#elif __unix__

//	Calling Posix API for Closing file
int fileCloseRet = fclose(fileStream);

//		Checking for Success or Failure
		if(fileCloseRet == 0)
			return eSUCCESS;
		else
			return ((eReturnType)fileCloseRet);
#endif
}



//##################################################################  Fread  ###############################################################
// Name :: Fread
//
// Purpose  :: Reading File
//
// Input :: Void pointer, Size in bytes, number of items to be read, FILE type pointer
//
// Output :: Read file and stored in void pointer location
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CFile::Fread(COSALVOIDPTR strLoc, size_t sizeInBytes, size_t noOfItems, FILE *fileStream)
{
#ifdef _WIN32

//	Calling Win API for Reading file
	size_t fileReadRet = fread(strLoc, sizeInBytes, noOfItems, fileStream);

	//	Checking for Success or Failure
		if(fileReadRet != 0)
			return eSUCCESS;
		else
			return ((eReturnType)fileReadRet);

#elif __unix__

//		Calling Posix API for Reading file
		int fileReadRet = fread(strLoc, sizeInBytes, noOfItems, fileStream);

		//	Checking for Success or Failure
		if(fileReadRet == 0)
			return eSUCCESS;
		else
			return ((eReturnType)fileReadRet);
#endif
}


//##################################################################  Fwrite  ###############################################################
// Name :: Fwrite
//
// Purpose  :: Writing to a file
//
// Input :: Void pointer, Size in bytes, number of items to be read, FILE type pointer
//
// Output :: write to file
//
// Return Value :: eReturnType
//
// Exceptions thrown

eReturnType CFile::Fwrite(COSALCVOIDPTR strLoc, size_t sizeInBytes, size_t noOfItems, FILE *fileStream)
{
#ifdef _WIN32

//	Calling Win API for writing to a file
	size_t fileWriteRet = fwrite(strLoc, sizeInBytes, noOfItems, fileStream);

	//	Checking for Success or Failure
		if(fileWriteRet != 0)
			return eSUCCESS;
		else
			return ((eReturnType)fileWriteRet);

#elif __unix__

		//	Calling Posix API for writing to a file
		int fileWriteRet = fwrite(strLoc, sizeInBytes, noOfItems, fileStream);

	//	Checking for Success or Failure
		if(fileWriteRet == 0)
			return eSUCCESS;
		else
			return ((eReturnType)fileWriteRet);
#endif
}
