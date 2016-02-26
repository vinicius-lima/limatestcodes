#include <cstdlib>
#include <cstring>
#include <string>
#include <cuda.h>
#include <cuda_runtime.h>
#include "functions.hpp"

using namespace std;

/**
 * Output for each state, as a bitwise mask.
 * Bit i in this mask is on if the keyword with index i appears when the machine
 * enters this state.
 */
int output[MAXS];

// Used internally in the algorithm.
int failure_function[MAXS]; // Failure function.
int goto_function[MAXS][MAXC]; // Goto function, -1 if fail.

__host__ int build_matching_machine(char **keywords, int keywords_size,
                            char lowest_char = 'a', char highest_char = 'z')
{
    memset(output, 0, sizeof output);
    memset(failure_function, -1, sizeof failure_function);
    memset(goto_function, -1, sizeof goto_function);

    int states = 1; // Initially, we just have the 0 state

    for(int i = 0; i < keywords_size; i++){
        const string keyword (keywords[i]);
        int current_state = 0;
        for(int j = 0; j < keyword.size(); j++){
            int c = keyword[j] - lowest_char;
            if(goto_function[current_state][c] == -1){ // Allocate a new node
                goto_function[current_state][c] = states++;
            }
            current_state = goto_function[current_state][c];
        }
        // There's a match of keywords[i] at node current_state.
        output[current_state] |= (1 << i);
    }

    // State 0 should have an outgoing edge for all characters.
    for(int c = 0; c < MAXC; c++){
        if(goto_function[0][c] == -1){
            goto_function[0][c] = 0;
        }
    }

    // Building the failure function
    queue_t *q = create_queue();
    data_u *dt = NULL;
    for(int c = 0; c <= highest_char - lowest_char; c++){
        // All nodes s of depth 1 have failure_function[s] = 0
        if(goto_function[0][c] != -1 and goto_function[0][c] != 0){
            failure_function[goto_function[0][c]] = 0;
            dt = (data_u *)malloc(sizeof(data_u));
            dt->state = goto_function[0][c];
            push(q, dt);
        }
    }
    while(q->size){
        dt = pop(q);
        int state = dt->state;
        free(dt);
        for(int c = 0; c <= highest_char - lowest_char; c++){
            if(goto_function[state][c] != -1){
                int failure = failure_function[state];
                while(goto_function[failure][c] == -1){
                    failure = failure_function[failure];
                }
                failure = goto_function[failure][c];
                failure_function[goto_function[state][c]] = failure;
                // Merge out values
                output[goto_function[state][c]] |= output[failure];
                dt = (data_u *)malloc(sizeof(data_u));
                dt->state = goto_function[state][c];
                push(q, dt);
            }
        }
    }
    free(q);

    return states;
}

__device__ int find_next_state(int current_state, char next_input,
		int* d_goto_function, int* d_failure_function, char lowest_char = 'a')
{
    int answer = current_state;
    int c = next_input - lowest_char;
    /*while(d_goto_function[answer][c] == -1)
        answer = d_failure_function[answer];*/
    while(d_goto_function[answer*MAXC + c] == -1)
            answer = d_failure_function[answer];
    //return d_goto_function[answer][c];
    return d_goto_function[answer*MAXC + c];
}

__global__ void search_pattern(location_t *d_locations, int keywords_size,
                                int *d_key_sizes, char *d_text, int num_lines,
                                int *d_line_pos, int* d_goto_function,
                                int* d_failure_function, int* d_output,
                                char lowest_char = 'a')
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    int current_state = 0, loc = 0; // Ver concorrencia de threads no d_locations

    if(idx < num_lines){
    	for(int i = d_line_pos[idx]; d_text[i] != '\n'; i++){
			current_state = find_next_state(current_state, d_text[i],
                                            d_goto_function, d_failure_function,
                                            lowest_char);
			if(d_output[current_state] == 0)
				continue; // Nothing new, let's move on to the next character.
			for(int j = 0; j < keywords_size; j++){
				if (d_output[current_state] & (1 << j)) { // Matched keywords[j]
					d_locations[loc].keyword = j;
					d_locations[loc].begin = i - d_key_sizes[j] + 1;
					d_locations[loc].end = i;
					loc++;
				}
			}
		}
    }
}
