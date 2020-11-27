/*
 * UseAPI.cpp
 *
 *  Created on: Apr 18, 2016
 *      Author: F60759B
 */
#include <ROHAN_COSAL.h>
//#include"ROHANEnums.h"

using namespace std;

void OSAL_debug1(msgType eType, const char*str)
{
	//return;
	//if(eType == eERROR || eCHECK)
	{
		cout << str <<endl;
		Sleep(2);
	}

}


#ifdef _WIN32
	void ThreadFunction(LPVOID *)
	{
		OSAL_debug1(eINFORMATION, "Hi, This is my first thread.");
		//return 0;
	}
#elif __unix__

	void *ThreadFunction(void *param)
	{
		OSAL_debug1(eINFORMATION, "Hi, This is my first thread.");
		return 0;
	}
#endif

//##################################################################  Main Function  #####################################################
#include"COSALMutex.h"
#include<stdio.h>
#include"COSALFile.h"
int main()
{
	//		size_t stacksize; // unsigned int
	//		pthread_attr_t attr; // typedef struct pthread_attr_t_ * pthread_attr_t;
	//		SIZE_T stacksizewin; // unsigned long


			COSALSTACKSIZE stacksize = (16 * 1024 * 1024);
//			COSALCONDTYPE cond1;
			eReturnType eStatus, eStatus1, estatusFile;
			char buffer[100];
			char str[] = "Hi, this is the written file";
			const char *filname = "Rohan.txt";
			const char *mdw = "w";
			const char *mdr = "r";


			COSALTHREADID id1;
			COSALMUTEX mutex1 = 0;
			CThread t1;
			CMutex m1;
			CFile f1;
//			CCondition c1;


			eStatus = t1.Pthread_create(stacksize, &ThreadFunction, &id1);
			m1.Pthread_mutex_init(&mutex1);
//			c1.Pthread_cond_wait(&cond1, &mutex1);
//			c1.Pthread_cond_signal(&cond1);
			m1.Pthread_mutex_destroy(&mutex1);
			eStatus1 = t1.Pthread_cancel(id1);
			t1.Pthread_detach(id1);

			FILE *fileptr = f1.Fopen(filname, mdw);
			f1.Fwrite(str, sizeof(char), sizeof(str), fileptr);
			fclose(fileptr);
			fileptr = f1.Fopen(filname, mdr);
			f1.Fread(buffer, sizeof(char), sizeof(str), fileptr);
			printf("\n%s", buffer);
			estatusFile = f1.Fclose(fileptr);
			cout<<eStatus<< eStatus1<< estatusFile<<endl;

			return 0;
}



