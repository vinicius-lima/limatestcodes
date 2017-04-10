#include <iostream>
#include <cstdlib>
#include <list>

#define RED 0
#define BLACK 1

using namespace std;

struct node {
    int key;
    char color;
    struct node *p;
    struct node *left;
    struct node *right;
};
typedef struct node node_t;

struct tree {
    node_t *nil;
    node_t *root;
};
typedef struct tree tree_t;

tree_t* tree_alloc()
{
    tree_t *T = NULL;
    if((T = (tree_t*)malloc(sizeof(tree_t))) == NULL)
        return NULL;

    if((T->nil = (node_t*)malloc(sizeof(node_t))) == NULL){
        free(T);
        return NULL;
    }

    T->nil->key = -1;
    T->nil->color = BLACK;
    T->nil->left = T->nil->right = T->nil->p = T->nil;
    T->root = T->nil;

    return T;
}

void tree_print(tree_t *T)
{
    list<node_t*> nodes;
    node_t *current;
    int layer = 1, count = 0;
    
    nodes.push_back(T->root);
    while(!nodes.empty()){
        current = nodes.front();
        nodes.pop_front();

        if(current->left != T->nil)
            nodes.push_back(current->left);
        if(current->right != T->nil)
            nodes.push_back(current->right);

        cout << "[" << current->key << "|" << current->p->key << "|"
            << ((current->color == RED) ? "R" : "B") << "]";

        count++;
        if(count == layer){
            layer *= 2;
            count = 0;
            cout << endl;
        }
    }
    cout << endl;
}

node_t* node_alloc(int key)
{
    node_t *z = (node_t*)malloc(sizeof(node_t));
    z->key = key;
    return z;
}

//void tree_free(tree_t* T); //Bfs approach

void left_rotate(tree_t *T, node_t *x)
{
    node_t *y = x->right;
    x->right = y->left;
    
    if(y->left != T->nil)
        y->left->p = x;

    y->p = x->p;
    if(x->p == T->nil)
        T->root = y;
    else if(x == x->p->left)
        x->p->left = y;
    else
        x->p->right = y;

    y->left = x;
    x->p = y;
}

void right_rotate(tree_t *T, node_t *y)
{
    node_t *x = y->left;
    y->left = x->right;
    
    if(x->right != T->nil)
        x->right->p = y;

    x->p = y->p;
    if(y->p == T->nil)
        T->root = x;
    else if(y == y->p->right)
        y->p->right = x;
    else
        y->p->left = x;

    x->right = y;
    y->p = x;
}

void rb_insert_fixup(tree_t *T, node_t *z)
{
    node_t *y;
    
    while(z->p->color == RED){
        if(z->p == z->p->p->left){
            y = z->p->p->right;
            if(y->color == RED){
                z->p->color = BLACK;
                y->color = BLACK;
                z->p->p->color = RED;
                z = z->p->p;
            }
            else{
                if(z == z->p->right){
                    z = z->p;
                    left_rotate(T, z);
                }
                z->p->color = BLACK;
                z->p->p->color = RED;
                right_rotate(T, z->p->p);
            }
        }
        else{
            y = z->p->p->left;
            if(y->color == RED){
                z->p->color = BLACK;
                y->color = BLACK;
                z->p->p->color = RED;
                z = z->p->p;
            }
            else{
                if(z == z->p->left){
                    z = z->p;
                    right_rotate(T, z);
                }
                z->p->color = BLACK;
                z->p->p->color = RED;
                left_rotate(T, z->p->p);
            }
        }
    }

    T->root->color = BLACK;
}

void rb_insert(tree_t *T, node_t *z)
{
    node_t *y = T->nil;
    node_t *x = T->root;

    while(x != T->nil){
        y = x;
        if(z->key < x->key)
            x = x->left;
        else
            x = x->right;
    }

    z->p = y;
    if(y == T->nil)
        T->root = z;
    else if(z->key < y->key)
        y->left = z;
    else
        y->right = z;

    z->left = z->right = T->nil;
    z->color = RED;
    rb_insert_fixup(T, z);
}

int main(int argc, char** argv)
{
    tree_t *T = tree_alloc();
    if(T == NULL){
        cout << "Could not allocate tree memory\n";
        exit(1);
    }

    int option = 0;
    int key;
    node_t *z = NULL;
    while(option != 3){
        cout << "Choose an option:\n1 - Insert key\n2 - Print tree\n3 - exit\n"
            << "Option: ";
        cin >> option;
        
        switch(option){
            case 1:
                    cout << "Type a key value: ";
                    cin >> key;
                    z = node_alloc(key);
                    rb_insert(T, z);
                    tree_print(T);
                    break;

            case 2:
                    tree_print(T);
                    break;

            case 3:
                    break;

            default:
                    cout << "Please, choose 1, 2 or 3\n";
        }
    }
    
    return(0);
}
