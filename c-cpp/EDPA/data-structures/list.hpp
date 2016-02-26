#include <cstdlib>

#ifndef _LIST_H
#define _LIST_H

template <typename T>
class list {
	private:
		struct node {
			T data;
			node *next;
			node *previous;
		};
		node begin;
		node end;
		int numElements;

	public:
		list<T>();
		~list<T>();
		void push_front(T const&);
		void pop_front();
		T front();
		void push_back(T const&);
		void pop_back();
		T back();
		bool insert(T const&, int);
		T remove(int);
		int size();
		bool empty();
		T get(int);
		int linearSearch(T const&);
};

template <typename T>
list<T>::list(){
	begin.next = &end;
	end.previous = &begin;
	begin.previous = end.next = NULL;
	numElements = 0;
}

template <typename T>
list<T>::~list(){
	while(numElements > 0)
		pop_front();
}

template <typename T>
void list<T>::push_front(T const& data){
	node *aux = new node;
	aux->data = data;
	aux->next = begin.next;
	aux->previous = &begin;
	begin.next->previous = aux;
	begin.next = aux;
	numElements++;
}

template <typename T>
void list<T>::pop_front(){
	if(numElements == 0)
		return;
	node *aux = begin.next;
	begin.next = aux->next;
	aux->next->previous = &begin;
	delete aux;
	numElements--;
}

template <typename T>
T list<T>::front(){
	if(numElements > 0)
		return begin.next->data;
	return 0;
}

template <typename T>
void list<T>::push_back(T const& data){
	node *aux = new node;
	aux->data = data;
	aux->next = &end;
	aux->previous = end.previous;
	end.previous->next = aux;
	end.previous = aux;
	numElements++;
}

template <typename T>
void list<T>::pop_back(){
	if(numElements == 0)
		return;
	node *aux = end.previous;
	end.previous = aux->previous;
	aux->previous->next = &end;
	delete aux;
	numElements--;
}

template <typename T>
T list<T>::back(){
	if(numElements > 0)
		return end.previous->data;
	return 0;
}

template <typename T>
bool list<T>::insert(T const& data, int index){
	if(empty() && index == 0){
		push_front(data);
		return true;
	}
	else if(index > numElements || index < 0 || (empty() && index != 0)){
		return false;
	}

	node *aux = begin.next;
	node *new_node = new node;

	new_node->data = data;
	for(int i = 0; i < index; aux = aux->next, i++);

	new_node->next = aux;
	new_node->previous = aux->previous;
	new_node->previous->next = new_node;
	aux->previous = new_node;
	numElements++;

	return true;
}

template <typename T>
T list<T>::remove(int index){
	if(numElements == 0 || index >= numElements)
		return 0;

	node *aux = begin.next;
	for(int i = 0; i < index; aux = aux->next, i++);
	T data = aux->data;

	aux->next->previous = aux->previous;
	aux->previous->next = aux->next;
	delete aux;
	numElements--;

	return data;
}

template <typename T>
int list<T>::size(){
	return numElements;
}

template <typename T>
bool list<T>::empty(){
	return numElements == 0;
}

template <typename T>
T list<T>::get(int index){
	node *aux = begin.next;
	for(int i = 0; aux != &end; aux = aux->next, i++){
		if(i == index)
			return aux->data;
	}
	return 0;
}

template <typename T>
int list<T>::linearSearch(T const& data){
	node *aux = end.previous;
	for(int index = numElements; index; index--, aux = aux->previous)
		if(aux->data == data)
			return (index - 1);

	return -1;
}

#endif
