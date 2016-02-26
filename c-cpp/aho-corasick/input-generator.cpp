#include <iostream>
#include <fstream>
//#include <sstream>
#include <string>
#include <cstdlib>
#include <cstring>
#include <ctime>

#define MAX_FILE_SIZE 1073741824 // Value in bytes. (1GB)
#define MAX_LINE_SIZE 200

using namespace std;

int main(int argc, char** argv)
{
    if(argc != 5){
        cout << "Usage:\n" << argv[0] << " <keywords file> <exit file name>"
            << " <exit file size> <maximum occurrences> \n";
        cout << "Example:\n" << argv[0] << " keywords.txt input_text.txt 1024 10"
            << endl;
    }

    char *keyword_file_name = argv[1], *exit_file_name = argv[2];
    int exit_file_size = atoi(argv[3]);
    int max_occurrences = atoi(argv[4]);

    if(exit_file_size > MAX_FILE_SIZE){
        cout << "Maximum exit file size is " << MAX_FILE_SIZE << " bytes\n";
        exit(1);
    }

    fstream keyword_file;
    int num_keywords = 0;

    keyword_file.open(keyword_file_name, fstream::in | fstream::out);
    if(!keyword_file.fail() && !keyword_file.eof() && keyword_file.is_open())
        keyword_file >> num_keywords;

    string keywords[num_keywords];
    int keywords_size = 0, occurrences[num_keywords];

    for(int i = 0; !keyword_file.fail() && !keyword_file.eof()
            && keyword_file.is_open() && i < num_keywords; i++){
        keyword_file >> keywords[i];
        keywords_size += max_occurrences * keywords[i].size();
        occurrences[i] = 0;
    }
    keyword_file.close();

    if(keywords_size > exit_file_size){
        cout << "Maximum occurrences times each keyword's length is greater"
            << " than the exit file size\n";
        exit(1);
    }

    srand(time(NULL));

    char line[MAX_LINE_SIZE + 1], src[MAX_LINE_SIZE];

    for(int i = 0; i < MAX_LINE_SIZE; i++)
        src[i] = (char)((rand() % 26) + 97);
    src[MAX_LINE_SIZE - 1] = '\0';

    fstream exit_file;
    exit_file.open(exit_file_name, fstream::out);
    //stringstream line;

    int keyword, pos, all_occurrences = num_keywords * max_occurrences;
    long size = 0;

    while(all_occurrences > 0){
        do{
            keyword = rand() % num_keywords;
        }while(occurrences[keyword] == max_occurrences);

        pos = (rand() % MAX_LINE_SIZE) - keywords[keyword].size();
        pos = (pos < 0) ? 0 : pos;
        memcpy(line, src, pos * sizeof(char));
        memcpy(&line[pos], keywords[keyword].c_str(),
                keywords[keyword].size() * sizeof(char));
        pos += keywords[keyword].size();
        memcpy(&line[pos], &src[pos], (MAX_LINE_SIZE - pos) * sizeof(char));
        line[MAX_LINE_SIZE] = '\0';

        exit_file << line << "\n";
        all_occurrences--;
        size += MAX_LINE_SIZE + 1;
    }

    memcpy(line, src, MAX_LINE_SIZE * sizeof(char));
    while(size < exit_file_size){
        exit_file << line << "\n";
        size += MAX_LINE_SIZE + 1;
    }
    
    exit_file.close();
    
    return(0);
}
