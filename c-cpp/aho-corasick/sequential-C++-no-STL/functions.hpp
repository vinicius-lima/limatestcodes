#include <string>

using namespace std;

#ifndef _FUNCTIONS_HPP_
#define _FUNCTIONS_HPP_

/**
 * Max number of states in the matching machine.
 * Should be equal to the sum of the length of all keywords.
 */
const int MAXS = 30;

/**
 * Number of characters in the alphabet.
 */
//const int MAXC = 26; // From 'a' to 'z'. According to ASCII table.
const int MAXC = 94; // From '!' to '~'. According to ASCII table.

typedef struct location
{
	int keyword;
	int begin;
	int end;
} location_t;

typedef union data{
    int state;
    location_t *loc;
} data_u;

typedef struct node
{
    data_u *dt;
    struct node *next;
} node_t;

typedef struct queue
{
    node_t *begin;
    node_t *end;
    int size;
} queue_t;


int build_matching_machine(char **keywords, int keywords_size, char lowest_char,
                            char highest_char);

void search_pattern(queue_t *locations, int keywords_size, int *key_sizes,
                    string &text, char lowest_char);

char** read_keywords(char* file_name, int &keywords_size, int &total_length);

void print_matching_machine(char lowest_char, char highest_char,
                                int total_states);

void print_failure_function(int total_states);

queue_t* create_queue();

void push(queue_t *q, data_u *dt);

data_u* pop(queue_t *q);

data_u* front(queue_t *q);

data_u* at(queue_t *q, int index);

#endif
