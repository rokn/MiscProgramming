#include <iostream>
#include "sorting.h"
#define SORT(arr, n) merge_sort(arr, n)

using namespace std;

void print(int *arr, int n) {
	for (int i = 0; i < n; ++i) {
		cout << arr[i] << endl;
	}
}

int main() {
	int n;
	int *arr;

	cin >> n;
	arr = new int[n];

	for (int i = 0; i < n; ++i) {
		cin >> arr[i];
	}

	SORT(arr, n);
	print(arr, n);
}
