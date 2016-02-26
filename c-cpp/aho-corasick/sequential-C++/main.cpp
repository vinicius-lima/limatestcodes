#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstdlib>
#include <utility>

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

    vector<string> keywords;

    cout << "Reading keywords..." << endl;
    read_keywords(keywords, argv[1]);

    /*cout << "Reading keywords..." << endl;
    int total_length = read_keywords(keywords, argv[1]);
    cout << "Sum of all keywords length = " << total_length << endl;
    exit(0);*/

    cout << "Keywords:" << endl;
    vector<string>::iterator str = keywords.begin();
    for( ; str != keywords.end(); ++str)
        cout << *str << endl;

    cout << "Building goto function..." << endl;
    //int total_states = build_matching_machine(keywords, 'a', 'z');
    int total_states = build_matching_machine(keywords, '!', '~');
    if(total_states > 1)
        cout << "Goto built!" << endl;
    else
        cout << "Could not build goto function" << endl;

    cout << "\nMatching machine:\n";
    //print_matching_machine('a', 'z', total_states);
    print_matching_machine('!', '~', total_states);

    cout << "\nFailure function:\n";
    print_failure_function(total_states);

    vector<pair<int,pair<int,int> > > locations;
    fstream file;
    string text;
    int line = 1;

    file.open(argv[2], fstream::in | fstream::out);

    cout << "\nSearching occurrences of keywords in the text...\n";
    while(!file.fail() && !file.eof() && file.is_open()){
        getline(file, text);
        
        locations.clear();
        //search_pattern(locations, keywords, text, 'a');
        search_pattern(locations, keywords, text, '!');

        if(locations.size() == 0){
            line++;
            continue;
        }
        
        cout << "\nOccurrences in line " << line++ << endl;
        vector<pair<int,pair<int,int> > >::iterator it = locations.begin();
        for( ; it != locations.end(); it++){
            cout << "Keyword " << keywords[it->first] << " appears from "
            << it->second.first << " to " << it->second.second << endl;
        }
    }

    file.close();
    
    return(0);
}
