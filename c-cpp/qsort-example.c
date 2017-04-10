/* qsort example */
#include <stdio.h>  /* printf, puts */
#include <stdlib.h> /* rand, srand, qsort */
#include <string.h> /* strcpy, strcmp */
#include <time.h>   /* time */

#define STUDENTS 6 /* Total amout of students. */

struct student {
    int id;
    char name[10];
    int age;
};
typedef struct student student_t;

student_t students[STUDENTS]; /* Students array. */

/**
 * Function to fill students array.
 */
void start()
{
    int n;

    srand(time(NULL));
    
    for(n = 0; n < STUDENTS; n++){
        students[n].id = rand() % 100;
        students[n].age = (rand() % 5) + 14;
    }

    /* Names are set in a static way. May be changed to a name generation
     * approach. */
    strcpy(students[0].name, "Marcos");
    strcpy(students[1].name, "Ana");
    strcpy(students[2].name, "Bob");
    strcpy(students[3].name, "Lucas");
    strcpy(students[4].name, "Marcelo");
    strcpy(students[5].name, "Lucia");
}

/**
 * Comparation functions.
 */
int cmp_id(const void * a, const void * b)
{
    student_t *s1 = (student_t*)a;
    student_t *s2 = (student_t*)b;
    
    return (s1->id - s2->id);
}

int cmp_name(const void * a, const void * b)
{
    student_t *s1 = (student_t*)a;
    student_t *s2 = (student_t*)b;
    
    return strcmp(s1->name, s2->name);
}

int cmp_age(const void * a, const void * b)
{
    student_t *s1 = (student_t*)a;
    student_t *s2 = (student_t*)b;
    
    return (s1->age - s2->age);
}

int main (int argc, char** argv)
{
    int n;

    start();

    puts("Generated values:");

    for(n = 0; n < STUDENTS; n++)
        printf("%d | %10s | %d\n", students[n].id, students[n].name, students[n].age);
    puts("---------------");
    
    //-----------------------------------------------------
    puts("Sorted by ID:");
    
    qsort(students, STUDENTS, sizeof(student_t), cmp_id);

    for(n = 0; n < STUDENTS; n++)
        printf("%d | %10s | %d\n", students[n].id, students[n].name, students[n].age);
    puts("---------------");
    
    //-----------------------------------------------------
    puts("Sorted by Name:");
    
    qsort(students, STUDENTS, sizeof(student_t), cmp_name);

    for(n = 0; n < STUDENTS; n++)
        printf("%d | %10s | %d\n", students[n].id, students[n].name, students[n].age);
    puts("---------------");
    
    //-----------------------------------------------------
    puts("Sorted by Age:");
    
    qsort(students, STUDENTS, sizeof(student_t), cmp_age);

    for(n = 0; n < STUDENTS; n++)
        printf("%d | %10s | %d\n", students[n].id, students[n].name, students[n].age);
    puts("---------------");

    return 0;
}
