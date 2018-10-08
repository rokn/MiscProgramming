#include "sorting.h"

const int insertion_size = 2;

void selection_sort(int *arr, int n) {
	if(n <= 1) return;

	for (int i = 0; i < n - 1; ++i) {
		int min = i;
		for(int j = i + 1; j < n; j++) {
			if (arr[j] < arr[min]) {
				min = j;
			}
		}
		if(min != i) {
			int temp = arr[min];
			arr[min] = arr[i];
			arr[i] = temp;
		}
	}
}
