#include <iostream>
#include <cstdlib>

#include "list.hpp"

using namespace std;

int main(int argc, char** argv){
	list<int> lt;

	lt.push_front(3);
	lt.push_front(2);
	lt.push_front(1);

	do{
		cout << "Front = " << lt.front() << endl;
		lt.pop_front();
	}while(!lt.empty());
	cout << "---------------------\n";

	lt.push_back(3);
	lt.push_back(2);
	lt.push_back(1);

	cout << "Get in position 1 = " << lt.get(1) << "\n---------------------\n";

	do{
		cout << "Back = " << lt.back() << endl;
		lt.pop_back();
	}while(!lt.empty());
	cout << "---------------------\n";

	lt.push_front(4);
	lt.push_front(2);
	lt.push_front(1);
	lt.insert(3, 2);

	for(int i = 0; i < lt.size(); i++)
		cout << "Get in position " << i << " = " << lt.get(i) << endl;
	cout << "---------------------\n";

	lt.remove(0);
	for(int i = 0; i < lt.size(); i++)
		cout << "Get in position " << i << " = " << lt.get(i) << endl;
	cout << "---------------------\n";

	lt.remove(1);
	for(int i = 0; i < lt.size(); i++)
		cout << "Get in position " << i << " = " << lt.get(i) << endl;
	cout << "---------------------\n";

	lt.insert(1, 0);
	lt.insert(3, 2);

    for(int i = 0; i < lt.size(); i++)
		cout << "Get in position " << i << " = " << lt.get(i) << endl;
	cout << "---------------------\n";

	cout << "Index for data equals to 2 = " << lt.linearSearch(2) << endl;
	
	return(0);
}
