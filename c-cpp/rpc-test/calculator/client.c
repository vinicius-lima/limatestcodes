#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "calc.h"


void main(int argc, char *argv[]) {
	CLIENT *cliente;
	int *return_value;
	float *f_return_value;
	char *servidor;

	if (argc < 5){
		fprintf(stderr,
				"Usage:\n%s <server> <operation> <number 1> <number 2>\n",
				argv[0]);
		fprintf(stderr, "Example:\n%s localhost add 2 5\n", argv[0]);
		exit (1);
	}
	
	servidor = argv[1];

	// Generating handle to call server.
	if ((cliente=clnt_create(servidor, CALCULATOR, CALCULATORVERS, "tcp"))
			== (CLIENT *) NULL) {
		clnt_pcreateerror(servidor);
		exit(2);
	}
	
	if (strcmp(argv[2], "add") == 0) {

		return_value = add_1(atoi(argv[3]), atoi(argv[4]), cliente);
		if (! *return_value) {
			fprintf(stderr, "error: couldn't add\n");
			exit(1);
		}
		else{
			printf("Return value = %d\n", *return_value);
		}
		exit(0);
		
	}
	else if (strcmp(argv[2], "sub") == 0) {

		return_value = sub_1(atoi(argv[3]), atoi(argv[4]), cliente);
		if (! *return_value) {
			fprintf(stderr, "error: couldn't sub\n");
			exit(1);
		}
		else{
			printf("Return value = %d\n", *return_value);
		}
		exit(0);
		
	}
	else if (strcmp(argv[2], "times") == 0) {

		return_value = times_1(atoi(argv[3]), atoi(argv[4]), cliente);
		if (! *return_value) {
			fprintf(stderr, "error: couldn't times\n");
			exit(1);
		}
		else{
			printf("Return value = %d\n", *return_value);
		}
		exit(0);
		
	}
	else if (strcmp(argv[2], "divide") == 0) {

		f_return_value = divide_1(atoi(argv[3]), atoi(argv[4]), cliente);
		if (! *f_return_value) {
			fprintf(stderr, "error: couldn't divide\n");
			exit(1);
		}
		else{
			printf("Return value = %.3f\n", *f_return_value);
		}
		exit(0);
		
	}
	else{
		fprintf(stderr, "Avaiable operations: add, sub, times, divide\n");
	}

	clnt_destroy(cliente);
}
