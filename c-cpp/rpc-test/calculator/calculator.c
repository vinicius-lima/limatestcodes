#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "calc.h"

int *add_1_svc (int a, int b, struct svc_req *req) {
	static int result = 0;
	result = a + b;
	return (&result);
}

int *sub_1_svc (int a, int b, struct svc_req *req) {
	static int result = 0;
	result = a - b;
	return (&result);
}

int *times_1_svc (int a, int b, struct svc_req *req) {
	static int result = 0;
	result = a * b;
	return (&result);
}

float *divide_1_svc (int a, int b, struct svc_req *req) {
	static float result = 0;
	result = (float)a / b;
	return (&result);
}
