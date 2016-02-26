#include <cstdlib>
#include <cstring>
#include <string>
#include <vector>
#include <queue>
#include <utility>

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

int build_matching_machine(const vector<string> &words, char lowest_char = 'a',
                            char highest_char = 'z')
{
    memset(output, 0, sizeof output);
    memset(failure_function, -1, sizeof failure_function);
    memset(goto_function, -1, sizeof goto_function);
    
    int states = 1; // Initially, we just have the 0 state
        
    for(int i = 0; i < words.size(); i++){
        const string &keyword = words[i];
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
    queue<int> q;
    for(int c = 0; c <= highest_char - lowest_char; c++){
        // All nodes s of depth 1 have failure_function[s] = 0
        if(goto_function[0][c] != -1 and goto_function[0][c] != 0){
            failure_function[goto_function[0][c]] = 0;
            q.push(goto_function[0][c]);
        }
    }
    while(q.size()){
        int state = q.front();
        q.pop();
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
                q.push(goto_function[state][c]);
            }
        }
    }

    return states;
}

/**
 * Finds the next state the machine will transition to.
 * Exclusively used in this file.
 * 
 * @param current_state - The current state of the machine. Must be between 0
 * and the number of states - 1, inclusive.
 *
 * @param next_input - The next character that enters into the machine.
 * Should be between lowestChar and highestChar, inclusive.
 *
 * @param lowest_char - Should be the same lowestChar that was passed to
 * "buildMatchingMachine".
 *
 * @return the next state the machine will transition to. This is an integer
 * between 0 and the number of states - 1, inclusive.
 */
static int find_next_state(int current_state, char next_input,
                            char lowest_char = 'a')
{
    int answer = current_state;
    int c = next_input - lowest_char;
    while(goto_function[answer][c] == -1)
        answer = failure_function[answer];
    return goto_function[answer][c];
}

void search_pattern(vector<pair<int,pair<int,int> > > &locations,
                    vector<string> &keywords, string &text,
                    char lowest_char = 'a')
{
    int current_state = 0, size = 0;
    for(int i = 0; i < text.size(); i++){
        current_state = find_next_state(current_state, text[i], lowest_char);
        if(output[current_state] == 0)
            continue; // Nothing new, let's move on to the next character.
        for(int j = 0; j < keywords.size(); j++){
            if (output[current_state] & (1 << j)) { // Matched keywords[j]
                locations.push_back(make_pair(j,
                                    make_pair(i - keywords[j].size() + 1, i)));
            }
        }
    }
}
