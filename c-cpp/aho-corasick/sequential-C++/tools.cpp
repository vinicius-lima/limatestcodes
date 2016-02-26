#include <iostream>
#include <fstream>
#include <vector>
#include <string>

#include "functions.hpp"

using namespace std;

extern const int MAXS;
extern const int MAXC;
extern int failure_function[MAXS];
extern int goto_function[MAXS][MAXC];

int read_keywords(vector<string> &keywords, char* file_name)
{
    fstream file;
    string in;
    int total_length = 0;
    
    file.open(file_name, fstream::in | fstream::out);
    while(!file.fail() && !file.eof() && file.is_open()){
        file >> in;
        //getline(file, in);
        keywords.push_back(in);
        total_length += in.size();
        in.clear();
    }
    keywords.pop_back();
    
    file.close();

    return total_length;
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
