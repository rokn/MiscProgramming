#include <iostream>
#include "ring.h"
using namespace std;

int main() {
	int n = 7;
	int* ring = fillRing(n);
	cout << add(0, 4, n) << endl;
	cout << add(3, 4, n) << endl;
	cout << add(2, 6, n) << endl;
	cout << add(4, 5, n) << endl;
	cout << subract(2, 5, n) << endl;
	cout << subract(1, 3, n) << endl;
	cout << subract(4, 5, n) << endl;
	cout << multiply(2, 6, n) << endl;
	cout << multiply(4, 4, n) << endl;
	cout << multiply(5, 5, n) << endl;

	int **pairs = fillPairs(n);

	int i = 0;
	while(pairs[0][i] != -1) {
		cout << pairs[0][i] << " * " << pairs[1][i] << " % " << n << " = 1" << endl;
		i++;
	}

	cout << "Divide: " << divide(2, 3, n, pairs) << endl;
	cout << "Divide: " << divide(5, 6, n, pairs) << endl;
	cout << "Divide: " << divide(3, 4, n, pairs) << endl;

	cout << "Power first: "  <<  powerFirst (3, 100, 7) << endl;
	cout << "Power second: " <<  powerSecond(3, 100, 7) << endl;

	cout << "Power first: "  <<  powerFirst (3, 64, 7) << endl;
	cout << "Power second: " <<  powerSecond(3, 64, 7) << endl;

	delete[] ring;
	delete[] pairs[0];
	delete[] pairs[1];
	delete[] pairs;
	return 0;
}
