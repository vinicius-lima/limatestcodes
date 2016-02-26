#include <iostream>
#include <fstream>
#include <cstdlib>
#include <cstring>
#include <string>

#include "functions.hpp"

using namespace std;

extern const int MAXS;
extern const int MAXC;
extern int failure_function[MAXS];
extern int goto_function[MAXS][MAXC];
extern int output[MAXS];

char** read_keywords(char* file_name, int &keywords_size, int &total_length)
{
    fstream file;
    string in;
    char **keywords;
    int i = 0;
    
    file.open(file_name, fstream::in | fstream::out);

    if(!file.fail() && !file.eof() && file.is_open())
        file >> keywords_size;
    keywords = (char **)malloc(keywords_size * sizeof(char *));
    
    while(!file.fail() && !file.eof() && file.is_open() && i < keywords_size){
        file >> in;
        //getline(file, in);
        keywords[i] = (char *)malloc((in.size() + 1) * sizeof(char));
        in.copy(keywords[i], in.size(), 0);
        keywords[i][in.size()] = '\0';
        i++;
        total_length += in.size();
        in.clear();
    }
    
    file.close();

    return keywords;
}

void print_matching_machine(char lowest_char = 'a', char highest_char = 'z',
                            int total_states = MAXS)
{
    int maxc = highest_char - lowest_char;
    for(int s = 0; s < total_states && s < MAXS; s++){
        cout << s << ": ";
        for(int c = 0; c < maxc; c++){
            if(goto_function[s][c] != -1 && goto_function[s][c] != 0){
                cout << (char)(lowest_char + c) << " --> "
                << goto_function[s][c] << " | ";
            }
        }
        cout << endl;
    }
}

void print_failure_function(int total_states)
{
    for(int s = 0; s < total_states; s++)
        cout << s << ":" << failure_function[s] << " | ";
    cout << endl;
}
