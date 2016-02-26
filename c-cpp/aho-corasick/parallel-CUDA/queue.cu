#include <cstdlib>
#include <cstring>

#include "functions.hpp"

#include <iostream>
using namespace std;

queue_t* create_queue()
{
    queue_t *q = (queue_t *)malloc(sizeof(queue_t));
    q->begin = q->end = NULL;
    q->size = 0;
    return q;
}

void push(queue_t *q, data_u *dt)
{
    node_t *aux = (node_t *)malloc(sizeof(node_t));
    aux->dt = dt;
    aux->next = NULL;

    if(q->size == 0){
        q->begin = q->end = aux;
        q->size++;
        return;
    }

    q->end->next = aux;
    q->end = aux;
    q->size++;
}

data_u* pop(queue_t *q)
{
    if(q->size == 0)
        return NULL;

    node_t *aux = q->begin;
    data_u *r = aux->dt;
    q->begin = q->begin->next;
    q->size--;
    free(aux);

    return r;
}

data_u* front(queue_t *q)
{
    if(q->size == 0)
        return NULL;

    return q->begin->dt;
}

data_u* at(queue_t *q, int index)
{
    node_t *aux = q->begin;
    for(int i = 0; i < q->size; i++){
        if(i == index)
            return aux->dt;
        else
            aux = aux->next;
    }
    return NULL;
}
