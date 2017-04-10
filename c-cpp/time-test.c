#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main()
{
	time_t t0 = time(0);
	system("sleep 5");
	time_t t1 = time(0);
	double datetime_diff_s = difftime(t1, t0);
	double datetime_diff_ms = difftime(t1, t0) * 1000.;

	/*clock_t c0 = clock();
	system("sleep 5");
	clock_t c1 = clock();
	double runtime_diff_ms = (c1 - c0) * 1000. / CLOCKS_PER_SEC;*/

	/*printf("Time difference = %.3f\nClock difference = %f\n\n", datetime_diff_ms, runtime_diff_ms);
	printf("Time difference = %e.3\nClock difference = %e\n\n", datetime_diff_ms, runtime_diff_ms);*/

	printf("Milisecond Time difference = %.3f\nSecond Time difference = %.3f\n\n", datetime_diff_ms, datetime_diff_s);
	printf("Milisecond Time difference = %e\nSecond Time difference = %e\n\n", datetime_diff_ms, datetime_diff_s);
	
	return(0);
}
