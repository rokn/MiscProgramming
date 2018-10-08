#include <iostream>
using namespace std;

void print(int* arr, int n) {
	for (int i = 0; i < n; ++i) {
		cout << arr[i];
	}
	cout << endl;
}

int main() {
	int set[] = {1, 3, 6, 4};
	int setLength = 4;
	int zeroes = 1;
	int ones = 3;
	int subsetLength = zeroes+ones;
	int* res = new int[subsetLength];

	for (int i = 0; i < setLength; ++i) {
	}

	return 0;
}
