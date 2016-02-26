#include <vector>
#include <utility>
#include <string>

using namespace std;

#ifndef _FUNCTIONS_HPP_
#define _FUNCTIONS_HPP_

/**
 * Max number of states in the matching machine.
 * Should be equal to the sum of the length of all keywords.
 */
const int MAXS = 22;

/**
 * Number of characters in the alphabet.
 */
//const int MAXC = 26; // From 'a' to 'z'. According to ASCII table.
const int MAXC = 94; // From '!' to '~'. According to ASCII table.

/**
 * Builds the string matching machine.
 *
 * @param words - Vector of keywords. The index of each keyword is important:
 *        "out[state] & (1 << i)" is > 0 if we just found word[i] in the text.
 * 
 * @param lowest_char - The lowest char in the alphabet. Defaults to 'a'.
 * 
 * @param highest_char - The highest char in the alphabet. Defaults to 'z'.
 * "highest_char - lowest_char" must be <= MAXC, otherwise we will access
 * the goto_function matrix outside its bounds.
 * 
 * @return the number of states that the new machine has. States are numbered 0
 * up to the return value - 1, inclusive.
 */
int build_matching_machine(const vector<string> &words, char lowest_char,
                            char highest_char);

/**
 * Uses the string matching machine in oder to find the keywords occurrences
 * in a text string.
 *
 * @param locations - The positions of all occurrences of any keyword. This
 * structure will be filled inside this function.
 *
 * @param text - The text where keywords will be searched.
 * 
 * @param lowest_char - Should be the same lowest_char that was passed to
 * "build_matching_machine".
 */
void search_pattern(vector<pair<int,pair<int,int> > > &locations,
                    vector<string> &keywords, string &text, char lowest_char);

/**
 * Read the keywords from an input file.
 *
 * @param keywords - Vector of keywords. This vector will be filled inside
 * this function.
 * 
 * @param file_name - The input file's name.
 * 
 * @return The sum of all keywords' length. Returns 0 if none keywords were
 * read.
 */
int read_keywords(vector<string> &keywords, char* file_name);

/**
 * Prints the string matching machine.
 *
 * @param lowest_char - The lowest char in the alphabet. Defaults to 'a'.
 * 
 * @param highest_char - The highest char in the alphabet. Defaults to 'z'.
 * "highest_char - lowest_char" must be <= MAXC, otherwise we will access
 * the gotoFunction matrix outside its bounds.
 *
 * @param total_states - The total number of states created in
 * "build_matching_machine".
 */
void print_matching_machine(char lowest_char, char highest_char,
                                int total_states);

/**
 * Prints the failure function.
 *
 * @param total_states - The total number of states created in
 * "build_matching_machine".
 */
void print_failure_function(int total_states);

#endif
