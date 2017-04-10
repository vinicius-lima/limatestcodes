#include <iostream>
#include <cstdlib>
#include <string>

using namespace std;

int main(int argc, char** argv)
{
    string browser_message;
    
    while(true) {
        cin >> browser_message;
        cout << browser_message << endl;
        browser_message.clear();
    }
    
    return(0);
}
