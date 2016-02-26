#include <iostream>
#include <fstream>
#include <string>
#include <cstdlib>
#include <cstring>

#include <cuda.h>
#include <cuda_runtime.h>

#include "functions.hpp"

//#define MAX_FILE_SIZE 1073741824 // Value in bytes. (1GB)
#define MAX_FILE_SIZE 1048576 // Value in bytes. (1MB)
#define MAX_LINE_SIZE 200
#define MAX_LINES 	  1000
#define MAX_LOCATIONS 500

using namespace std;

extern const int MAXS;
extern const int MAXC;
extern int failure_function[MAXS];
extern int goto_function[MAXS][MAXC];
extern int output[MAXS];

int main(int argc, char** argv)
{
    if(argc < 3){
        cout << "Usage:\n" << argv[0] << " <keywords file name>"
        << " <text file name>" << endl;
        cout << "Example:\n" << argv[0] << " keywords.txt input_text.txt"
        << endl;
        exit(1);
    }

    char **keywords;
    int keywords_size, total_length;

    cout << "Reading keywords..." << endl;
    keywords = read_keywords(argv[1], keywords_size, total_length);

    cout << "Keywords:" << endl;
    for(int i = 0; i < keywords_size; i++)
        cout << keywords[i] << endl;

    cout << "Building goto function..." << endl;
    //int total_states = build_matching_machine(keywords, keywords_size,'a', 'z');
    int total_states = build_matching_machine(keywords, keywords_size, '!', '~');
    if(total_states > 1)
        cout << "Goto built!" << endl;
    else
        cout << "Could not build goto function" << endl;

    cout << "\nMatching machine:\n";
    //print_matching_machine('a', 'z', total_states);
    print_matching_machine('!', '~', total_states);

    cout << "\nFailure function:\n";
    print_failure_function(total_states);

    fstream file;
    string text;
    char full_text[MAX_FILE_SIZE], new_line[MAX_LINE_SIZE];
    int key_sizes[keywords_size];

    // Reading input text file.
    for(int i = 0; i < keywords_size; i++)
        key_sizes[i] = strlen(keywords[i]);

    file.open(argv[2], fstream::in | fstream::out);

    int idx = 1, pos, line_pos[MAX_LINES], num_lines;
    full_text[0] = '\0';
	pos = num_lines = 0;
	line_pos[0] = pos;
    while(!file.fail() && !file.eof() && file.is_open()){
        getline(file, text);
        if(text.size() == 0)
        	continue;
        text.copy(new_line, text.size(), 0);
        new_line[text.size()] = '\0';
        strcat(full_text, new_line);
        pos += text.size() + 1;
        full_text[pos - 1] = '\n';
        full_text[pos] = '\0';
        line_pos[idx++] = pos;
        num_lines++;
    }

    file.close();

    // Making preparations for kernel launch.
    dim3 grid, block;
	//block.x = 1024;
	//grid.x = 15;
	block.x = 128;
	grid.x = 1;

	//location_t locations[MAX_LOCATIONS], *d_locations;
	location_t *locations, *d_locations;
	locations = (location_t *)malloc(MAX_LOCATIONS * sizeof(location_t));
	int *d_key_sizes, *d_line_pos;
	int *d_goto_function, *d_failure_function, *d_output;
	char *d_text;

	cudaMalloc((void**)&d_locations, MAX_LOCATIONS * sizeof(location_t));
	cudaMalloc((void**)&d_key_sizes, keywords_size * sizeof(int));
	cudaMalloc((void**)&d_line_pos, MAX_LINES * sizeof(int));
	cudaMalloc((void**)&d_goto_function, MAXS*MAXC * sizeof(int));
	cudaMalloc((void**)&d_failure_function, MAXS * sizeof(int));
	cudaMalloc((void**)&d_output, MAXS * sizeof(int));
	cudaMalloc((void**)&d_text, MAX_FILE_SIZE * sizeof(char));

	cudaMemcpy(d_key_sizes, key_sizes, keywords_size * sizeof(int),
                cudaMemcpyHostToDevice);
	cudaMemcpy(d_line_pos, line_pos, num_lines * sizeof(int),
                cudaMemcpyHostToDevice);
	cudaMemcpy(d_failure_function, failure_function, total_states * sizeof(int),
                cudaMemcpyHostToDevice);
	cudaMemcpy(d_output, output, MAXS * sizeof(int), cudaMemcpyHostToDevice);
	cudaMemcpy(d_text, full_text, strlen(full_text) * sizeof(char),
                cudaMemcpyHostToDevice);

	for(int i = 0; i < MAXS; i++)
		cudaMemcpy(&d_goto_function[i*MAXC], goto_function[i],
                    MAXC * sizeof(char), cudaMemcpyHostToDevice);

    cout << "\nSearching occurrences of keywords in the text...\n";
	//search_pattern(locations, keywords_size, key_sizes, text, 'a');
    search_pattern<<<grid, block>>>(d_locations, keywords_size, d_key_sizes,
                                    d_text, num_lines, d_line_pos,
                                    d_goto_function, d_failure_function,
                                    d_output, '!');

    cudaMemcpy(locations, d_locations, MAX_LOCATIONS * sizeof(location_t),
                cudaMemcpyDeviceToHost);

	/*int line = 1;

	if(locations->size == 0){
		line++;
		continue;
	}

	cout << "\nOccurrences in line " << line++ << endl;*/
    cout << "\nOccurrences:\n";
	for(int i = 0; i < 10 && locations[i].keyword < keywords_size; i++){
		cout << "Keyword " << keywords[locations[i].keyword] << " appears from "
		<< locations[i].begin << " to " << locations[i].end << endl;
	}

    for(int i = 0; i < keywords_size; i++)
        free(keywords[i]);
    free(keywords);
    free(locations);

    return(0);
}
