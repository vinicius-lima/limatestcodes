#include <iostream>
#include <fstream>
#include <string>
#include <cstdlib>
#include <cstring>

#include "functions.hpp"

using namespace std;

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

    /*cout << "Reading keywords..." << endl;
    read_keywords(argv[1], keywords_size, total_length);
    cout << "Sum of all keywords length = " << total_length << endl;
    exit(0);*/

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

    queue_t *locations = create_queue();
    fstream file;
    string text;
    int line = 1, key_sizes[keywords_size];

    for(int i = 0; i < keywords_size; i++)
        key_sizes[i] = strlen(keywords[i]);

    file.open(argv[2], fstream::in | fstream::out);

    cout << "\nSearching occurrences of keywords in the text...\n";
    while(!file.fail() && !file.eof() && file.is_open()){
        getline(file, text);
        
        //search_pattern(locations, keywords_size, key_sizes, text, 'a');
        search_pattern(locations, keywords_size, key_sizes, text, '!');

        if(locations->size == 0){
            line++;
            continue;
        }
        
        cout << "\nOccurrences in line " << line++ << endl;
        data_u *dt = NULL;
        while(locations->size > 0){
            dt = pop(locations);
            cout << "Keyword " << keywords[dt->loc->keyword] << " appears from "
            << dt->loc->begin << " to " << dt->loc->end << endl;
            free(dt->loc);
            free(dt);
        }
    }
    
    for(int i = 0; i < keywords_size; i++)
        free(keywords[i]);
    free(keywords);
    free(locations);

    file.close();
    
    return(0);
}
