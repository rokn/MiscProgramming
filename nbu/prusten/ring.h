#ifndef RING_H
#define RING_H

int* fillRing(int n);
int add(int left, int right, int n);
int subract(int left, int right, int n);
int multiply(int left, int right, int n);
int** fillPairs(int n);
int findReciprocal(int numb, int** pairs);
int divide(int left, int right, int n, int** pairs);
int powerFirst(int numb, int pow, int n);
int powerSecond(int numb, int pow, int n);
bool isField(int n);

#endif // RING_H
