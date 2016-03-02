#include <iostream>
#include <fstream>
//#include <sstream>
#include <string>
#include <cstdlib>
#include <cstring>
#include <ctime>

#define MEGA_BYTE 1048576 // Value in bytes. (1 MB)
#define MAX_FILE_SIZE 10485760 // Value in bytes. (10 MB)
#define MAX_LINE_SIZE 100

using namespace std;

int main(int argc, char** argv)
{
    if(argc != 3){
        cout << "Usage:\n" << argv[0] << " <exit file name> <exit file size in MB>\n";
        cout << "Example:\n" << argv[0] << " input-data.dat 1" << endl;
    }

    char *exit_file_name = argv[1];
    int exit_file_size = atoi(argv[2]);
    float exit_file_size_f = 0.0;
    if(exit_file_size == 0){
        exit_file_size_f = (float)atof(argv[2]);
        exit_file_size = exit_file_size_f * MEGA_BYTE;
        cout << "exit_file_size = " << exit_file_size << endl;
    }
    else {
        exit_file_size *= MEGA_BYTE;
        cout << "exit_file_size = " << exit_file_size << endl;
    }

    if(exit_file_size > MAX_FILE_SIZE){
        cout << "Maximum exit file size is " << MAX_FILE_SIZE << " bytes\n";
        exit(1);
    }

    fstream exit_file;
    exit_file.open(exit_file_name, fstream::out);
    //stringstream line;

    for(int size = 0, l = 0; size < exit_file_size; size++){
        exit_file << "a";
        l++;
        if(l == 99){
            exit_file << endl;
            l = 0;
            size++;
        }
    }
    exit_file.close();
    
    return(0);
}
